package com.api.auth_service.service.impl;

import com.api.auth_service.dto.*;
import com.api.auth_service.entity.User;
import com.api.auth_service.repository.UserRepository;
import com.api.auth_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final StringRedisTemplate redis;
    private final UserRepository users;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();
    private static final Duration TTL = Duration.ofMinutes(5);

    public void sendRegistrationOtp(String email) {
        send(email.trim().toLowerCase(), "REGISTRATION");
    }

    public void verifyRegistrationOtp(VerifyOtpRequest r) {
        String email = r.getEmail().trim().toLowerCase();
        User u = userService.findRecordByEmailId(email);
        verify(email, r.getOtp(), "REGISTRATION");
        u.setEmailVerified(true);
        users.save(u);
    }

    public void sendOtp(ForgotPasswordRequest r) {
        userService.findRecordByEmailId(r.getEmail());
        send(r.getEmail().trim().toLowerCase(), "PASSWORD_RESET");
    }

    public void resetPassword(ResetPasswordRequest r) {
        String email = r.getEmail().trim().toLowerCase();
        verify(email, r.getOtp(), "PASSWORD_RESET");
        User u = userService.findRecordByEmailId(email);
        u.setPassword(encoder.encode(r.getNewPassword()));
        users.save(u);
    }

    private void send(String email, String purpose) {
        String otp = "%06d".formatted(random.nextInt(1_000_000));
        String hash = encoder.encode(otp);
        redis.opsForValue().set(key(purpose, email), hash, TTL);
        emailService.sendSimpleMail(email, purpose.equals("REGISTRATION") ? "AI Job Agent - Verify your email" : "AI Job Agent - Password reset OTP", "Your OTP is: " + otp + "\n\nIt expires in 5 minutes.");
    }

    private void verify(String email, String otp, String purpose) {
        String hash = redis.opsForValue().get(key(purpose, email));
        if (hash == null || !encoder.matches(otp, hash)) throw new IllegalArgumentException("Invalid or expired OTP");
        redis.delete(key(purpose, email));
    }

    private String key(String purpose, String email) {
        return "auth:otp:" + purpose + ":" + email;
    }
}
