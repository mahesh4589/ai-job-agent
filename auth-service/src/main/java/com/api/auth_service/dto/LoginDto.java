package com.api.auth_service.dto;
import jakarta.validation.constraints.*; import lombok.Data;
@Data public class LoginDto { @NotBlank @Email private String emailId; @NotBlank private String password; }
