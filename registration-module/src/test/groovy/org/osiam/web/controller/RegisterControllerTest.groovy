/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.web.controller

import javax.servlet.ServletContext
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.helper.ObjectMapperWithExtensionConfig
import org.osiam.resources.scim.Email
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.osiam.web.exception.OsiamException
import org.osiam.web.mail.SendEmail;
import org.osiam.web.service.RegistrationExtensionUrnProvider;
import org.osiam.web.service.ResourceServerUriBuilder;
import org.osiam.web.template.EmailTemplateRenderer;
import org.osiam.web.template.RenderAndSendEmail;
import org.osiam.web.util.HttpHeader
import org.springframework.http.HttpStatus

import spock.lang.Specification

class RegisterControllerTest extends Specification {

    def mapper = new ObjectMapperWithExtensionConfig()

    def registrationExtensionUrnProvider = Mock(RegistrationExtensionUrnProvider)
    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def httpClientMock = Mock(HttpClientHelper)
    def contextMock = Mock(ServletContext)

    def urn = 'urn:scim:schemas:osiam:1.0:Registration'

    def activationTokenField = 'activationToken'
    def clientRegistrationUri = 'http://someStuff.de/'

    def registermailFrom = 'noreply@example.org'
    def registermailSubject = 'Ihre Registrierung'
    def registermailLinkPrefix = 'https://example.org/register?'

    def bootStrapLib = 'http://bootstrap'
    def angularLib = 'http://angular'
    
    SendEmail sendMailService = Mock()
    EmailTemplateRenderer emailTemplateRendererService = Mock()
    RenderAndSendEmail renderAndSendEmailService = new RenderAndSendEmail(sendMailService: sendMailService, 
        emailTemplateRendererService: emailTemplateRendererService);

    def registerController = new RegisterController(context: contextMock, httpClient: httpClientMock,
            clientRegistrationUri: clientRegistrationUri, activationTokenField: activationTokenField,
            fromAddress: registermailFrom, registermailLinkPrefix: registermailLinkPrefix,
            registrationExtensionUrnProvider: registrationExtensionUrnProvider,
            resourceServerUriBuilder: resourceServerUriBuilder, mapper: mapper, 
            bootStrapLib: bootStrapLib, angularLib: angularLib,
            renderAndSendEmailService: renderAndSendEmailService)

    def 'The registration controller should return a HTML file as stream'() {
        given:
        def httpServletResponseMock = Mock(HttpServletResponse)
        def inputStream = new ByteArrayInputStream('nine bytes and one placeholder $REGISTERLINK and $BOOTSTRAP and $ANGULAR'.bytes)
        def outputStreamMock = Mock(ServletOutputStream)

        when:
        registerController.index(httpServletResponseMock)

        then:
        1 * httpServletResponseMock.setContentType('text/html')
        1 * contextMock.getResourceAsStream('/WEB-INF/registration/registration.html') >> inputStream
        1 * httpServletResponseMock.getOutputStream() >> outputStreamMock
    }

    def 'The registration controller should activate an previously registered user'(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate('Bearer ACCESS_TOKEN', userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        response.getStatusCode() == HttpStatus.OK
    }

    def 'The registration controller should return the status code if the user was not found by his id at activation'(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def requestResultMock = Mock(HttpClientRequestResult)

        when:
        def response = registerController.activate('Bearer ACCESS_TOKEN', userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'The registration controller should return the status code if the user was not updated at activation'(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def requestResultGetMock = Mock(HttpClientRequestResult)
        def requestResultPutMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate('Bearer ACCESS_TOKEN', userId, activationToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultGetMock
        1 * requestResultGetMock.getStatusCode() >> 200
        1 * requestResultGetMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultPutMock
        2 * requestResultPutMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'The registration controller should not activate an previously registered user if wrong activation token is presented'(){
        given:
        def userId = UUID.randomUUID().toString()
        def activationToken = UUID.randomUUID().toString()
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId
        def requestResultMock = Mock(HttpClientRequestResult)
        def userString = getUserAsStringWithExtension(activationToken)

        when:
        def response = registerController.activate('Bearer ACCESS_TOKEN', userId, UUID.randomUUID().toString())

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, 'Bearer ACCESS_TOKEN') >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userString
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def 'The registration controller should send a html register-mail'() {
        given:
        def uri = 'http://localhost:8080/osiam-resource-server/Users/'

        def registerMailContent = 'irrelevant'
        def registerSubjectContent = 'irrelevant'
        def auth = 'BEARER ABC=='
        def body = getUserAsStringWithExtension('')

        when:
        def response = registerController.create(auth, body)

        then:
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId('') >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('{"id":"1234","schemas":["urn"]}', 201)
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> registerSubjectContent
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> registerMailContent
        1 * sendMailService.sendHTMLMail('noreply@example.org', 'email@example.org', registerSubjectContent, registerMailContent)
        response.statusCode == HttpStatus.OK
    }

    def 'there should be an failure if no primary email was found'() {
        given:
        def auth = 'BEARER ABC=='
        def body = getUserAsStringWithExtensionAndWithoutEmail('')

        when:
        def response = registerController.create(auth, body)

        then:
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'there should be an failure if the user could not be updated with activation token'() {
        given:
        def uri = 'http://localhost:8080/osiam-resource-server/Users/'

        def auth = 'BEARER ABC=='
        def body = getUserAsStringWithExtension('')

        when:
        def response = registerController.create(auth, body)

        then:
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId('') >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('', 400)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'there should be an failure if the email content for confirmation mail was not found'() {
        given:
        def uri = 'http://localhost:8080/osiam-resource-server/Users/'

        def auth = 'BEARER ABC=='
        def body = getUserAsStringWithExtension('')

        when:
        def response = registerController.create(auth, body)

        then:
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * resourceServerUriBuilder.buildUsersUriWithUserId('') >> uri
        1 * httpClientMock.executeHttpPost(_, _, _, _) >> new HttpClientRequestResult('{"id":"1234","schemas":["urn"]}', 201)
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def 'there should be an failure if the provided activation token is empty'() {
        when:
        def result = registerController.activate('authZ', 'userId', '')

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def getUserAsStringWithExtension(String token) {
        def emails = new Email.Builder().setPrimary(true).setValue('email@example.org').build()

        Extension extension = new Extension(urn)
        extension.addOrUpdateField('activationToken', token)

        def user = new User.Builder('George')
                .setPassword('password')
                .setEmails([emails])
                .addExtension(extension)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }
    
    def getUserAsStringWithExtensionAndWithoutEmail(String token) {
        Extension extension = new Extension(urn)
        extension.addOrUpdateField('activationToken', token)

        def user = new User.Builder('George')
                .setPassword('password')
                .addExtension(extension)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }
}