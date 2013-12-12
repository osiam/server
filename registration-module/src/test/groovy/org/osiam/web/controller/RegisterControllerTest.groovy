package org.osiam.web.controller

import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.helper.ObjectMapperWithExtensionConfig
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.osiam.web.util.HttpHeader
import org.osiam.web.util.MailSender
import org.osiam.web.util.RegistrationExtensionUrnProvider
import org.osiam.web.util.ResourceServerUriBuilder
import org.springframework.http.HttpStatus
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

class RegisterControllerTest extends Specification {

    def mapper = new ObjectMapperWithExtensionConfig()

    def registrationExtensionUrnProvider = Mock(RegistrationExtensionUrnProvider)
    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def httpClientMock = Mock(HttpClientHelper)
    def contextMock = Mock(ServletContext)

    def urn = "urn:scim:schemas:osiam:1.0:Registration"

    def activationTokenField = "activationToken"
    def clientRegistrationUri = "http://someStuff.de/"

    def registermailFrom = "noreply@example.org"
    def registermailSubject = "Ihre Registrierung"
    def registermailLinkPrefix = "https://example.org/register?"

    def mailSenderMock = Mock(MailSender)

    def registerController = new RegisterController(context: contextMock, httpClient: httpClientMock,
            clientRegistrationUri: clientRegistrationUri, activationTokenField: activationTokenField,
            mailSender: mailSenderMock, registermailFrom: registermailFrom, registermailSubject: registermailSubject,
            registermailLinkPrefix: registermailLinkPrefix, registrationExtensionUrnProvider: registrationExtensionUrnProvider,
            resourceServerUriBuilder: resourceServerUriBuilder, mapper: mapper)


    def "The registration controller should return a HTML file as stream"() {
        given:
        def httpServletResponseMock = Mock(HttpServletResponse)
        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder $REGISTERLINK'.bytes)
        def outputStreamMock = Mock(ServletOutputStream)

        when:
        registerController.index(httpServletResponseMock)

        then:
        1 * httpServletResponseMock.setContentType("text/html")
        1 * contextMock.getResourceAsStream("/WEB-INF/registration/registration.html") >> inputStream
        1 * httpServletResponseMock.getOutputStream() >> outputStreamMock
    }

    def "The registration controller should activate an previously registered user"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPut(uri, _, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        response.getStatusCode() == HttpStatus.OK
    }

    def "The registration controller should return the status code if the user was not found by his id at activation"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def requestResultMock = Mock(HttpClientRequestResult)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "The registration controller should return the status code if the user was not updated at activation"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId

        def requestResultGetMock = Mock(HttpClientRequestResult)
        def requestResultPutMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultGetMock
        1 * requestResultGetMock.getStatusCode() >> 200
        1 * requestResultGetMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPut(uri, _, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultPutMock
        2 * requestResultPutMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "The registration controller should not activate an previously registered user if wrong activation token is presented"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/Users/" + userId
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, UUID.randomUUID().toString())

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def "The registration controller should send a register-mail"() {
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        def registerMailContent = new ByteArrayInputStream("Hallo \$REGISTERLINK Tschuess".bytes)
        def auth = "BEARER ABC=="
        def body = getUserAsStringWithExtension("")

        when:
        def response = registerController.create(auth, body)

        then:
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId("") >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('{"id":"1234","schemas":["urn"]}', 201)
        1 * mailSenderMock.getEmailContentAsStream("/WEB-INF/registration/registermail-content.txt", _, contextMock) >> registerMailContent
        1 * mailSenderMock.sendMail("noreply@example.org", "toemail@example.org", "Ihre Registrierung", registerMailContent, _)
        1 * mailSenderMock.extractPrimaryEmail(_) >> "toemail@example.org"
        response.statusCode == HttpStatus.OK
    }

    def "there should be an failure if no primary email was found"() {
        given:
        def auth = "BEARER ABC=="
        def body = getUserAsStringWithExtension("")

        when:
        def response = registerController.create(auth, body)

        then:
        1 * mailSenderMock.extractPrimaryEmail(_) >> null
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if the user could not be updated with activation token"() {
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        def auth = "BEARER ABC=="
        def body = getUserAsStringWithExtension("")

        when:
        def response = registerController.create(auth, body)

        then:
        1 * mailSenderMock.extractPrimaryEmail(_) >> "primary@mail.com"
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId("") >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('', 400)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "there should be an failure if the email content for confirmation mail was not found"() {
        given:
        def uri = "http://localhost:8080/osiam-resource-server/Users/"

        def auth = "BEARER ABC=="
        def body = getUserAsStringWithExtension("")

        when:
        def response = registerController.create(auth, body)

        then:
        1 * mailSenderMock.extractPrimaryEmail(_) >> "primary@mail.com"
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId("") >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('{"id":"1234","schemas":["urn"]}', 201)
        1 * mailSenderMock.getEmailContentAsStream("/WEB-INF/registration/registermail-content.txt", _, contextMock) >> null
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "there should be an failure if the provided activation token is empty"() {
        when:
        def result = registerController.activate("authZ", "userId", "")

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def getUserAsStringWithExtension(String token) {
        def emails = new MultiValuedAttribute(primary: true, value: "email@example.org")

        Extension extension = new Extension(urn)
        extension.addOrUpdateField("activationToken", token)

        def user = new User.Builder("George")
                .setPassword("password")
                .setEmails([emails])
                .addExtension(extension)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }
}