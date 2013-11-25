package org.osiam.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;
import org.osiam.web.util.HttpHeader;
import org.osiam.web.util.MailSender;
import org.osiam.web.util.RegistrationExtensionUrnProvider;
import org.osiam.web.util.ResourceServerUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for change E-Mail purpose.
 * User: Jochen Todea
 * Date: 20.11.13
 * Time: 11:29
 * Created: with Intellij IDEA
 */
@Controller
@RequestMapping(value = "/email")
public class ChangeEmailController {

    private static final Logger LOGGER = Logger.getLogger(ChangeEmailController.class.getName());

    private ObjectMapper mapper;

    @Inject
    private RegistrationExtensionUrnProvider registrationExtensionUrnProvider;
    @Inject
    private HttpClientHelper httpClient;
    @Inject
    private ResourceServerUriBuilder resourceServerUriBuilder;
    @Inject
    private MailSender mailSender;

    @Inject
    private ServletContext context;

    /* Extension configuration */
    @Value("${osiam.confirm.email.token.field}")
    private String confirmationTokenField;
    @Value("${osiam.temp.email.field}")
    private String tempEmail;

    /* Change email configuration */
    @Value("${osiam.web.emailchange.linkprefix}")
    private String emailChangeLinkPrefix;
    @Value("${osiam.web.emailchange.from}")
    private String emailChangeMailFrom;
    @Value("${osiam.web.emailchange.subject}")
    private String emailChangeMailSubject;
    @Value("${osiam.web.emailchange.content.path}")
    private String pathToEmailContent;

    /* Info mail configuration */
    @Value("${osiam.web.emailchange-info.subject}")
    private String emailChangeInfoMailSubject;
    @Value("${osiam.web.emailchange-info.content.path}")
    private String pathToEmailInfoContent;


    public ChangeEmailController() {
        mapper = new ObjectMapper();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, null, null))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);
    }

    /**
     * Saving the new E-Mail temporary, generating confirmation token and sending an E-Mail to the old registered address.
     * @param authorization Authorization header with HTTP Bearer authorization and a valid access token
     * @param userId The user id for the user whom email address should be changed
     * @param newEmailValue The new email address value
     * @return The HTTP status code
     * @throws IOException
     * @throws MessagingException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/change", produces = "application/json")
    public ResponseEntity<String> change(@RequestHeader final String authorization, @RequestParam final String userId,
                                         @RequestParam final String newEmailValue) throws IOException, MessagingException {

        String uri = resourceServerUriBuilder.buildUriWithUserId(userId);

        //get user by user id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authorization);
        if (result.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by ID!");
            return new ResponseEntity<>("{\"error\":\"Problems retrieving user by ID!\"}", HttpStatus.valueOf(result.getStatusCode()));
        }

        //generate confirmation token
        String confirmationToken = UUID.randomUUID().toString();

        //building the user for update with confirm token and temp email as extensions
        String updateUser = buildUserForUpdateAsString(newEmailValue, result, confirmationToken);

        //update the user
        HttpClientRequestResult updateUserResult = httpClient.executeHttpPatch(uri, updateUser, HttpHeader.AUTHORIZATION, authorization);
        if (updateUserResult.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems updating user with extensions!");
            return new ResponseEntity<>("{\"error\":\"Problems updating user with extensions!\"}", HttpStatus.valueOf(result.getStatusCode()));
        }

        //send email to the new address with confirmation token and user id
        User savedUser = mapper.readValue(updateUserResult.getBody(), User.class);

        return sendingConfirmationMailToNewAddress(newEmailValue, confirmationToken, savedUser);
    }

    /**
     * Validating the confirm token and saving the new email value as primary email if the validation was successful.
     * @param authorization Authorization header with HTTP Bearer authorization and a valid access token
     * @param userId The user id for the user whom email address should be changed
     * @param confirmToken The previously generated confirmation token from the confirmation email
     * @return The HTTP status code and the updated user if successful
     */
    @RequestMapping(method = RequestMethod.POST, value = "/confirm", produces = "application/json")
    public ResponseEntity<String> confirm(@RequestHeader final String authorization, @RequestParam final String userId,
                                          @RequestParam final String confirmToken) throws IOException, MessagingException {

        if (confirmToken.equals("")) {
            LOGGER.log(Level.WARNING, "Confirmation token miss match!");
            return new ResponseEntity<>("{\"error\":\"No ongoing email change!\"}", HttpStatus.UNAUTHORIZED);
        }

        String uri = resourceServerUriBuilder.buildUriWithUserId(userId);

        //get user by user id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authorization);
        if (result.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by ID!");
            return new ResponseEntity<>("{\"error\":\"Problems retrieving user by ID!\"}", HttpStatus.valueOf(result.getStatusCode()));
        }

        User user = mapper.readValue(result.getBody(), User.class);

        // get user extensions for validation purpose
        Extension extension = user.getExtension(registrationExtensionUrnProvider.getExtensionUrn());
        String existingConfirmToken = extension.getField(this.confirmationTokenField);

        if (!existingConfirmToken.equals(confirmToken)) {
            LOGGER.log(Level.WARNING, "Confirmation token miss match!");
            return new ResponseEntity<>("{\"error\":\"No ongoing email change!\"}", HttpStatus.FORBIDDEN);
        }

        // get new email
        String newEmail = extension.getField(this.tempEmail);

        // get old email address
        String oldEmail = mailSender.extractPrimaryEmail(user);

        //replacing only the old primary, non primary are still valid
        List<MultiValuedAttribute> emails = replaceOldPrimaryMail(newEmail, user.getEmails());

        String updateUserAsString = getUserAsStringWithUpdatedExtensionsAndEmails(extension, user, emails);

        //update the user
        HttpClientRequestResult updateUserResult = httpClient.executeHttpPatch(uri, updateUserAsString, HttpHeader.AUTHORIZATION, authorization);
        if (updateUserResult.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems updating user with extensions!");
            return new ResponseEntity<>("{\"error\":\"Problems updating user with extensions!\"}", HttpStatus.valueOf(updateUserResult.getStatusCode()));
        }

        // Send info mail
        return sendingInfoMailToOldAddress(oldEmail, updateUserResult.getBody());
    }


    /*---- private methods for change endpoint ----*/

    private String buildUserForUpdateAsString(String newEmailValue, HttpClientRequestResult result, String confirmationToken) throws IOException {

        //create extension Map
        Map<String, String> extMap = new HashMap<>();

        //add the confirmation token to extension field
        extMap.put(confirmationTokenField, confirmationToken);

        //add the new email value to the tempMail extension field
        extMap.put(tempEmail, newEmailValue);

        //Add extension Map to Extensions
        Extension extension = new Extension(registrationExtensionUrnProvider.getExtensionUrn(), extMap);

        User user = mapper.readValue(result.getBody(), User.class);

        //add extensions to user
        User updateUser = new User.Builder(user).addExtension(registrationExtensionUrnProvider.getExtensionUrn(), extension).build();

        String updateUserAsString = mapper.writeValueAsString(updateUser);

        return updateUserAsString;
    }

    private ResponseEntity<String> sendingConfirmationMailToNewAddress(String newEmailAddress, String confirmationToken, User user) throws IOException, MessagingException {

        //build the string for confirmation link
        StringBuilder activateURL = new StringBuilder(emailChangeLinkPrefix);
        activateURL.append("userId=").append(user.getId());
        activateURL.append("&confirmToken=").append(confirmationToken);

        //build the Map with the link for replacement
        Map<String, String> vars = new HashMap<>();
        vars.put("$EMAILCHANGEURL", activateURL.toString());

        //get mail content as stream and check failure if file is not present
        InputStream mailContentStream =
                mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-content.txt", pathToEmailContent,
                        context);

        if (mailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>("{\"error\":\"Cant open registermail-content.txt on classpath! Please configure!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //send the mail
        mailSender.sendMail(emailChangeMailFrom, newEmailAddress, emailChangeMailSubject, mailContentStream, vars);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*---- private methods for confirm endpoint ----*/

    private String getUserAsStringWithUpdatedExtensionsAndEmails(Extension extension, User user, List<MultiValuedAttribute> emails) throws JsonProcessingException {
        // remove extension values after already successful validation.
        extension.setField(this.confirmationTokenField, "");
        extension.setField(this.tempEmail, "");

        //add mails and extensions to user
        User updateUser = new User.Builder(user).setEmails(emails).addExtension(registrationExtensionUrnProvider.
                getExtensionUrn(), extension).build();

        return mapper.writeValueAsString(updateUser);
    }

    private List<MultiValuedAttribute> replaceOldPrimaryMail(String newEmail, List<MultiValuedAttribute> emails) {

        List<MultiValuedAttribute> updatedEmailList = new ArrayList<>();

        // add new primary email address
        updatedEmailList.add(new MultiValuedAttribute.Builder().
                setValue(newEmail).
                setPrimary(true).
                build());

        //add only non primary mails to new list and remove all primary entries
        for (MultiValuedAttribute mail : emails) {
            if (!mail.isPrimary()) {
                updatedEmailList.add(mail);
            }
        }

        return updatedEmailList;
    }

    private ResponseEntity<String> sendingInfoMailToOldAddress(String oldEmailAddress, String user) throws IOException, MessagingException {

        //get mail content as stream and check failure if file is not present
        InputStream mailContentStream =
                mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-info.txt", pathToEmailInfoContent,
                        context);

        if (mailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>("{\"error\":\"Cant open registermail-content.txt on classpath! Please configure!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        mailSender.sendMail(emailChangeMailFrom, oldEmailAddress, emailChangeInfoMailSubject, mailContentStream, null);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}