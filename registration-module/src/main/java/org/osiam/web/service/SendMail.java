package org.osiam.web.service;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class SendMail {

    @Inject
    private MailSender mailSender;

    public void sendMail(String fromAddress, String toAddress, String subject, String content) throws MessagingException, IOException {
        SimpleMailMessage message = getMessage(fromAddress, toAddress, subject, content);
        mailSender.send(message);
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
}
