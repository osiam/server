package org.osiam.web.controller

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.osiam.web.util.MailSender
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * Test for LostPasswordController
 * User: Jochen Todea
 * Date: 15.11.13
 * Time: 14:41
 * Created: with Intellij IDEA
 */
class LostPasswordControllerTest extends Specification {

    @Shared def mapper
    def httpClientMock = Mock(HttpClientHelper)
    def requestResultMock = Mock(HttpClientRequestResult)
    def contextMock = Mock(ServletContext)

    def serverPort = 8080
    def serverHost = "localhost"
    def httpScheme = "http"
    def internalScimExtensionUrn = "urn:scim:schemas:osiam:1.0:Registration"
    def oneTimePasswordField = "oneTimePassword"

    def mailSenderMock = Mock(MailSender)
    def passwordlostLinkPrefix = "http://localhost:8080"
    def passwordlostMailFrom = "noreply@example.org"
    def passwordlostMailSubject = "Subject"

    def lostPasswordController = new LostPasswordController(httpClient: httpClientMock, serverPort: serverPort,
            serverHost: serverHost, httpScheme: httpScheme, internalScimExtensionUrn: internalScimExtensionUrn,
            oneTimePassword: oneTimePasswordField, context: contextMock, mailSender: mailSenderMock,
            passwordlostLinkPrefix: passwordlostLinkPrefix, passwordlostMailFrom: passwordlostMailFrom,
            passwordlostMailSubject: passwordlostMailSubject)

    def setupSpec() {
        mapper = new ObjectMapper()
        def userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
                .addDeserializer(User.class, new UserDeserializer(User.class))
        mapper.registerModule(userDeserializerModule)
    }


    def "The controller should start the flow by generating a one time password and send an email to the user"() {
        given:
        def userId = "someId"
        def authZHeader = "Bearer ACCESSTOKEN"

        def uri = "http://localhost:8080/osiam-resource-server/Users/"
        def userString = getUserAsStringWithExtension("token")

        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder $PASSWORDLOSTURL'.bytes)

        when:
        def result = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * httpClientMock.executeHttpPatch(uri + userId, _, "Authorization", authZHeader) >> new HttpClientRequestResult("body", 200);

        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString

        1 * mailSenderMock.getEmailContentAsStream("/WEB-INF/registration/passwordlostmail-content.txt", _, contextMock) >> inputStream
        1 * mailSenderMock.sendMail("noreply@example.org", "toemail@example.org", "Subject", inputStream, _)
        1 * mailSenderMock.extractPrimaryEmail(_) >> "toemail@example.org"

        result.getStatusCode() == HttpStatus.OK
    }

    def "there should be an failure if retrieving the user by his id failed"(){
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"
        def userId = "someId"
        def authZHeader = "Bearer ACCESSTOKEN"

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> new HttpClientRequestResult('', 400)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if the user could not be updated with one time password"(){
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"
        def userId = "someId"
        def authZHeader = "Bearer ACCESSTOKEN"
        def userString = getUserAsStringWithExtension("token")

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * httpClientMock.executeHttpPatch(uri + userId, _, "Authorization", authZHeader) >> new HttpClientRequestResult("body", 400);
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if no primary email was found"(){
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"
        def userId = "someId"
        def authZHeader = "Bearer ACCESSTOKEN"
        def userString = getUserAsStringWithExtension("token")

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * httpClientMock.executeHttpPatch(uri + userId, _, "Authorization", authZHeader) >> new HttpClientRequestResult("body", 200);
        1 * mailSenderMock.extractPrimaryEmail(_) >> null
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody() != null
    }

    def "there should be an failure if the email content for confirmation mail was not found"(){
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"
        def userId = "someId"
        def authZHeader = "Bearer ACCESSTOKEN"
        def userString = getUserAsStringWithExtension("token")

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * httpClientMock.executeHttpPatch(uri + userId, _, "Authorization", authZHeader) >> new HttpClientRequestResult("body", 200);
        1 * mailSenderMock.extractPrimaryEmail(_) >> "primary@mail.com"
        1 * mailSenderMock.getEmailContentAsStream("/WEB-INF/registration/passwordlostmail-content.txt", _, contextMock) >> null
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody() != null
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

    def "The controller should verify the user and change its password"() {
        given:
        def otp = "someOTP"
        def userId = "someId"
        def newPassword = "newPassword"
        def authZHeader = "Bearer ACCESSTOKEN"
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        def userById = getUserAsStringWithExtension(otp)

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userById
        1 * httpClientMock.executeHttpPatch(uri + userId, _, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> "updated user"

        result.getStatusCode() == HttpStatus.OK
        result.getBody() == "updated user"
    }

    def "If the user will not be found by his id the response should contain the appropriate status code"() {
        given:
        def otp = "someOTP"
        def userId = "someId"
        def newPassword = "newPassword"
        def authZHeader = "Bearer ACCESSTOKEN"
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400

        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "If the provided one time password has no match with the saved on from the database the appropriate status code will be returned and the process is stopped"() {
        given:
        def otp = "someOTP"
        def userId = "someId"
        def newPassword = "newPassword"
        def authZHeader = "Bearer ACCESSTOKEN"
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        def userById = getUserAsStringWithExtension("Invalid OTP")

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userById

        result.getStatusCode() == HttpStatus.FORBIDDEN
    }

    def "there should be a failure if the provided one time password is empty"() {
        when:
        def result = lostPasswordController.change("authZ", "", "userId", "newPW")

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def getUserAsStringWithExtension(String otp) {
        def urn = "urn:scim:schemas:osiam:1.0:Registration"
        def extensionData = ["oneTimePassword":otp]

        def emails = new MultiValuedAttribute(primary: true, value: "email@example.org")

        Extension extension = new Extension(urn, extensionData)
        def user = new User.Builder("George")
                .setPassword("password")
                .setEmails([emails])
                .addExtension(urn, extension)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }
}