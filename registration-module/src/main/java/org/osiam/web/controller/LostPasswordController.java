package org.osiam.web.controller;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osiam.helper.HttpClientHelper;
import org.osiam.helper.HttpClientRequestResult;
import org.osiam.resources.helper.UserDeserializer;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller to handel the lost password flow
 * User: Jochen Todea
 * Date: 15.11.13
 * Time: 14:58
 * Created: with Intellij IDEA
 */
@Controller
@RequestMapping(value = "/password")
public class LostPasswordController {

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

    @Value("${osiam.one.time.password.field}")
    private String oneTimePassword;


    public LostPasswordController() {
        mapper = new ObjectMapper();
        SimpleModule userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null, null, null))
                .addDeserializer(User.class, new UserDeserializer(User.class));
        mapper.registerModule(userDeserializerModule);
    }


    @RequestMapping(value = "/lost/{userId}", method = RequestMethod.POST)
    public ResponseEntity<String> lost(@RequestHeader final String authorization, @PathVariable final String userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/lostForm", method = RequestMethod.GET, produces = "text/html")
    public ResponseEntity<String> lostFrom(@RequestParam String otp, @RequestParam String userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * Method to change the users password if the preconditions are satisfied.
     * @param authorization authZ header with valid access token
     * @param otp the previously generated one time password
     * @param userId the user id for whom you want to change the password
     * @param newPassword the new user password
     * @return the response with status code and the updated user if successfully
     * @throws IOException
     */
    @RequestMapping(value = "/change", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> change(@RequestHeader final String authorization, @RequestParam String otp,
                                 @RequestParam String userId, @RequestParam String newPassword) throws IOException {

        String uri = httpScheme + "://" + serverHost + ":" + serverPort + RESOURCE_SERVER_URI + "/" + userId;

        //get user by id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, AUTHORIZATION, authorization);
        if (result.getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by ID!");
            return new ResponseEntity<>(HttpStatus.valueOf(result.getStatusCode()));
        }
        User user = mapper.readValue(result.getBody(), User.class);

        //validate the otp with the saved one from DB
        Extension extension = user.getExtension(internalScimExtensionUrn);
        String savedOTP = extension.getField(oneTimePassword);
        if (!savedOTP.equals(otp)) {
            LOGGER.log(Level.SEVERE, "The submitted one time password is invalid!");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        //delete the otp from user entity
        extension.setField(oneTimePassword, "");

        //set new password for the user
        User updateUser = new User.Builder(user).setPassword(newPassword).build();
        String updateUserAsString = mapper.writeValueAsString(updateUser);

        //update the user with PATCH
        HttpClientRequestResult savedResult = httpClient.executeHttpPatch(uri, updateUserAsString, AUTHORIZATION, authorization);

        //return saved user with corresponding status code
        return new ResponseEntity<>(savedResult.getBody(), HttpStatus.valueOf(savedResult.getStatusCode()));
    }
}