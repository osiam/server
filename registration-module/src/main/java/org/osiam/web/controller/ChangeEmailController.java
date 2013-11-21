package org.osiam.web.controller;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.User;
import org.osiam.web.util.MailSender;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private static final Logger LOGGER = Logger.getLogger(LostPasswordController.class.getName());

    private static final String AUTHORIZATION = "Authorization";

    private HttpClientHelper httpClient = new HttpClientHelper();
    private ObjectMapper mapper;

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    private static final String RESOURCE_SERVER_URI = "/osiam-resource-server/Users";

    @Value("${osiam.internal.scim.extension.urn}")
    private String internalScimExtensionUrn;

    @Value("${osiam.confirm.email.token.field}")
    private String confirmationTokenField;

    @Value("${osiam.temp.email.field}")
    private String tempEmail;

    private MailSender mailSender = new MailSender();

    @Value("${osiam.web.emailchange.linkprefix}")
    private String emailChangeLinkPrefix;
    @Value("${osiam.web.emailchange.from}")
    private String emailChangeMailFrom;
    @Value("${osiam.web.emailchange.subject}")
    private String emailChangeMailSubject;

    @Inject
    ServletContext context;


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
    @RequestMapping(method = RequestMethod.POST, value = "/change")
    public ResponseEntity<String> change(@RequestHeader final String authorization, @RequestParam final String userId,
                                         @RequestParam final String newEmailValue) throws IOException, MessagingException {

        String uri = httpScheme + "://" + serverHost + ":" + serverPort + RESOURCE_SERVER_URI + "/" + userId;

        //get user by user id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, AUTHORIZATION, authorization);
        if (result.getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by ID!");
            return new ResponseEntity<>(HttpStatus.valueOf(result.getStatusCode()));
        }

        //generate confirmation token
        String confirmationToken = UUID.randomUUID().toString();

        //building the user for update with confirm token and temp email as extensions
        User updateUser = buildUserForUpdate(newEmailValue, result, confirmationToken);

        //update the user
        String updateUserAsString = mapper.writeValueAsString(updateUser);
        HttpClientRequestResult updateUserResult = httpClient.executeHttpPatch(uri, updateUserAsString, AUTHORIZATION, authorization);
        if (updateUserResult.getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Problems updating user with extensions!");
            return new ResponseEntity<>(HttpStatus.valueOf(result.getStatusCode()));
        }

        //send email to the new address with confirmation token and user id
        User savedUser = mapper.readValue(updateUserResult.getBody(), User.class);
        sendingConfirmationMailToNewAddress(newEmailValue, confirmationToken, savedUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private User buildUserForUpdate(String newEmailValue, HttpClientRequestResult result, String confirmationToken) throws IOException {

        //create extension Map
        Map<String, String> extMap = new HashMap<>();

        //add the confirmation token to extension field
        extMap.put(confirmationTokenField, confirmationToken);

        //add the new email value to the tempMail extension field
        extMap.put(tempEmail, newEmailValue);

        //Add extension Map to Extensions
        Extension extension = new Extension(internalScimExtensionUrn, extMap);

        //add extensions to user
        User user = mapper.readValue(result.getBody(), User.class);
        return new User.Builder(user).addExtension(internalScimExtensionUrn, extension).build();
    }

    private void sendingConfirmationMailToNewAddress(String newEmailAddress, String confirmationToken, User user) throws IOException, MessagingException {

        //build the string for confirmation link
        StringBuilder activateURL = new StringBuilder(emailChangeLinkPrefix);
        activateURL.append("userId=").append(user.getId());
        activateURL.append("&confirmToken=").append(confirmationToken);

        //build the Map with the link for replacement
        Map<String, String> vars = new HashMap<>();
        vars.put("$EMAILCHANGEURL", activateURL.toString());

        //get mail content as stream and check failure if file is not present
        InputStream mailContentStream = context.getResourceAsStream("/WEB-INF/registration/emailchange-content.txt");

        //send the mail
        mailSender.sendMail(emailChangeMailFrom, newEmailAddress, emailChangeMailSubject, mailContentStream, vars);
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
                                          @RequestParam final String confirmToken) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}