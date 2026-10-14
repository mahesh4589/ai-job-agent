package com.api.auth_service.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(String eventId, Integer userId, String email, String firstName, Instant occurredAt) {
    public static UserRegisteredEvent of(Integer id, String email, String firstName) {
        return new UserRegisteredEvent(UUID.randomUUID().toString(), id, email, firstName, Instant.now());
    }
}