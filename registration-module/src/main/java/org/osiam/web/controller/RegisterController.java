package org.osiam.web.controller;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;
import org.osiam.web.util.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller to handel the registration purpose
 */
@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    private static final String AUTHORIZATION = "Authorization";

    private HttpClientHelper httpClient;
    private ObjectMapper mapper;

    @Value("${osiam.web.registermail.from}")
    private String registermailFrom;
    @Value("${osiam.web.registermail.subject}")
    private String registermailSubject;
    @Value("${osiam.web.registermail.linkprefix}")
    private String registermailLinkPrefix;
    @Value("${osiam.web.registermail.content.path}")
    private String pathToContentFile;

    @Value("${osiam.web.register.url}")
    private String clientRegistrationUri;

    @Value("${osiam.server.port}")
    private int serverPort;
    @Value("${osiam.server.host}")
    private String serverHost;
    @Value("${osiam.server.http.scheme}")
    private String httpScheme;

    private static final String RESOURCE_SERVER_URI = "/osiam-resource-server/Users";

    @Value("${osiam.internal.scim.extension.urn}")
    private String internalScimExtensionUrn;

    @Value("${osiam.activation.token.field}")
    private String activationTokenField;


    @Inject
    ServletContext context;

    private MailSender mailSender = new MailSender();

    public RegisterController() {
        httpClient = new HttpClientHelper();

        mapper = new ObjectMapper();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, null, null))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);
    }

    /**
     * Generates a form with all needed fields for registration purpose.
     */
    @RequestMapping(method=RequestMethod.GET)
    public void index(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        InputStream inputStream = context.getResourceAsStream("/WEB-INF/registration/registration.html");

        String htmlContent = IOUtils.toString(inputStream, "UTF-8");
        String replacedHtmlContent = htmlContent.replace("$REGISTERLINK", clientRegistrationUri);
        InputStream in = IOUtils.toInputStream(replacedHtmlContent);

        IOUtils.copy(in, response.getOutputStream());
    }

    /**
     * Creates a new User.
     *
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to
     * the registered email-address.
     *
     * @param authorization a valid access token
     * @return the saved user and HTTP.OK (200) for successful creation, otherwise only the HTTP status
     * @throws IOException
     * @throws MessagingException
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String authorization, @RequestBody String user) throws IOException, MessagingException {

        User parsedUser = mapper.readValue(user, User.class);
        String primaryEmail = mailSender.extractPrimaryEmail(parsedUser);
        if (primaryEmail == null) {
            LOGGER.log(Level.WARNING, "No primary email found!");
            return new ResponseEntity<>("{\"error\":\"No primary email found!\"}", HttpStatus.BAD_REQUEST);
        }
        // generate Activation Token
        String activationToken = UUID.randomUUID().toString();
        parsedUser = createUserForRegistration(parsedUser, activationToken);

        // Save user
        HttpClientRequestResult saveUserResponse = saveUser(parsedUser, authorization);
        if (saveUserResponse.getStatusCode() != 201) {
            LOGGER.log(Level.WARNING, "Problems creating user for registration");
            return new ResponseEntity<>("{\"error\":\"Problems creating user for registration\"}", HttpStatus.valueOf(saveUserResponse.getStatusCode()));
        }

        String savedUserId = mapper.readValue(saveUserResponse.getBody(), User.class).getId();
        return sendActivationMail(primaryEmail, savedUserId, activationToken, saveUserResponse);
    }

    private User createUserForRegistration(User parsedUser, String activationToken) {

        // Add Extension with the token to the user and set active to false
        User.Builder builder = new User.Builder(parsedUser);
        builder.setActive(false);

        //Add user to role 'USER' to be able to login afterwards
        List<MultiValuedAttribute> roles = new ArrayList<>();
        roles.add(new MultiValuedAttribute.Builder().setValue("USER").build());
        builder.setRoles(roles);

        Map<String,String> fields = new HashMap<>();
        fields.put("activationToken", activationToken);
        builder.addExtension(internalScimExtensionUrn, new Extension(internalScimExtensionUrn, fields));

        return builder.build();
    }

    private ResponseEntity<String> sendActivationMail(String toAddress, String userId, String activationToken,
                                  HttpClientRequestResult saveUserResponse) throws MessagingException, IOException {

        InputStream registerMailContentStream =
                mailSender.getEmailContentAsStream("/WEB-INF/registration/registermail-content.txt", pathToContentFile,
                        context);

        if (registerMailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>("{\"error\":\"Cant open registermail-content.txt on classpath! Please configure!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        StringBuilder activateURL = new StringBuilder(registermailLinkPrefix);
        activateURL.append("userId=").append(userId);
        activateURL.append("&activationToken=").append(activationToken);

        Map<String, String> mailVars = new HashMap<>();
        mailVars.put("$REGISTERLINK", activateURL.toString());

        mailSender.sendMail(registermailFrom, toAddress, registermailSubject, registerMailContentStream, mailVars);
        return new ResponseEntity<>(saveUserResponse.getBody(), HttpStatus.OK);
    }

    private HttpClientRequestResult saveUser(User userToSave, String authorization) throws IOException {
        String userAsString = mapper.writeValueAsString(userToSave);
        String createUserUri = httpScheme + "://" + serverHost + ":" + serverPort + RESOURCE_SERVER_URI;
        return httpClient.executeHttpPost(createUserUri, userAsString, AUTHORIZATION, authorization);
    }

    /**
     * Activates a previously registered user.
     *
     * After activation E-Mail arrived the activation link will point to this URI.
     *
     * @param authorization an valid OAuth2 token
     * @param userId the id of the registered user
     * @param activationToken the user's activation token, send by E-Mail
     *
     * @return HTTP status, HTTP.OK (200) for a valid activation
     */
    @RequestMapping(value = "/activate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity activate(@RequestHeader final String authorization,
                                   @RequestParam final String userId, @RequestParam final String activationToken) throws IOException {

        if (activationToken.equals("")) {
            LOGGER.log(Level.WARNING, "Activation token miss match!");
            return new ResponseEntity<>("{\"error\":\"Activation token miss match!\"}", HttpStatus.UNAUTHORIZED);
        }

        String uri = httpScheme + "://" + serverHost + ":" + serverPort + RESOURCE_SERVER_URI + "/" + userId;

        //get user by his id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, AUTHORIZATION, authorization);

        if (result.getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by his ID!");
            return new ResponseEntity<>("{\"error\":\"Problems retrieving user by his ID!\"}", HttpStatus.valueOf(result.getStatusCode()));
        }

        //get extension field to check activation token validity
        User userForActivation = mapper.readValue(result.getBody(), User.class);
        Extension extension = userForActivation.getExtension(internalScimExtensionUrn);
        String activationTokenFieldValue = extension.getField(activationTokenField);

        if (!activationTokenFieldValue.equals(activationToken)) {
            LOGGER.log(Level.WARNING, "Activation token miss match!");
            return new ResponseEntity<>("{\"error\":\"Activation token miss match!\"}", HttpStatus.UNAUTHORIZED);
        }

        //validation successful -> delete token and activate user
        extension.setField(activationTokenField, "");
        User updateUser = new User.Builder(userForActivation).setActive(true).build();

        //update user
        HttpClientRequestResult requestResult = httpClient.executeHttpPut(uri,
                mapper.writeValueAsString(updateUser), AUTHORIZATION, authorization);

        if (requestResult.getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Updating user with extensions failed!");
            return new ResponseEntity<>("{\"error\":\"Updating user with extensions failed!\"}", HttpStatus.valueOf(requestResult.getStatusCode()));
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}