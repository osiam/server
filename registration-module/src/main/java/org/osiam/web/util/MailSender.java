package org.osiam.web.util;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 12.11.13
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MailSender {

    public void sendMail(MimeMessage msg ) throws MessagingException {
        Transport.send(msg);
    }
}
