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
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.Meta;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.User;
import org.osiam.web.exception.OsiamException;
import org.osiam.web.service.SendMail;
import org.osiam.web.service.TemplateRenderer;
import org.osiam.web.util.HttpHeader;
import org.osiam.web.util.RegistrationExtensionUrnProvider;
import org.osiam.web.util.RegistrationHelper;
import org.osiam.web.util.ResourceServerUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;

/**
 * Controller to handle the registration process
 * 
 */
@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    @Inject
    private ObjectMapperWithExtensionConfig mapper;

    @Inject
    private ResourceServerUriBuilder resourceServerUriBuilder;

    @Inject
    private RegistrationExtensionUrnProvider registrationExtensionUrnProvider;

    @Inject
    private HttpClientHelper httpClient;

    @Inject
    private ServletContext context;

    @Inject
    private SendMail sendMailService;

    @Inject
    private TemplateRenderer templateRendererService;

    /* Registration email configuration */
    @Value("${osiam.web.registermail.content.path}")
    private String pathToContentFile;
    @Value("${osiam.web.registermail.linkprefix}")
    private String registermailLinkPrefix;
    @Value("${osiam.web.registermail.from}")
    private String registermailFrom;
    @Value("${osiam.web.registermail.subject}")
    private String registermailSubject;

    /* Registration extension configuration */
    @Value("${osiam.activation.token.field}")
    private String activationTokenField;

    /* URI for the registration call from JavaScript */
    @Value("${osiam.web.register.url}")
    private String clientRegistrationUri;

    // css and js libs
    @Value("${osiam.html.dependencies.bootstrap}")
    private String bootStrapLib;

    @Value("${osiam.html.dependencies.angular}")
    private String angularLib;

    @Value("${osiam.html.dependencies.jquery}")
    private String jqueryLib;

    /**
     * Generates a HTTP form with the fields for registration purpose.
     */
    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        InputStream inputStream = context.getResourceAsStream("/WEB-INF/registration/registration.html");

        // replace registration link
        String htmlContent = IOUtils.toString(inputStream, "UTF-8");
        String replacedHtmlContent = htmlContent.replace("$REGISTERLINK", clientRegistrationUri);

        // replace all libs
        replacedHtmlContent = replacedHtmlContent.replace("$BOOTSTRAP", bootStrapLib);
        replacedHtmlContent = replacedHtmlContent.replace("$ANGULAR", angularLib);

        InputStream in = IOUtils.toInputStream(replacedHtmlContent);

        IOUtils.copy(in, response.getOutputStream());
    }

    /**
     * Creates a new User.
     * 
     * Needs all data given by the 'index'-form. Saves the user in an inactivate-state. Sends an activation-email to the
     * registered email-address.
     * 
     * @param authorization
     *            a valid access token
     * @return the saved user and HTTP.OK (200) for successful creation, otherwise only the HTTP status
     * @throws IOException
     * @throws MessagingException
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> create(@RequestHeader final String authorization, @RequestBody String user)
            throws IOException, MessagingException {

        User parsedUser = mapper.readValue(user, User.class);

        Optional<String> email = RegistrationHelper.extractSendToEmail(parsedUser);
        if (!email.isPresent()) {
            LOGGER.log(Level.WARNING, "No primary email found!");
            return new ResponseEntity<>("{\"error\":\"No primary email found!\"}", HttpStatus.BAD_REQUEST);
        }

        // generate Activation Token
        String activationToken = UUID.randomUUID().toString();
        parsedUser = createUserForRegistration(parsedUser, activationToken);

        // Save user
        HttpClientRequestResult saveUserResponse = saveUser(parsedUser, authorization);
        if (saveUserResponse.getStatusCode() != HttpStatus.CREATED.value()) {
            LOGGER.log(Level.WARNING, "Problems creating user for registration");
            return new ResponseEntity<>("{\"error\":\"Problems creating user for registration\"}",
                    HttpStatus.valueOf(saveUserResponse.getStatusCode()));
        }

        User createdUser = mapper.readValue(saveUserResponse.getBody(), User.class);

        try {
            sendActivationMail(email.get(), createdUser, activationToken);
        } catch (OsiamException e) {
            return new ResponseEntity<>("{\"error\":\"Problems creating user for registration\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(saveUserResponse.getBody(), HttpStatus.OK);
    }

    /**
     * Activates a previously registered user.
     * 
     * After activation E-Mail arrived the activation link will point to this URI.
     * 
     * @param authorization
     *            an valid OAuth2 token
     * @param userId
     *            the id of the registered user
     * @param activationToken
     *            the user's activation token, send by E-Mail
     * 
     * @return HTTP status, HTTP.OK (200) for a valid activation
     */
    @RequestMapping(value = "/activate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> activate(@RequestHeader final String authorization,
            @RequestParam final String userId, @RequestParam final String activationToken) throws IOException {

        if (activationToken.equals("")) {
            LOGGER.log(Level.WARNING, "Activation token miss match!");
            return new ResponseEntity<>("{\"error\":\"Activation token miss match!\"}", HttpStatus.UNAUTHORIZED);
        }

        String uri = resourceServerUriBuilder.buildUsersUriWithUserId(userId);

        // get user by his id
        HttpClientRequestResult result = httpClient.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authorization);
        if (result.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Problems retrieving user by his ID!");
            return new ResponseEntity<>("{\"error\":\"Problems retrieving user by his ID!\"}",
                    HttpStatus.valueOf(result.getStatusCode()));
        }

        // get extension field to check activation token validity
        User userForActivation = mapper.readValue(result.getBody(), User.class);
        Extension extension = userForActivation.getExtension(registrationExtensionUrnProvider.getExtensionUrn());
        String activationTokenFieldValue = extension.getField(activationTokenField, ExtensionFieldType.STRING);

        if (!activationTokenFieldValue.equals(activationToken)) {
            LOGGER.log(Level.WARNING, "Activation token miss match!");
            return new ResponseEntity<>("{\"error\":\"Activation token miss match!\"}", HttpStatus.UNAUTHORIZED);
        }

        String updateUser = getUserForActivationAsString(extension);

        // update user
        HttpClientRequestResult requestResult = httpClient.executeHttpPatch(uri, updateUser, HttpHeader.AUTHORIZATION,
                authorization);
        if (requestResult.getStatusCode() != HttpStatus.OK.value()) {
            LOGGER.log(Level.WARNING, "Updating user with extensions failed!");
            return new ResponseEntity<>("{\"error\":\"Updating user with extensions failed!\"}",
                    HttpStatus.valueOf(requestResult.getStatusCode()));
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void sendActivationMail(String toAddress, User createdUser, String activationToken)
            throws MessagingException, IOException {
        StringBuilder activateURL = new StringBuilder(registermailLinkPrefix);
        activateURL.append("userId=").append(createdUser.getId());
        activateURL.append("&activationToken=").append(activationToken);

        Map<String, String> mailVars = new HashMap<>();
        mailVars.put("registerlink", activateURL.toString());

        String mailContent = templateRendererService.renderTemplate("registration", createdUser, mailVars);

        sendMailService.sendHTMLMail(registermailFrom, toAddress, registermailSubject, mailContent);
    }

    private String getUserForActivationAsString(Extension extension) throws JsonProcessingException {
        // validation successful -> delete token and activate user
        Set<String> deletionSet = new HashSet<String>();
        deletionSet.add(extension.getUrn() + "." + activationTokenField);
        Meta meta = new Meta.Builder().setAttributes(deletionSet).build();
        User updateUser = new User.Builder().setActive(true).setMeta(meta).build();
        return mapper.writeValueAsString(updateUser);
    }

    private User createUserForRegistration(User parsedUser, String activationToken) {
        // Add Extension with the token to the user and set active to false
        User.Builder builder = new User.Builder(parsedUser);
        builder.setActive(false);

        // Add user to role 'USER' to be able to login afterwards
        List<Role> roles = new ArrayList<>();
        roles.add(new Role.Builder().setValue("USER").build());
        builder.setRoles(roles);

        Extension extension = new Extension(registrationExtensionUrnProvider.getExtensionUrn());
        extension.addOrUpdateField(activationTokenField, activationToken);
        builder.addExtension(extension);

        return builder.build();
    }

    private HttpClientRequestResult saveUser(User userToSave, String authorization) throws IOException {
        String userAsString = mapper.writeValueAsString(userToSave);
        String createUserUri = resourceServerUriBuilder.buildUsersUriWithUserId("");
        return httpClient.executeHttpPost(createUserUri, userAsString, HttpHeader.AUTHORIZATION, authorization);
    }
}