package com.ai.agent.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmailRequest {

    private String to;
    private String subject;
    private String body;
}