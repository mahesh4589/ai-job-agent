package com.api.notificaton_service.kafka.consumer;

import com.api.notificaton_service.kafka.event.OtpRequestedEvent;
import com.api.notificaton_service.service.OtpEmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OtpRequestedListener {

    private final ObjectMapper objectMapper;
    private final OtpEmailService otpEmailService;

    public OtpRequestedListener(
            ObjectMapper objectMapper,
            OtpEmailService otpEmailService
    ) {
        this.objectMapper = objectMapper;
        this.otpEmailService = otpEmailService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.otp-requested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) throws Exception {
        OtpRequestedEvent event =
                objectMapper.readValue(message, OtpRequestedEvent.class);

        otpEmailService.sendOtp(event);
    }
}