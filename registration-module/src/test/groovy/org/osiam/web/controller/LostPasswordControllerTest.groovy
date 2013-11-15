package org.osiam.web.controller

import org.springframework.http.HttpStatus
import spock.lang.Specification


/**
 * Test for LostPasswordController
 * User: Jochen Todea
 * Date: 15.11.13
 * Time: 14:41
 * Created: with Intellij IDEA
 */
class LostPasswordControllerTest extends Specification {

    def lostPasswordController = new LostPasswordController()

    def "The controller should start the flow by generating a one time password and send an email to the user"() {
        when:
        def result = lostPasswordController.lost()

        then:
        result.getStatusCode() == HttpStatus.CREATED
    }

    def "The controller should serve a html form to enable the user to submit the his new password"() {
        when:
        lostPasswordController.lostFrom()

        then:
        false
    }

    def "The controller should verify the user and change his password"() {
        when:
        def result = lostPasswordController.change()

        then:
        result.getStatusCode() == HttpStatus.OK
    }
}