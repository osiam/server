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
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String authorization, @RequestBody String body) {
        ResponseEntity<String> res;
        try {
            User parsedUser = mapper.readValue(body, User.class);
            String primaryEmail = mailSender.extractPrimaryEmail(parsedUser);
            if (primaryEmail == null) {
                LOGGER.log(Level.WARNING, "No primary email found!");
                res = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                // generate Activation Token
                String activationToken = UUID.randomUUID().toString();
                parsedUser = createUserForRegistration(parsedUser, activationToken);

                // Save user
                HttpClientRequestResult saveUserResponse = saveUser(parsedUser, authorization);
                if (saveUserResponse.getStatusCode() != 201) {
                    res = new ResponseEntity<>(HttpStatus.valueOf(saveUserResponse.getStatusCode()));
                } else {
                    res = sendActivationMail(primaryEmail, parsedUser, activationToken, saveUserResponse);
                }
            }
        } catch (IOException | MessagingException e) {
            LOGGER.log(Level.SEVERE, "Internal error", e);
            res = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
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

    private ResponseEntity<String> sendActivationMail(String toAddress, User parsedUser, String activationToken,
                                  HttpClientRequestResult saveUserResponse) throws MessagingException, IOException {

        // Mailcontent with $REGISTERLINK as placeholder
        InputStream registerMailContentStream = context.getResourceAsStream("/WEB-INF/registration/registermail-content.txt");

        if (registerMailContentStream == null) {
            LOGGER.log(Level.SEVERE, "Cant open registermail-content.txt on classpath! Please configure!");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        StringBuilder activateURL = new StringBuilder(registermailLinkPrefix);
        activateURL.append("userId=").append(parsedUser.getId());
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
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity activate(@RequestHeader final String authorization,
                                   @RequestParam final String userId, @RequestParam final String activationToken) throws IOException {

        ResponseEntity response = new ResponseEntity(HttpStatus.UNAUTHORIZED);

        String uri = httpScheme + "://" + serverHost + ":" + serverPort + RESOURCE_SERVER_URI + "/" + userId;
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, AUTHORIZATION, authorization);

        if (result.getStatusCode() == 200) {
            User userForActivation = mapper.readValue(result.getBody(), User.class);
            Extension extension = userForActivation.getExtension(internalScimExtensionUrn);
            String activationTokenFieldValue = extension.getField(activationTokenField);

            if (activationTokenFieldValue.equals(activationToken)) {
                extension.setField(activationTokenField, "");
                User updateUser = new User.Builder(userForActivation).setActive(true).build();

                HttpClientRequestResult requestResult = httpClient.executeHttpPut(uri,
                        mapper.writeValueAsString(updateUser), AUTHORIZATION, authorization);

                if (requestResult.getStatusCode() == 200) {
                    response = new ResponseEntity(HttpStatus.OK);
                } else {
                    response = new ResponseEntity(HttpStatus.valueOf(requestResult.getStatusCode()));
                }
            }
        } else {
            response = new ResponseEntity(HttpStatus.valueOf(result.getStatusCode()));
        }

        return response;
    }
}