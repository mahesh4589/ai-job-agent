package com.api.auth_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AI Job Agent - Authentication API",
                version = "v1",
                description = "Registration, email OTP verification, login, refresh-token rotation, logout, and password recovery APIs.",
                contact = @Contact(name = "AI Job Agent")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the access token returned by login or refresh. Do not include the 'Bearer ' prefix."
)
public class OpenApiConfig {
}
