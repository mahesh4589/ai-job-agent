package com.api.notificaton_service.service;

import com.api.notificaton_service.kafka.event.OtpRequestedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.ArgumentCaptor;

class OtpEmailServiceTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final OtpEmailService service = new OtpEmailService(mailSender, "no-reply@example.test");

    @Test
    void sendsRegistrationOtpEmail() {
        OtpRequestedEvent event = new OtpRequestedEvent(
                "event-1", "asha@example.test", "REGISTRATION", "123456", Instant.parse("2026-10-16T10:30:00Z"));

        service.sendOtp(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("no-reply@example.test", message.getFrom());
        assertEquals("asha@example.test", message.getTo()[0]);
        assertEquals("AI Job Agent - Verify your email", message.getSubject());
        assertTrue(message.getText().contains("123456"));
        assertTrue(message.getText().contains("verify your email address"));
    }

    @Test
    void sendsPasswordResetOtpEmail() {
        OtpRequestedEvent event = new OtpRequestedEvent(
                "event-2", "asha@example.test", "PASSWORD_RESET", "654321", Instant.parse("2026-10-16T10:30:00Z"));

        service.sendOtp(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertEquals("AI Job Agent - Password reset OTP", captor.getValue().getSubject());
        assertTrue(captor.getValue().getText().contains("reset your password"));
    }

    @Test
    void rejectsUnsupportedPurposeWithoutSendingEmail() {
        OtpRequestedEvent event = new OtpRequestedEvent(
                "event-3", "asha@example.test", "OTHER", "123456", Instant.now());

        assertThrows(IllegalArgumentException.class, () -> service.sendOtp(event));
        verifyNoInteractions(mailSender);
    }
}