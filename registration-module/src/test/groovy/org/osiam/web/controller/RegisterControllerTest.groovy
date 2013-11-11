package org.osiam.web.controller

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

/**
 * CHANGE THIS TEXT TO SOMETHING USEFUL, DESCRIBING THE CLASS.
 * User: Jochen Todea
 * Date: 07.11.13
 * Time: 14:08
 * Created: with Intellij IDEA
 */
class RegisterControllerTest extends Specification {

    def contextMock = Mock(ServletContext)
    def httpClientMock = Mock(HttpClientHelper)
    def registerController = new RegisterController(context: contextMock, httpClient: httpClientMock)

    @Shared def mapper

    def setupSpec() {
        mapper = new ObjectMapper()
        def userDeserializerModule = new SimpleModule("userDeserializerModule", new Version(1, 0, 0, null))
                .addDeserializer(User.class, new UserDeserializer(User.class))
        mapper.registerModule(userDeserializerModule)

    }

    def "The registration controller should return a HTML file as stream"() {
        given:
        def httpServletResponseMock = Mock(HttpServletResponse)
        def inputStreamMock = new ByteArrayInputStream("neunbytes".bytes)
        def outputStreamMock = Mock(ServletOutputStream)

        when:
        registerController.index("Bearer ACCESS_TOKEN", httpServletResponseMock)

        then:
        1 * httpServletResponseMock.setContentType("text/html")
        1 * contextMock.getResourceAsStream("/WEB-INF/registration/registration.html") >> inputStreamMock
        1 * httpServletResponseMock.getOutputStream() >> outputStreamMock
    }

    def "The registration controller should activate an previously registered user"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/User/"
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * httpClientMock.executeHttpPut(uri + userId, "updateUser", _, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        response.getStatusCode() == HttpStatus.OK
    }

    def getUserAsStringWithExtension(String token) {
        def urn = "urn:scim:schemas:osiam:1.0:webregister"
        def extensionData = ["activationToken":token]

        Extension extension = new Extension(urn, extensionData)
        def user = new User.Builder("George")
                .setPassword("password")
                .addExtension(urn, extension)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }

    def "The registration controller should return the status code if the user was not found by his id at activation"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()

        def requestResultMock = Mock(HttpClientRequestResult)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * httpClientMock.executeHttpGet("http://localhost:8080/osiam-resource-server/User/" + userId, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "The registration controller should return the status code if the user was not updated at activation"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/User/"
        def requestResultGetMock = Mock(HttpClientRequestResult)
        def requestResultPutMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, activationToken)

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultGetMock
        1 * requestResultGetMock.getStatusCode() >> 200
        1 * requestResultGetMock.getBody() >> userString
        1 * httpClientMock.executeHttpPut(uri + userId, "updateUser", _, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultPutMock
        2 * requestResultPutMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "The registration controller should not activate an previously registered user if wrong activation token is presented"(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = "http://localhost:8080/osiam-resource-server/User/"
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate("Bearer ACCESS_TOKEN", userId, UUID.randomUUID().toString())

        then:
        1 * httpClientMock.executeHttpGet(uri + userId, "Authorization", "Bearer ACCESS_TOKEN") >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }
}