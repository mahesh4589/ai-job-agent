package com.api.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String otp;
    @NotBlank
    @Size(min = 8, max = 100)
    private String newPassword;
}
