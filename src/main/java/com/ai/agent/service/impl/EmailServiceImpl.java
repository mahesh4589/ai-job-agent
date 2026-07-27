package com.ai.agent.service.impl;


import com.ai.agent.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;


import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


@Slf4j
@Service
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMail(String to,
                               String subject,
                               String body) {

        log.info("Sending email to {}", to);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {

            mailSender.send(message);

            log.info("Email sent successfully to {}", to);

        } catch (Exception exception) {

            log.error("Unable to send email", exception);

            throw new RuntimeException("Unable to send email");
        }
    }
}