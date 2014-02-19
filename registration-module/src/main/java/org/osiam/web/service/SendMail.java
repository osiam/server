package org.osiam.web.service;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendMail {

    @Inject
    private JavaMailSender mailSender;

    public void sendPlainTextMail(String fromAddress, String toAddress, String subject, String content) throws MessagingException, IOException {
        mailSender.send(getMessage(fromAddress, toAddress, subject, content));
    }
    
    public void sendHTMLMail(String fromAddress, String toAddress, String subject, String htmlContent) throws MessagingException, IOException {
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
    
    private MimeMessage getMimeMessage(String fromAddress, String toAddress, String subject, String mailContent) throws MessagingException {
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject(subject);
        message.setText(mailContent, true);
        return mimeMessage;
    }
}
