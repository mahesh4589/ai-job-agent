package com.ai.agent.controller;

import com.ai.agent.dto.ApiResponse;
import com.ai.agent.dto.EmailRequest;
import com.ai.agent.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {


    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }


    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendEmail(
            @Valid @RequestBody EmailRequest emailRequest) {

        service.sendSimpleMail(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Email sent successfully",
                        "Success",
                        HttpStatus.OK.value()));
    }


}
