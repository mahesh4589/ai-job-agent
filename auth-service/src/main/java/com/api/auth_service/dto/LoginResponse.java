package com.api.auth_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    Long expiresIn;
    String email;
    String firstName;
}
