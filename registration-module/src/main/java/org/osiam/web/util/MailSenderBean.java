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

package org.osiam.web.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * Class for sending mails.
 * Also getting primary email from users email list and loads the mail content as stream.
 * @author Igor, Jochen Todea
 */
@Component
public class MailSenderBean {

    @Inject
    private MailSender mailSender;

    public void sendMail(String fromAddress, String toAddress, String subject, InputStream mailContent,
                         Map<String, String> mailContentReplacements) throws MessagingException, IOException {

        String strMailContent = getMailContentWithReplacementsAsString(mailContent, mailContentReplacements);
        SimpleMailMessage message = getMessage(fromAddress, toAddress, subject, strMailContent);
        mailSender.send(message);
    }

    private String getMailContentWithReplacementsAsString(InputStream mailContent, Map<String, String> mailContentReplacements) throws IOException {
        String strMailContent = IOUtils.toString(mailContent, "UTF-8");
        if (mailContentReplacements != null) {
            for (Map.Entry<String, String> entry : mailContentReplacements.entrySet()) {
                strMailContent = strMailContent.replace(entry.getKey(), entry.getValue());
            }
        }
        return strMailContent;
    }

    private SimpleMailMessage getMessage(String fromAddress, String toAddress, String subject, String mailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject(subject);
        message.setText(mailContent);
        message.setSentDate(new Date(System.currentTimeMillis()));
        return message;
    }

    public String extractPrimaryEmail(User user) {
        String foundEmail = null;
        for (Email email : user.getEmails()) {
            if (email.isPrimary()) {
                foundEmail = email.getValue();
            }
        }
        return foundEmail;
    }

    public InputStream getEmailContentAsStream(String defaultPath, String pathToContentFile, ServletContext context) throws FileNotFoundException {

        if(pathToContentFile == null || pathToContentFile.isEmpty()) {
            // Mail content with placeholders, default file from deployment
            return context.getResourceAsStream(defaultPath);
        }
        // Mail content with placeholders, user defined
        return new FileInputStream(pathToContentFile);
    }
}