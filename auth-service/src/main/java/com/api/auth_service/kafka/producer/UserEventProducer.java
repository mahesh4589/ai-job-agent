package com.api.auth_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_TOPIC = "user-events";

    public void sendUserCreatedEvent(Object event) {
        kafkaTemplate.send(USER_TOPIC, event);
    }
}