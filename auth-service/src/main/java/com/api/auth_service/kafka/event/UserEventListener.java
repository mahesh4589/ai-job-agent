package com.api.auth_service.kafka.event;

import com.api.auth_service.kafka.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEventListener {

    @KafkaListener(topics = "user-events", groupId = "auth-service-group")
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: {}", event);

        // Process the event (e.g., send a welcome email, update a database, etc.)
        log.info("Processing user registration for userId: {}, email: {}", event.userId(), event.email());
    }
}