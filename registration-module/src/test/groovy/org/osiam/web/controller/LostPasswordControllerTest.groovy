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
import org.osiam.web.service.RegistrationExtensionUrnProvider
import org.osiam.web.service.ResourceServerUriBuilder
import org.osiam.web.template.EmailTemplateRenderer;
import org.osiam.web.template.RenderAndSendEmail;
import org.osiam.web.util.HttpHeader
import org.springframework.http.HttpStatus

import spock.lang.Specification

/**
 * Test for LostPasswordController
 */
class LostPasswordControllerTest extends Specification {

    def mapper = new ObjectMapperWithExtensionConfig()
    def httpClientMock = Mock(HttpClientHelper)
    def requestResultMock = Mock(HttpClientRequestResult)
    def contextMock = Mock(ServletContext)

    def registrationExtensionUrnProvider = Mock(RegistrationExtensionUrnProvider)
    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)

    def urn = 'urn:scim:schemas:osiam:1.0:Registration'

    def oneTimePasswordField = 'oneTimePassword'

    SendEmail sendMailService = Mock()
    EmailTemplateRenderer emailTemplateRendererService = Mock()
    RenderAndSendEmail renderAndSendEmailService = new RenderAndSendEmail(sendMailService: sendMailService, 
        emailTemplateRendererService: emailTemplateRendererService);
    
    def passwordlostLinkPrefix = 'http://localhost:8080'
    def passwordlostMailFrom = 'noreply@example.org'
    def passwordlostMailSubject = 'Subject'

    def clientPasswordChangeUri = 'http://localhost:8080'

    def bootStrapLib = 'http://bootstrap'
    def angularLib = 'http://angular'
    def jqueryLib = 'http://jquery'

    def lostPasswordController = new LostPasswordController(httpClient: httpClientMock, oneTimePassword: oneTimePasswordField,
            context: contextMock, passwordlostLinkPrefix: passwordlostLinkPrefix,
            fromAddress: passwordlostMailFrom, resourceServerUriBuilder: resourceServerUriBuilder,
            registrationExtensionUrnProvider: registrationExtensionUrnProvider, 
            clientPasswordChangeUri: clientPasswordChangeUri, mapper: mapper, bootStrapLib: bootStrapLib, angularLib: angularLib,
            jqueryLib: jqueryLib, renderAndSendEmailService: renderAndSendEmailService)

    def 'The controller should start the flow by generating a one time password and send an email to the user'() {
        given:
        def userId = 'someId'
        def authZHeader = 'Bearer ACCESSTOKEN'

        def uri = 'http://localhost:8080/osiam-resource-server/Users/'+ userId
        def userString = getUserAsStringWithExtension('token')

        def emailContent = 'nine bytes and one placeholder $PASSWORDLOSTURL and $BOOTSTRAP and $ANGULAR and $JQUERY'

        when:
        def result = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> new HttpClientRequestResult(userString, 200)
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> emailContent
        1 * sendMailService.sendHTMLMail(_, _, _, _)

        result.getStatusCode() == HttpStatus.OK
    }

    def 'there should be an failure if the user could not be updated with one time password'(){
        given:
        def userId = 'someId'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId
        def userString = getUserAsStringWithExtension('token')

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> new HttpClientRequestResult('body', 400)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'there should be an failure if no primary email was found'(){
        given:
        def userId = 'someId'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId
        def userString = getUserAsStringWithExtensionAndWithoutEmail('token')

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> new HttpClientRequestResult(userString, 200)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
        response.getBody() != null
    }

    def 'there should be an failure if the email content for confirmation mail was not found'(){
        given:
        def userId = 'someId'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId
        def userString = getUserAsStringWithExtension('token')

        when:
        def response = lostPasswordController.lost(authZHeader, userId)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> new HttpClientRequestResult(userString, 200)
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        response.getBody() != null
    }

    def 'The controller should verify the user and change its password'() {
        given:
        def otp = 'someOTP'
        def userId = 'someId'
        def newPassword = 'newPassword'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def userById = getUserAsStringWithExtension(otp)

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userById
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> 'updated user'

        result.getStatusCode() == HttpStatus.OK
        result.getBody() == 'updated user'
    }

    def 'If the user will not be found by his id the response should contain the appropriate status code'() {
        given:
        def otp = 'someOTP'
        def userId = 'someId'
        def newPassword = 'newPassword'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400

        result.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'If the provided one time password has no match with the saved one from the database the appropriate status code will be returned and the process is stopped'() {
        given:
        def otp = 'someOTP'
        def userId = 'someId'
        def newPassword = 'newPassword'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def userById = getUserAsStringWithExtension('Invalid OTP')

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userById
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        result.getStatusCode() == HttpStatus.FORBIDDEN
    }

    def 'there should be a failure if the user update with extensions failed'() {
        given:
        def otp = 'someOTP'
        def userId = 'someId'
        def newPassword = 'newPassword'
        def authZHeader = 'Bearer ACCESSTOKEN'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def userById = getUserAsStringWithExtension('someOTP')

        when:
        def result = lostPasswordController.change(authZHeader, otp, userId, newPassword)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * httpClientMock.executeHttpGet(uri, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        1 * requestResultMock.getStatusCode() >> 200
        1 * requestResultMock.getBody() >> userById
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> requestResultMock
        2 * requestResultMock.getStatusCode() >> 400
        result.getStatusCode() == HttpStatus.BAD_REQUEST
        result.getBody() != null
    }

    def 'there should be a failure if the provided one time password is empty'() {
        when:
        def result = lostPasswordController.change('authZ', '', 'userId', 'newPW')

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def 'The controller should provide a html form for entering the new password with already known values like otp and user id'(){
        given:
        def servletResponseMock = Mock(HttpServletResponse)
        def servletResponseOutputStream = Mock(ServletOutputStream)
        def otp = 'otp'
        def userId = 'userID'

        def inputStream = new ByteArrayInputStream('some html with placeholder \$CHANGELINK, \$OTP, \$USERID'.bytes)

        when:
        lostPasswordController.lostForm(otp, userId, servletResponseMock)

        then:
        1 * contextMock.getResourceAsStream('/WEB-INF/registration/change_password.html') >> inputStream
        1 * servletResponseMock.getOutputStream() >> servletResponseOutputStream
    }

    def getUserAsStringWithExtension(String otp) {
        def emails = new Email.Builder().setPrimary(true).setValue('email@example.org').build()

        Extension extension = new Extension(urn)
        extension.addOrUpdateField('oneTimePassword', otp)

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