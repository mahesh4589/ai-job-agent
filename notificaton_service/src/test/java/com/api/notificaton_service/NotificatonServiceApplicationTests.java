package com.api.notificaton_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.admin.auto-create=false",
        "eureka.client.enabled=false"
})
class NotificatonServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}