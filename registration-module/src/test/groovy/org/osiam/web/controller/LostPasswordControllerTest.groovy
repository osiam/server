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
        given:
        def userId = "someId"
        when:
        def result = lostPasswordController.lost(userId)

        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED //HttpStatus.CREATED
    }

    def "The controller should serve a html form to enable the user to submit the his new password"() {
        given:
        def otp = "someOTP"
        def userId = "someId"

        when:
        def result = lostPasswordController.lostFrom(otp, userId)

        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED
    }

    def "The controller should verify the user and change his password"() {
        given:
        def params = "The Body"

        when:
        def result = lostPasswordController.change(params)

        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED //HttpStatus.OK
    }
}