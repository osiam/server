package org.osiam.web.controller;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    private static final String AUTHORIZATION = "Authorization";
    private static final String INTERNAL_SCIM_EXTENSION_URN = "urn:scim:schemas:osiam:1.0:Registration";
    private static final String ACTIVATION_TOKEN_FIELD = "activationToken";

    private HttpClientHelper httpClient;
    private ObjectMapper mapper;

    @Value("${osiam.web.registermail.from}")
    private String registermailFrom;
    @Value("${osiam.web.registermail.subject}")
    private String registermailSubject;
    @Value("${osiam.web.registermail.linkprefix}")
    private String registermailLinkPrefix;

    private String createUserUri = "http://localhost:8080/osiam-resource-server/Users"; // TODO

    @Inject
    ServletContext context;

    public RegisterController() {
        httpClient = new HttpClientHelper();

        mapper = new ObjectMapper();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);
    }

    /**
     * Generates a form with all needed fields for creating a new user.
     *
     * @param authorization
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public void index(@RequestHeader final String authorization, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        InputStream in = context.getResourceAsStream("/WEB-INF/registration/registration.html");
        IOUtils.copy(in, response.getOutputStream());
    }

    /**
     *
     * Creates a new User.
     *
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to
     * the registered email-address.
     * @param authorization
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String authorization, @RequestBody String body) {
        try {
            User parsedUser = mapper.readValue(body, User.class);

            String foundEmail = null;
            for (MultiValuedAttribute email : parsedUser.getEmails()) {
                if (email.isPrimary()) {
                    foundEmail = (String) email.getValue();
                }
            }
            if (foundEmail == null) {
                // return error / no email found
            } else {

                // generate Activation Token
                String activationToken = UUID.randomUUID().toString();
                Extension webRegisterExt = parsedUser.getExtension(INTERNAL_SCIM_EXTENSION_URN);
                webRegisterExt.setField("activation_token", activationToken);


                // Save user
                saveUser(parsedUser, authorization);

                // Send activation mail
                MimeMessage msg = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
                msg.addFrom(InternetAddress.parse(registermailFrom));
                msg.addRecipient(Message.RecipientType.TO, InternetAddress.parse(foundEmail)[0]);
                msg.addHeader("Subject", MimeUtility.encodeText(registermailSubject));

                // Mailcontent with $REGISTERLINK as placeholder
                InputStream registerMailContentStream = this.getClass().getResourceAsStream("/registermail-content.txt");

                if (registerMailContentStream == null) {
                    // TODO LOG.error("Cant open registermail-content.txt on classpath! Please configure!");
                } else {
                    String mailContent = IOUtils.toString(registerMailContentStream);
                    StringBuilder activateURL = new StringBuilder(registermailLinkPrefix);
                    activateURL.append("?user=").append(parsedUser.getName());
                    activateURL.append("&token=").append(activationToken);

                    mailContent.replace("$REGISTERLINK", activateURL);
                    msg.setContent(mailContent, "text/plain");

                    Transport.send(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private HttpResponse saveUser(User userToSave, String authorization) throws IOException {
        InputStream content = null;
        try {
            HttpPost realWebResource = new HttpPost(createUserUri);
            realWebResource.addHeader(AUTHORIZATION, authorization);

            String userAsString = mapper.writeValueAsString(userToSave);

            realWebResource.setEntity(new StringEntity(userAsString, ContentType.create("application/json")));

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(realWebResource);

            return response;
        }finally{
            try {
                content.close();
            } catch (Exception ignore) {/* if fails we don't care */}
        }
    }

    /**
     * Activates a previously registered user.
     *
     * After activation E-Mail arrived the activation link will point to this URI.
     *
     * @param authorization an valid OAuth2 token
     * @param user the id of the registered user
     * @param token the user's activation token, send by E-Mail
     *
     * @return HTTP status, HTTP.OK (200) for a valid activation
     */
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity activate(@RequestHeader final String authorization,
                                   @RequestParam("user") final String user, @RequestParam("token") final String token) throws IOException {

        ResponseEntity response = new ResponseEntity(HttpStatus.UNAUTHORIZED);

        String uri = createUserUri + "/" + user;
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, AUTHORIZATION, authorization);

        if (result.getStatusCode() == 200) {
            User userForActivation = mapper.readValue(result.getBody(), User.class);
            Extension extension = userForActivation.getExtension(INTERNAL_SCIM_EXTENSION_URN);
            String activationTokenFieldValue = extension.getField(ACTIVATION_TOKEN_FIELD);

            if (activationTokenFieldValue.equals(token)) {
                extension.setField(ACTIVATION_TOKEN_FIELD, null);
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