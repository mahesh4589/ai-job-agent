package com.api.notificaton_service.kafka.event;

import java.time.Instant;

public record OtpRequestedEvent(
        String eventId,
        String email,
        String purpose,
        String otp,
        Instant expiresAt
) {}