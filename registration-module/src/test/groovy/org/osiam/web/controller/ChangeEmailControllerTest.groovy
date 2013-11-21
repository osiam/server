package org.osiam.web.controller

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.osiam.web.util.MailSender
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.ServletContext

/**
 * Unit test for change email controller.
 * User: Jochen Todea
 * Date: 20.11.13
 * Time: 13:35
 * Created: with Intellij IDEA
 */
class ChangeEmailControllerTest extends Specification {

    @Shared def mapper

    def httpScheme = "http"
    def httpHost = "localhost"
    def httpPort = 8080
    def httpClientMock = Mock(HttpClientHelper)
    def urn = "urn:scim:schemas:osiam:1.0:Registration"
    def confirmTokenField = "emailConfirmToken"
    def tempMailField = "tempMail"

    def mailSender = Mock(MailSender)
    def emailChangeLinkPrefix = "http://localhost:8080/stuff"
    def emailChangeMailFrom = "bugs@bunny.com"
    def emailChangeMailSubject = "email change"
    def context = Mock(ServletContext)

    def changeEmailController = new ChangeEmailController(httpScheme: httpScheme, serverHost: httpHost,
            serverPort: httpPort, httpClient: httpClientMock, confirmationTokenField: confirmTokenField,
            tempEmail: tempMailField, internalScimExtensionUrn: urn, mailSender: mailSender, context: context,
            emailChangeLinkPrefix: emailChangeLinkPrefix, emailChangeMailFrom: emailChangeMailFrom,
            emailChangeMailSubject: emailChangeMailSubject)

    def setupSpec() {
        mapper = new ObjectMapper()
        def userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
                .addDeserializer(User.class, new UserDeserializer(User.class))
        mapper.registerModule(userDeserializerModule)
    }

    def "Change email should return the status code if update user with extensions failed"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def resultMock = Mock(HttpClientRequestResult)

        when:
        def result = changeEmailController.change(authZHeader, userId, newEmailValue)

        then:
        1 * httpClientMock.executeHttpGet(uri, "Authorization", authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "Change email should return the status code if get user by id failed"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def resultMock = Mock(HttpClientRequestResult)

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, userId, newEmailValue)

        then:
        1 * httpClientMock.executeHttpGet(uri, "Authorization", authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * httpClientMock.executeHttpPatch(uri, _, "Authorization", authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "Change email should generate a confirmation token, save the new email temporarily and send an email"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def resultMock = Mock(HttpClientRequestResult)

        def user = getUserAsString()

        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder $EMAILCHANGEURL'.bytes)

        when:
        def result = changeEmailController.change(authZHeader, userId, newEmailValue)

        then:
        1 * httpClientMock.executeHttpGet(uri, "Authorization", authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        1 * httpClientMock.executeHttpPatch(uri, _, "Authorization", authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * context.getResourceAsStream("/WEB-INF/registration/emailchange-content.txt") >> inputStream
        1 * mailSender.sendMail(emailChangeMailFrom, newEmailValue, emailChangeMailSubject, inputStream, _)
        result.getStatusCode() == HttpStatus.OK
    }

    def getUserAsString() {
        def emails = new MultiValuedAttribute(primary: true, value: "email@example.org")

        def user = new User.Builder("Boy George")
                .setPassword("password")
                .setEmails([emails])
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }

    // TODO TBD
    def "Confirm email should validate the confirmation token and save the new email value as primary email ans send an email"() {
        given:
        def authZHeader = ""
        def userId = ""
        def confirmToken = ""

        when:
        def result = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        result.getStatusCode() == HttpStatus.NOT_IMPLEMENTED
    }
}
