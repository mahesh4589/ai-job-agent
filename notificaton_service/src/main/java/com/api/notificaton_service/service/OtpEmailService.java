package com.api.notificaton_service.service;

import com.api.notificaton_service.kafka.event.OtpRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpEmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public OtpEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendOtp(OtpRequestedEvent event) {
        String subject;
        String purposeText;

        switch (event.purpose()) {
            case "REGISTRATION" -> {
                subject = "AI Job Agent - Verify your email";
                purposeText = "verify your email address";
            }
            case "PASSWORD_RESET" -> {
                subject = "AI Job Agent - Password reset OTP";
                purposeText = "reset your password";
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported OTP purpose: " + event.purpose()
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(event.email());
        message.setSubject(subject);
        message.setText(
                "Your OTP to " + purposeText + " is: " + event.otp()
                        + "\n\nIt expires at " + event.expiresAt() + "."
        );

        mailSender.send(message);
    }
}