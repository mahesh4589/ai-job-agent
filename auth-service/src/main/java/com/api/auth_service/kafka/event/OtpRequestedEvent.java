package com.api.auth_service.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record OtpRequestedEvent(
        String eventId,
        String email,
        String purpose,
        String otp,
        Instant expiresAt
) {
    public static OtpRequestedEvent of(String email, String purpose, String otp, Instant expiresAt) {
        return new OtpRequestedEvent(UUID.randomUUID().toString(), email, purpose, otp, expiresAt);
    }
}
