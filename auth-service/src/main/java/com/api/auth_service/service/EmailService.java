package com.api.auth_service.service;

public interface EmailService {
    void sendSimpleMail(String to, String subject, String body);
}
