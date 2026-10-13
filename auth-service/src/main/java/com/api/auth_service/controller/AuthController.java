package com.api.auth_service.controller;

import com.api.auth_service.dto.*;
import com.api.auth_service.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody Userdto userdto) {
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(ApiResponse.success("Registration successful. OTP sent.", authService.userRegister(userdto), HttpStatus.CREATED.value()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verify(@Valid @RequestBody VerifyOtpRequest r) {
        otpService.verifyRegistrationOtp(r);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", "Email verified", HttpStatus.OK.value()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginDto d) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.userLogin(d), HttpStatus.OK.value()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest r) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authService.tokenRefresh(r.getRefreshToken()), HttpStatus.OK.value()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutRequest r) {
        authService.logout(r.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out", HttpStatus.OK.value()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgot(@Valid @RequestBody ForgotPasswordRequest r) {
        otpService.sendOtp(r);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", "Success", HttpStatus.OK.value()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> reset(@Valid @RequestBody ResetPasswordRequest r) {
        otpService.resetPassword(r);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", "Success", HttpStatus.OK.value()));
    }
}
