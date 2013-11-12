package org.osiam.web.controller

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.apache.http.HttpResponse
import org.apache.http.ProtocolVersion
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
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

    @Shared def mapper
    @Shared HttpClientHelper httpClientHelper = Mock()

    def context = Mock(ServletContext)
    def registerController = new RegisterController(context: context, httpClient: httpClientHelper)

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
        1 * context.getResourceAsStream("/WEB-INF/registration/registration.html") >> inputStreamMock
        1 * httpServletResponseMock.getOutputStream() >> outputStreamMock
    }

    def "The registration controller should send a register-mail"() {

        given:
        registerController.registermailFrom = "noreply@example.org"
        registerController.registermailSubject = "Ihre Registrierung"
        registerController.registermailLinkPrefix = "https://example.org/register?"

        def mailSenderMock = Mock(MailSender)
        registerController.mailSender = mailSenderMock

        def httpClientHelper = Mock(HttpClientHelper)
        registerController.httpClient = httpClientHelper

        def registerMailContent = new ByteArrayInputStream("Hallo \$REGISTERLINK Tschuess".bytes)
        def auth = "BEARER ABC=="
        def body = getUserAsStringWithExtension()

        when:
        def response = registerController.create(auth, body)

        then:
        httpClientHelper.executeHttpPut(_, _, _, _, _) >> new HttpClientRequestResult("body", 200)
        context.getResourceAsStream("/WEB-INF/registration/registermail-content.txt") >> registerMailContent
        response.statusCode == HttpStatus.OK

        1 * mailSenderMock.sendMail(_)
    }

    def getUserAsStringWithExtension() {
        def urn = "extension"
        def extensionData = ["gender":"male","birth":"Wed Oct 30 16:54:00 CET 1985","newsletter":"false"]

        def emails = new MultiValuedAttribute(primary: true, value: "email@example.org")

        Extension extension = new Extension(urn, extensionData)
        def user = new User.Builder("George")
                .setPassword("password")
                .setEmails([emails])
                .addExtension(urn, extension)
                .build()

        return mapper.writeValueAsString(user)
    }
}
