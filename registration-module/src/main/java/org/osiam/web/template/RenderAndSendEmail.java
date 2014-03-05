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

package org.osiam.web.template;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.osiam.resources.scim.User;
import org.osiam.web.mail.SendEmail;
import org.springframework.stereotype.Component;

@Component
public class RenderAndSendEmail {

    @Inject
    private SendEmail sendMailService;

    @Inject
    private EmailTemplateRenderer emailTemplateRendererService;

    public void renderAndSendEmail(String templateName, String fromAddress, String toAddress, User user,
            Map<String, String> mailVariables) throws IOException,
            MessagingException {

        String emailSubject = emailTemplateRendererService.renderEmailSubject(templateName, user, mailVariables);

        String emailBody = emailTemplateRendererService.renderEmailBody(templateName, user, mailVariables);

        sendMailService.sendHTMLMail(fromAddress, toAddress, emailSubject, emailBody);
    }
}
