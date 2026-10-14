package com.api.auth_service.controller;

import com.api.auth_service.dto.*;
import com.api.auth_service.service.AuthService;
import com.api.auth_service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Account registration and authentication lifecycle")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @Operation(summary = "Register a new user", description = "Creates an unverified account and sends a registration OTP to the supplied email address.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody Userdto userdto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. OTP sent.", authService.userRegister(userdto), HttpStatus.CREATED.value()));
    }

    @Operation(summary = "Verify registration OTP", description = "Verifies the email address using the OTP sent during registration.")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verify(@Valid @RequestBody VerifyOtpRequest request) {
        otpService.verifyRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", "Email verified", HttpStatus.OK.value()));
    }

    @Operation(summary = "Login", description = "Authenticates a verified user and returns a short-lived access JWT and a refresh token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginDto request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.userLogin(request), HttpStatus.OK.value()));
    }

    @Operation(summary = "Refresh tokens", description = "Rotates the refresh token and returns a new access JWT and refresh token. Use the new refresh token for the next refresh; the submitted token is revoked.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authService.tokenRefresh(request.getRefreshToken()), HttpStatus.OK.value()));
    }

    @Operation(summary = "Logout", description = "Revokes the submitted refresh token. A revoked token cannot be refreshed.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out", HttpStatus.OK.value()));
    }

    @Operation(summary = "Request password-reset OTP", description = "Sends a password-reset OTP to the supplied email address.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgot(@Valid @RequestBody ForgotPasswordRequest request) {
        otpService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", "Success", HttpStatus.OK.value()));
    }

    @Operation(summary = "Reset password", description = "Resets the password after validating the email and password-reset OTP.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> reset(@Valid @RequestBody ResetPasswordRequest request) {
        otpService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", "Success", HttpStatus.OK.value()));
    }
}
