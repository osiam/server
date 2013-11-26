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
import org.osiam.web.util.AccessTokenInformationProvider
import org.osiam.web.util.HttpHeader
import org.osiam.web.util.MailSender
import org.osiam.web.util.RegistrationExtensionUrnProvider
import org.osiam.web.util.ResourceServerUriBuilder
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

    def httpClientMock = Mock(HttpClientHelper)
    def resultMock = Mock(HttpClientRequestResult)
    def registrationExtensionUrnProvider = Mock(RegistrationExtensionUrnProvider)
    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def accessTokenInformationProvider = Mock(AccessTokenInformationProvider)

    def urn = "urn:scim:schemas:osiam:1.0:Registration"

    def confirmTokenField = "emailConfirmToken"
    def tempMailField = "tempMail"

    def mailSender = Mock(MailSender)
    def emailChangeLinkPrefix = "http://localhost:8080/stuff"
    def emailChangeMailFrom = "bugs@bunny.com"
    def emailChangeMailSubject = "email change"
    def emailChangeInfoMailSubject = "email change done"
    def context = Mock(ServletContext)

    def changeEmailController = new ChangeEmailController(httpClient: httpClientMock, confirmationTokenField: confirmTokenField,
            tempEmail: tempMailField, mailSender: mailSender, context: context, emailChangeLinkPrefix: emailChangeLinkPrefix,
            emailChangeMailFrom: emailChangeMailFrom, emailChangeMailSubject: emailChangeMailSubject,
            emailChangeInfoMailSubject: emailChangeInfoMailSubject, registrationExtensionUrnProvider: registrationExtensionUrnProvider,
            resourceServerUriBuilder: resourceServerUriBuilder, accessTokenInformationProvider: accessTokenInformationProvider)

    def setupSpec() {
        mapper = new ObjectMapper()
        def userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
                .addDeserializer(User.class, new UserDeserializer(User.class))
        mapper.registerModule(userDeserializerModule)
    }

    def "there should be an failure in change email if update user with extensions failed"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure in change email if get user by id failed"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure in change email if email content was not found"(){
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-content.txt", _, context) >> null
        result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        result.getBody() != null
    }

    def "Change email should generate a confirmation token, save the new email temporarily and send an email"() {
        given:
        def authZHeader = "Bearer ACCESSTOKEN"
        def userId = "theUserId"
        def newEmailValue = "bam@boom.com"
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def user = getUserAsString()

        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder $EMAILCHANGEURL'.bytes)

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-content.txt", _, context) >> inputStream
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

    def "Confirm email should validate the confirmation token and save the new email value as primary email ans send an email"() {
        given:
        def authZHeader = "abc"
        def userId = "userId"
        def confirmToken = "confToken"
        def url = "http://localhost:8080/osiam-resource-server/Users/" + userId;

        def user = getUserWithTempEmailAsString("confToken")

        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder'.bytes)

        when:
        def result = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(url, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * mailSender.extractPrimaryEmail(_) >> "email@example.org"
        1 * mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-info.txt", _, context) >> inputStream
        1 * mailSender.sendMail(emailChangeMailFrom, "email@example.org", emailChangeInfoMailSubject, inputStream, _)

        result.getStatusCode() == HttpStatus.OK
        User savedUser = mapper.readValue(result.getBody(), User)
        savedUser.getEmails().size() == 2
    }

    def "there should be an failure if retrieving user by id failed"() {
        given:
        def authZHeader = "abc"
        def userId = "userId"
        def confirmToken = "confToken"
        def url = "http://localhost:8080/osiam-resource-server/Users/" + userId;

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if confirmation token miss match"() {
        given:
        def authZHeader = "abc"
        def userId = "userId"
        def confirmToken = "confToken"
        def url = "http://localhost:8080/osiam-resource-server/Users/" + userId;

        def user = getUserWithTempEmailAsString("bullShit")

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        response.getStatusCode() == HttpStatus.FORBIDDEN
    }

    def "there should be an failure if updating user with extensions failed"() {
        given:
        def authZHeader = "abc"
        def userId = "userId"
        def confirmToken = "confToken"
        def url = "http://localhost:8080/osiam-resource-server/Users/" + userId;

        def user = getUserWithTempEmailAsString("confToken")

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(url, _,HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if content input stream is null"() {
        given:
        def authZHeader = "abc"
        def userId = "userId"
        def confirmToken = "confToken"
        def url = "http://localhost:8080/osiam-resource-server/Users/" + userId;

        def user = getUserWithTempEmailAsString("confToken")

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        2 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(url, _,HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * mailSender.getEmailContentAsStream("/WEB-INF/registration/emailchange-info.txt", _, context) >> null
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "there should be a failure if the provided confirmation token is empty"() {
        when:
        def result = changeEmailController.confirm("authZ", "userId", "")

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def getUserWithTempEmailAsString(confToken) {
        def primary = new MultiValuedAttribute(primary: true, value: "email@example.org")
        def email = new MultiValuedAttribute(primary: false, value: "nonPrimary@example.org")

        def fields = [:]
        fields.put(confirmTokenField, confToken)
        fields.put(tempMailField, "newemail@example.org")
        def extension = new Extension(urn, fields);

        def user = new User.Builder("Boy George")
                .setPassword("password")
                .setEmails([primary, email] as List)
                .setActive(false)
                .addExtension(urn, extension)
                .build()

        return mapper.writeValueAsString(user)
    }
}
