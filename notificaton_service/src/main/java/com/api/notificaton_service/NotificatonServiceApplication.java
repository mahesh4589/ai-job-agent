package com.api.notificaton_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificatonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificatonServiceApplication.class, args);
	}

}
