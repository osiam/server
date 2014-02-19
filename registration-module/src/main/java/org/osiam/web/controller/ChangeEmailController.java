/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.helper.ObjectMapperWithExtensionConfig;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.User;
import org.osiam.web.util.AccessTokenInformationProvider;
import org.osiam.web.util.HttpHeader;
import org.osiam.web.util.MailSenderBean;
import org.osiam.web.util.RegistrationExtensionUrnProvider;
import org.osiam.web.util.RegistrationHelper;
import org.osiam.web.util.ResourceServerUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;

/**
 * Controller for change E-Mail process.
 */
@Controller
@RequestMapping(value = "/email")
public class ChangeEmailController {

    private static final Logger LOGGER = Logger.getLogger(ChangeEmailController.class.getName());

    @Inject
    private HttpClientHelper httpClient;
    @Inject
    private ResourceServerUriBuilder resourceServerUriBuilder;
    @Inject
    private RegistrationExtensionUrnProvider registrationExtensionUrnProvider;
    @Inject
    private ObjectMapperWithExtensionConfig mapper;
    @Inject
    private MailSenderBean mailSender;
    @Inject
    private AccessTokenInformationProvider accessTokenInformationProvider;

    @Inject
    private ServletContext context;

    /* Extension configuration */
    @Value("${osiam.temp.email.field}")
    private String tempEmail;
    @Value("${osiam.confirm.email.token.field}")
    private String confirmationTokenField;

    /* Change email configuration */
    @Value("${osiam.web.emailchange.subject}")
    private String emailChangeMailSubject;
    @Value("${osiam.web.emailchange.content.path}")
    private String pathToEmailContent;
    @Value("${osiam.web.emailchange.linkprefix}")
    private String emailChangeLinkPrefix;
    @Value("${osiam.web.emailchange.from}")
    private String emailChangeMailFrom;

    /* Info mail configuration */
    @Value("${osiam.web.emailchange-info.subject}")
    private String emailChangeInfoMailSubject;
    @Value("${osiam.web.emailchange-info.content.path}")
    private String pathToEmailInfoContent;

    /* URI for the change email call from JavaScript */
    @Value("${osiam.web.email.url}")
    private String clientEmailChangeUri;

    // css and js libs
    @Value("${osiam.html.dependencies.bootstrap}")
    private String bootStrapLib;
    @Value("${osiam.html.dependencies.angular}")
    private String angularLib;
    @Value("${osiam.html.dependencies.jquery}")
    private String jqueryLib;

    /**
     * Generates a HTTP form with the fields for change email purpose.
     */
    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response) throws IOException {
        // load the html file as stream
        InputStream inputStream = context.getResourceAsStream("/WEB-INF/registration/change_email.html");
        String htmlContent = IOUtils.toString(inputStream, "UTF-8");
        // replacing the url
        String replacedAll = htmlContent.replace("$CHANGELINK", clientEmailChangeUri);

        // replace all lib links
        replacedAll = replacedAll.replace("$BOOTSTRAP", bootStrapLib);
        replacedAll = replacedAll.replace("$ANGULAR", angularLib);
        replacedAll = replacedAll.replace("$JQUERY", jqueryLib);

        InputStream in = IOUtils.toInputStream(replacedAll);
        // set the content type
        response.setContentType("text/html");
        IOUtils.copy(in, response.getOutputStream());
    }

    /**
     * Saving the new E-Mail temporary, generating confirmation token and sending an E-Mail to the old registered
     * address.
     * 
     * @param authorization
     *        Authorization header with HTTP Bearer authorization and a valid access token
     * @param newEmailValue
     *        The new email address value
     * @return The HTTP status code
     * @throws IOException
     * @throws MessagingException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/change", produces = "application/json")
    public ResponseEntity<String> change(@RequestHeader final String authorization,
            @RequestParam final String newEmailValue) throws IOException, MessagingException {
        String userId;

        // catch exception due to problems getting information from the access token, possible that the token was
        // invalid
        try {
            userId = accessTokenInformationProvider.getUserIdFromToken(authorization);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        String uri = resourceServerUriBuilder.buildUsersUriWithUserId(userId);

        // generate confirmation token
        String confirmationToken = UUID.randomUUID().toString();

        // building the user for update with confirm token and temp email as extensions
        String updateUser = buildUserForUpdateAsString(newEmailValue, confirmationToken);

        // update the user
        HttpClientRequestResult updateUserResult = httpClient.executeHttpPatch(uri, updateUser,
                HttpHeader.AUTHORIZATION, authorization);

        if (updateUserResult.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems updating user with extensions!");
            return new ResponseEntity<>("{\"error\":\"Problems updating user with extensions!\"}",
                    HttpStatus.valueOf(updateUserResult.getStatusCode()));
        }

        // send email to the new address with confirmation token and user id
        User savedUser = mapper.readValue(updateUserResult.getBody(), User.class);

        return sendingConfirmationMailToNewAddress(newEmailValue, confirmationToken, savedUser);
    }

    /**
     * Validating the confirm token and saving the new email value as primary email if the validation was successful.
     * 
     * @param authorization
     *        Authorization header with HTTP Bearer authorization and a valid access token
     * @param userId
     *        The user id for the user whom email address should be changed
     * @param confirmToken
     *        The previously generated confirmation token from the confirmation email
     * @return The HTTP status code and the updated user if successful
     */
    @RequestMapping(method = RequestMethod.POST, value = "/confirm", produces = "application/json")
    public ResponseEntity<String> confirm(@RequestHeader final String authorization, @RequestParam final String userId,
            @RequestParam final String confirmToken) throws IOException, MessagingException {

        if (confirmToken.equals("")) {
            LOGGER.log(Level.WARNING, "Confirmation token miss match!");
            return new ResponseEntity<>("{\"error\":\"No ongoing email change!\"}", HttpStatus.UNAUTHORIZED);
        }

        String uri = resourceServerUriBuilder.buildUsersUriWithUserId(userId);

        HttpClientRequestResult result = httpClient.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authorization);
        if (result.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by ID!");
            return new ResponseEntity<>("{\"error\":\"Problems retrieving user by ID!\"}", HttpStatus.valueOf(result
                    .getStatusCode()));
        }

        User user = mapper.readValue(result.getBody(), User.class);

        Extension extension = user.getExtension(registrationExtensionUrnProvider.getExtensionUrn());
        String existingConfirmToken = extension.getField(confirmationTokenField, ExtensionFieldType.STRING);

        if (!existingConfirmToken.equals(confirmToken)) {
            LOGGER.log(Level.WARNING, "Confirmation token mismatch!");
            return new ResponseEntity<>("{\"error\":\"No ongoing email change!\"}", HttpStatus.FORBIDDEN);
        }

        String newEmail = extension.getField(tempEmail, ExtensionFieldType.STRING);
        Optional<String> oldEmail = RegistrationHelper.extractSendToEmail(user);

        List<Email> emails = replaceOldPrimaryMail(newEmail, user.getEmails());

        String updateUserAsString = getUserAsStringWithUpdatedExtensionsAndEmails(extension, emails);

        // update the user
        HttpClientRequestResult updateUserResult = httpClient.executeHttpPatch(uri, updateUserAsString,
                HttpHeader.AUTHORIZATION, authorization);

        if (updateUserResult.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems updating user with extensions!");
            return new ResponseEntity<>("{\"error\":\"Problems updating user with extensions!\"}",
                    HttpStatus.valueOf(updateUserResult.getStatusCode()));
        }

        // Send info mail
        return sendingInfoMailToOldAddress(oldEmail.get(), updateUserResult.getBody());
    }

    private String buildUserForUpdateAsString(String newEmailValue, String confirmationToken) throws IOException {

        // add the confirmation token to the extension and add the new email value to the tempMail extension field
        Extension extension = new Extension(registrationExtensionUrnProvider.getExtensionUrn());
        extension.addOrUpdateField(confirmationTokenField, confirmationToken);
        extension.addOrUpdateField(tempEmail, newEmailValue);

        // add extensions to user
        User updateUser = new User.Builder().addExtension(extension).build();

        return mapper.writeValueAsString(updateUser);
    }

    private ResponseEntity<String> sendingConfirmationMailToNewAddress(String newEmailAddress,
            String confirmationToken, User user) throws IOException, MessagingException {

        // build the string for confirmation link
        StringBuilder activateURL = new StringBuilder(emailChangeLinkPrefix);
        activateURL.append("userId=").append(user.getId());
        activateURL.append("&confirmToken=").append(confirmationToken);

        // build the Map with the link for replacement
        Map<String, String> vars = new HashMap<>();
        vars.put("$EMAILCHANGEURL", activateURL.toString());

        // get mail content as stream and check failure if file is not present
        InputStream mailContentStream =
                mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-content.txt", pathToEmailContent,
                        context);

        if (mailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>(
                    "{\"error\":\"Cant open registermail-content.txt on classpath! Please configure!\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // send the mail
        mailSender.sendMail(emailChangeMailFrom, newEmailAddress, emailChangeMailSubject, mailContentStream, vars);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getUserAsStringWithUpdatedExtensionsAndEmails(Extension extension, List<Email> emails)
            throws JsonProcessingException {
        // remove extension values after already successful validation.
        Set<String> deletionSet = new HashSet<String>();
        deletionSet.add(extension.getUrn() + "." + confirmationTokenField);
        deletionSet.add(extension.getUrn() + "." + tempEmail);
        Meta meta = new Meta.Builder().setAttributes(deletionSet).build();
        // add mails and extensions to user
        User updateUser = new User.Builder().setEmails(emails).setMeta(meta).build();

        return mapper.writeValueAsString(updateUser);
    }

    private List<Email> replaceOldPrimaryMail(String newEmail, List<Email> emails) {

        List<Email> updatedEmailList = new ArrayList<>();

        // add new primary email address
        updatedEmailList.add(new Email.Builder()
                .setValue(newEmail)
                .setPrimary(true)
                .build());

        // add only non primary mails to new list and remove all primary entries
        for (Email mail : emails) {
            if (mail.isPrimary()) {
                updatedEmailList.add(new Email.Builder().setType(mail.getType())
                        .setPrimary(mail.isPrimary())
                        .setValue(mail.getValue()).setOperation("delete").build());
            } else {
                updatedEmailList.add(mail);
            }
        }

        return updatedEmailList;
    }

    private ResponseEntity<String> sendingInfoMailToOldAddress(String oldEmailAddress, String user) throws IOException,
            MessagingException {

        // get mail content as stream and check failure if file is not present
        InputStream mailContentStream =
                mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-info.txt",
                        pathToEmailInfoContent,
                        context);

        if (mailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>(
                    "{\"error\":\"Cant open registermail-content.txt on classpath! Please configure!\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        mailSender.sendMail(emailChangeMailFrom, oldEmailAddress, emailChangeInfoMailSubject, mailContentStream, null);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}