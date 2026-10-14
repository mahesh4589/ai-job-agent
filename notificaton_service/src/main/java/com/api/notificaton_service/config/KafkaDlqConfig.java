package com.api.notificaton_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDlqConfig {

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<Object, Object> producerFactory =
                new DefaultKafkaProducerFactory<>(properties);

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> dltKafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        dltKafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(record.topic() + "-dlt", record.partition())
                );

        // 2 retries after the first attempt = 3 total attempts.
        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1_000L, 2L)
        );
    }

    @Bean
    public NewTopic otpRequestedTopic(
            @Value("${app.kafka.topics.otp-requested}") String topic
    ) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic otpRequestedDltTopic(
            @Value("${app.kafka.topics.otp-requested}") String topic
    ) {
        return TopicBuilder.name(topic + "-dlt")
                .partitions(1)
                .replicas(1)
                .build();
    }
}