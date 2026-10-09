package com.api.auth_service.dto;
import jakarta.validation.constraints.*; import lombok.Data;
@Data public class VerifyOtpRequest { @NotBlank @Email private String email; @NotBlank @Pattern(regexp="\\d{6}") private String otp; }
