package com.api.auth_service.kafka.producer;

import com.api.auth_service.kafka.event.OtpRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class OtpEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public OtpEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.otp-requested:otp-requested}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(OtpRequestedEvent event) {
        try {
            kafkaTemplate.send(topic, event.email(), event).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing OTP notification", exception);
        } catch (ExecutionException | TimeoutException exception) {
            throw new IllegalStateException("Could not publish OTP notification", exception);
        }
    }
}
