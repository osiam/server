/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.web.template

import org.osiam.web.controller.RegisterController
import org.osiam.web.exception.OsiamException
import org.osiam.web.mail.SendEmail

import spock.lang.Specification

class RenderAndSendEmailTest extends Specification {
    
    SendEmail sendMailService = Mock()
    EmailTemplateRenderer emailTemplateRendererService = Mock()
    RenderAndSendEmail renderAndSendEmail = new RenderAndSendEmail(sendMailService: sendMailService, 
        emailTemplateRendererService: emailTemplateRendererService)

    def 'there should be an failure if template file was not found'() {
        given:
        def subject = 'subject'
        
        when:
        renderAndSendEmail.renderAndSendEmail('templateName', 'fromAddress', 'toAddress', null, null)

        then:
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> subject
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        thrown(OsiamException)
    }

    def 'there should be an failure in change email if email body not found'() {
        given:
        def subject = 'subject'

        when:
        renderAndSendEmail.renderAndSendEmail('templateName', 'fromAddress', 'toAddress', null, null)

        then:
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> subject
        1 * emailTemplateRendererService.renderEmailBody(_, _, _) >> {throw new OsiamException()}
        thrown(OsiamException)
    }
    
    def 'there should be an failure if email subject not found'() {
        given:

        when:
        renderAndSendEmail.renderAndSendEmail('templateName', 'fromAddress', 'toAddress', null, null)

        then:
        1 * emailTemplateRendererService.renderEmailSubject(_, _, _) >> {throw new OsiamException()}
        thrown(OsiamException)
    }
}