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

package org.osiam.web.mail;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Send email service for sending an email
 * 
 */
@Service
public class SendEmail {

    @Inject
    private JavaMailSender mailSender;

    public void sendPlainTextMail(String fromAddress, String toAddress, String subject, String content)
            throws MessagingException, IOException {
        mailSender.send(getMessage(fromAddress, toAddress, subject, content));
    }

    public void sendHTMLMail(String fromAddress, String toAddress, String subject, String htmlContent)
            throws MessagingException, IOException {
        mailSender.send(getMimeMessage(fromAddress, toAddress, subject, htmlContent));
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

    private MimeMessage getMimeMessage(String fromAddress, String toAddress, String subject, String mailContent)
            throws MessagingException {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject(subject);
        message.setText(mailContent, true);
        message.setSentDate(new Date(System.currentTimeMillis()));
        return mimeMessage;
    }
}
