package com.ai.agent.controller;


import com.ai.agent.auth.AuthService;
import com.ai.agent.dto.*;
import com.ai.agent.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API", description = "Authentication Operations")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register User",
            description = "Create a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> userRegister(
            @Valid @RequestBody Userdto userdto) {
        log.info("Registration request received for email: {}", userdto.getEmailId());
        UserResponse response = authService.userRegister(userdto);
        log.info("Registration completed successfully for email: {}", response.getEmailId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Registration successful",
                        response,
                        HttpStatus.CREATED.value()));
    }

    @Operation(
            summary = "User Login",
            description = "Authenticate user and generate JWT Access Token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLogin(
            @Valid @RequestBody LoginDto loginDto) {
        log.info("Login request received for email: {}", loginDto.getEmailId());

        LoginResponse response = authService.userLogin(loginDto);
        log.info("Login successful for email: {}", response.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response,
                        HttpStatus.OK.value()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> tokenRefresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.tokenRefresh(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Token refreshed successfully",
                        response,
                        HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody LogoutRequest request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logout successful",
                        "User logged out successfully",
                        HttpStatus.OK.value()));
    }
}