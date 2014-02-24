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
import org.osiam.web.service.AccessTokenInformationProvider;
import org.osiam.web.service.RegistrationExtensionUrnProvider;
import org.osiam.web.service.ResourceServerUriBuilder;
import org.osiam.web.template.EmailTemplateRenderer;
import org.osiam.web.template.RenderAndSendEmail;
import org.osiam.web.util.*
import org.springframework.http.HttpStatus

import spock.lang.Specification

class ChangeEmailControllerTest extends Specification {

    def mapper = new ObjectMapperWithExtensionConfig()

    def httpClientMock = Mock(HttpClientHelper)
    def resultMock = Mock(HttpClientRequestResult)
    def registrationExtensionUrnProvider = Mock(RegistrationExtensionUrnProvider)
    def resourceServerUriBuilder = Mock(ResourceServerUriBuilder)
    def accessTokenInformationProvider = Mock(AccessTokenInformationProvider)
    SendEmail sendMailService = Mock()
    EmailTemplateRenderer emailTemplateRendererService = Mock()
    RenderAndSendEmail renderAndSendEmailService = new RenderAndSendEmail(sendMailService: sendMailService, emailTemplateRendererService: emailTemplateRendererService);

    def urn = 'urn:scim:schemas:osiam:1.0:Registration'

    def confirmTokenField = 'emailConfirmToken'
    def tempMailField = 'tempMail'

    def emailChangeLinkPrefix = 'http://localhost:8080/stuff'
    def emailChangeMailFrom = 'bugs@bunny.com'
    def emailChangeMailSubject = 'email change'
    def emailChangeInfoMailSubject = 'email change done'
    def clientEmailChangeUri = 'http://test'

    def bootStrapLib = 'http://bootstrap'
    def angularLib = 'http://angular'
    def jqueryLib = 'http://jquery'

    def context = Mock(ServletContext)

    ChangeEmailController changeEmailController = new ChangeEmailController(httpClient: httpClientMock, confirmationTokenField: confirmTokenField,
    tempEmail: tempMailField, context: context, emailChangeLinkPrefix: emailChangeLinkPrefix,
    fromAddress: emailChangeMailFrom, registrationExtensionUrnProvider: registrationExtensionUrnProvider,
    resourceServerUriBuilder: resourceServerUriBuilder, accessTokenInformationProvider: accessTokenInformationProvider,
    mapper: mapper, clientEmailChangeUri: clientEmailChangeUri, bootStrapLib: bootStrapLib, angularLib: angularLib,
    jqueryLib: jqueryLib, renderAndSendEmailService: renderAndSendEmailService)

    def 'there should be an failure in change email if email template file was not found'() {
        given:
        def authZHeader = 'Bearer ACCESSTOKEN'
        def userId = 'theUserId'
        def newEmailValue = 'bam@boom.com'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        result.getBody() != null
    }

    def 'there should be an failure in change email if email body not found'() {
        given:
        def authZHeader = 'Bearer ACCESSTOKEN'
        def userId = 'theUserId'
        def newEmailValue = 'bam@boom.com'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        result.getBody() != null
    }
    
    def 'there should be an failure in change email if email subject not found'() {
        given:
        def authZHeader = 'Bearer ACCESSTOKEN'
        def userId = 'theUserId'
        def newEmailValue = 'bam@boom.com'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserAsString()

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> {throw new OsiamException()}
        result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        result.getBody() != null
    }

    def 'change email should generate a confirmation token, save the new email temporarily and send an email'() {
        given:
        def authZHeader = 'Bearer ACCESSTOKEN'
        def userId = 'theUserId'
        def newEmailValue = 'bam@boom.com'
        def uri = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserAsString()

        def emailContent = 'nine bytes and one placeholder $EMAILCHANGEURL and $BOOTSTRAP and $ANGULAR and $JQUERY'

        when:
        def result = changeEmailController.change(authZHeader, newEmailValue)

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZHeader) >> userId
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> uri
        1 * resultMock.getStatusCode() >> 200
        2 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(uri, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> 'subject'
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> emailContent
        1 * sendMailService.sendHTMLMail(_, _, _, _)
        result.getStatusCode() == HttpStatus.OK
    }

    def 'should catch IllegalArgumentException and returning response with error message'(){
        given:
        def authZ = 'invalid access token'

        when:
        def result = changeEmailController.change(authZ, 'some@email.de')

        then:
        1 * accessTokenInformationProvider.getUserIdFromToken(authZ) >> {throw new IllegalArgumentException('{\'error\':\'unauthorized\'}')}
        result.getBody() == '{\'error\':\'unauthorized\'}'
    }

    def 'confirm email should validate the confirmation token and save the new email value as primary email and send an email'() {
        given:
        def authZHeader = 'abc'
        def userId = 'userId'
        def confirmToken = 'confToken'
        def url = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserWithTempEmailAsString('confToken')
        def upatedUser = getUpdatedUser()

        def subjectContent = 'email change done'
        def emailContent = 'nine bytes and one placeholder'

        when:
        def result = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        2 * resultMock.getBody() >> updatedUser
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(url, _, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> subjectContent
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> emailContent
        1 * sendMailService.sendHTMLMail(emailChangeMailFrom, 'email@example.org', subjectContent, emailContent)

        result.getStatusCode() == HttpStatus.OK
        User savedUser = mapper.readValue(result.getBody(), User)
        savedUser.getEmails().size() == 2
        !savedUser.emails.find { it.value == 'email@example.org' }
    }

    def 'there should be an failure if retrieving user by id failed'() {
        given:
        def authZHeader = 'abc'
        def userId = 'userId'
        def confirmToken = 'confToken'
        def url = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'there should be an failure if confirmation token miss match'() {
        given:
        def authZHeader = 'abc'
        def userId = 'userId'
        def confirmToken = 'confToken'
        def url = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserWithTempEmailAsString('bullShit')

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        response.getStatusCode() == HttpStatus.FORBIDDEN
    }

    def 'there should be an failure if updating user with extensions failed'() {
        given:
        def authZHeader = 'abc'
        def userId = 'userId'
        def confirmToken = 'confToken'
        def url = 'http://localhost:8080/osiam-resource-server/Users/' + userId

        def user = getUserWithTempEmailAsString('confToken')

        when:
        def response = changeEmailController.confirm(authZHeader, userId, confirmToken)

        then:
        1 * resourceServerUriBuilder.buildUsersUriWithUserId(userId) >> url
        1 * httpClientMock.executeHttpGet(url, HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        1 * resultMock.getStatusCode() >> 200
        1 * resultMock.getBody() >> user
        1 * registrationExtensionUrnProvider.getExtensionUrn() >> urn
        1 * httpClientMock.executeHttpPatch(url, _,HttpHeader.AUTHORIZATION, authZHeader) >> resultMock
        2 * resultMock.getStatusCode() >> 400
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def 'there should be a failure if the provided confirmation token is empty'() {
        when:
        def result = changeEmailController.confirm('authZ', 'userId', '')

        then:
        result.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def 'the controller should provide a html form for entering the new email address'() {
        given:
        def servletResponseMock = Mock(HttpServletResponse)
        def servletOutputStream = Mock(ServletOutputStream)
        def inputStream = new ByteArrayInputStream('some html stuff with \$CHANGELINK placeholder'.bytes)

        when:
        changeEmailController.index(servletResponseMock)

        then:
        1 * context.getResourceAsStream('/WEB-INF/registration/change_email.html') >> inputStream
        1 * servletResponseMock.getOutputStream() >> servletOutputStream
    }
    
    def getUserAsString() {
        def emails = new Email.Builder().setPrimary(true).setValue('email@example.org').build()

        def user = new User.Builder('Boy George')
                .setPassword('password')
                .setEmails([emails])
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }

    def getUserWithTempEmailAsString(confToken) {
        def primary = new Email.Builder().setPrimary(true).setValue('email@example.org').build()
        def email = new Email.Builder().setPrimary(false).setValue('nonPrimary@example.org').build()


        def extension = new Extension(urn)
        extension.addOrUpdateField(confirmTokenField, confToken)
        extension.addOrUpdateField(tempMailField, 'newemail@example.org')

        def user = new User.Builder('Boy George')
                .setPassword('password')
                .setEmails([primary, email] as List)
                .setActive(false)
                .addExtension(extension)
                .build()

        return mapper.writeValueAsString(user)
    }

    def getUpdatedUser() {
        def primary = new Email.Builder().setPrimary(true).setValue('newemail@example.org').build()
        def email = new Email.Builder().setPrimary(false).setValue('nonPrimary@example.org').build()

        def user = new User.Builder('Boy George')
                .setPassword('password')
                .setEmails([primary, email] as List)
                .setActive(false)
                .build()

        return mapper.writeValueAsString(user)
    }
}