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

import org.apache.commons.io.IOUtils;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Class for sending mails.
 * Also getting primary email from users email list and loads the mail content as stream.
 * @author Igor, Jochen Todea
 */
@Component
public class MailSender {

    @Value("${osiam.mailServer.smtp.port}")
    private int smtpPort;

    @Value("${osiam.mailServer.host.name}")
    private String mailServerHost;

    void transportMail(MimeMessage msg ) throws MessagingException {
        Transport.send(msg);
    }

    public void sendMail(String fromAddress, String toAddress, String subject, InputStream mailContent,
                         Map<String, String> mailContentReplacements) throws MessagingException, IOException {

        String strMailContent = IOUtils.toString(mailContent, "UTF-8");
        if (mailContentReplacements != null) {
            for (Map.Entry<String, String> entry : mailContentReplacements.entrySet()) {
                strMailContent = strMailContent.replace(entry.getKey(), entry.getValue());
            }
        }

        MimeMessage msg = getMimeMessage(fromAddress, InternetAddress.parse(toAddress)[0], subject, strMailContent);
        transportMail(msg);
    }

    private MimeMessage getMimeMessage(String fromAddress, InternetAddress address, String subject,
                                       String strMailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(getMailServerProperties()));
        msg.addFrom(InternetAddress.parse(fromAddress));
        msg.addRecipient(Message.RecipientType.TO, address);
        msg.addHeader("Subject", MimeUtility.encodeText(subject));
        msg.setContent(strMailContent, "text/plain");
        return msg;
    }

    Properties getMailServerProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.host", mailServerHost);
        return properties;
    }

    public String extractPrimaryEmail(User user) {
        String foundEmail = null;
        for (MultiValuedAttribute email : user.getEmails()) {
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
