package org.osiam.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/lost/{userId}", method = RequestMethod.POST)
    public ResponseEntity<String> lost(@PathVariable final String userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/lostForm", method = RequestMethod.GET)
    public ResponseEntity<String> lostFrom(@RequestParam String otp, @RequestParam String userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @RequestMapping(value = "/change", method = RequestMethod.POST)
    public ResponseEntity<String> change(@RequestBody String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
