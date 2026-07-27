package com.ai.agent.service;

public interface EmailService {


    void sendSimpleMail(String to,
                        String subject,
                        String body);


}
