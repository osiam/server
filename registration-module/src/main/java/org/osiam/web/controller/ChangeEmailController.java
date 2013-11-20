package org.osiam.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * Saving the new E-Mail temporary, generating confirmation token and sending an E-Mail to the old registered address.
     * @param authorization Authorization header with HTTP Bearer authorization and a valid access token
     * @param userId The user id for the user whom email address should be changed
     * @param newEmailValue The new email address value
     * @return The HTTP status code
     */
    @RequestMapping(method = RequestMethod.POST, value = "/change")
    public ResponseEntity<String> change(@RequestHeader final String authorization, @RequestParam final String userId,
                                         @RequestParam final String newEmailValue) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Validating the confirm token and saving the new email value as primary email if the validation was successful.
     * @param authorization Authorization header with HTTP Bearer authorization and a valid access token
     * @param userId The user id for the user whom email address should be changed
     * @param confirmToken The previously generated confirmation token from the confirmation email
     * @return The HTTP status code and the updated user if successful
     */
    @RequestMapping(method = RequestMethod.POST, value = "/confirm")
    public ResponseEntity<String> confirm(@RequestHeader final String authorization, @RequestParam final String userId,
                                          @RequestParam final String confirmToken) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}