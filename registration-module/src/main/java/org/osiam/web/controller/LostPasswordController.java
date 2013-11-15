package org.osiam.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    public ResponseEntity<String> lost() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public void lostFrom() {

    }

    public ResponseEntity<String> change() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
