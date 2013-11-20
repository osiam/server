package org.osiam.web.util;

import org.apache.commons.io.IOUtils;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

    public void sendMail(String fromAddress, String toAddress, String subject, InputStream mailContent,
                         Map<String, String> mailContentReplacements) throws MessagingException, IOException {

        String strMailContent = IOUtils.toString(mailContent, "UTF-8");
        for (Map.Entry<String, String> entry : mailContentReplacements.entrySet()) {
            strMailContent = strMailContent.replace(entry.getKey(), entry.getValue());
        }

        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
        msg.addFrom(InternetAddress.parse(fromAddress));
        msg.addRecipient(Message.RecipientType.TO, InternetAddress.parse(toAddress)[0]);
        msg.addHeader("Subject", MimeUtility.encodeText(subject));
        msg.setContent(strMailContent, "text/plain");
        transportMail(msg);
    }

    public String extractPrimaryEmail(User user) {
        String foundEmail = null;
        for (MultiValuedAttribute email : user.getEmails()) {
            if (email.isPrimary()) {
                foundEmail = (String) email.getValue();
            }
        }
        return foundEmail;
    }
}
