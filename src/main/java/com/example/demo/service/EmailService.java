package com.example.demo.service;

import com.example.demo.entity.Customers;
import com.example.demo.entity.VerificationToken;
import com.example.demo.exception.EmailFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${front.url}")
    private String frontUrl;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage simpleMailMessage = makeMailMessage();
        simpleMailMessage.setTo(verificationToken.getCustomers().getEmail());
        simpleMailMessage.setSubject("Verify your account.");
        simpleMailMessage.setText("Please follow the link bellow to verify your email to activate your account.\n" + frontUrl + "/auth/verify?token=" + verificationToken.getToken());
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }

    public void sendResetPasswordEmail(Customers customers, String token) throws EmailFailureException {
        SimpleMailMessage simpleMailMessage = makeMailMessage();
        simpleMailMessage.setTo(customers.getEmail());
        simpleMailMessage.setSubject("Reset your password.");
        simpleMailMessage.setText("Please follow the link bellow to reset your password to login to our System.\n" + frontUrl + "/auth/forgot?token=" + token);
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }
}
