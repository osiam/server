package org.osiam.web.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 12.11.13
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MailSender {

    void transportMail(MimeMessage msg ) throws MessagingException {
        Transport.send(msg);
    }

    public void sendMail(String fromAddress, String toAddress, String subject, String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
        msg.addFrom(InternetAddress.parse(fromAddress));
        msg.addRecipient(Message.RecipientType.TO, InternetAddress.parse(toAddress)[0]);
        msg.addHeader("Subject", MimeUtility.encodeText(subject));
        msg.setContent(mailContent, "text/plain");
        transportMail(msg);
    }
}
