package com.api.auth_service.service.impl;

import com.api.auth_service.dto.*;
import com.api.auth_service.entity.User;
import com.api.auth_service.repository.UserRepository;
import com.api.auth_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final StringRedisTemplate redis;
    private final UserRepository users;
    private final UserService userService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public void sendRegistrationOtp(String email) {
        send(normalize(email), "REGISTRATION");
    }

    public void verifyRegistrationOtp(VerifyOtpRequest r) {
        String email = normalize(r.getEmail());
        User u = users.findByEmailIdIgnoreCase(email).orElse(null);
        verify(email, r.getOtp(), "REGISTRATION");
        if (u == null) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        u.setEmailVerified(true);
        users.save(u);
    }

    public void sendOtp(ForgotPasswordRequest r) {
        String email = normalize(r.getEmail());
        if (users.findByEmailIdIgnoreCase(email).isPresent()) {
            send(email, "PASSWORD_RESET");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest r) {
        String email = normalize(r.getEmail());
        verify(email, r.getOtp(), "PASSWORD_RESET");
        User u = userService.findRecordByEmailId(email);
        u.setPassword(encoder.encode(r.getNewPassword()));
        users.save(u);
        refreshTokenService.revokeAllForUser(u);
    }

    private void send(String email, String purpose) {
        String otpKey = key(purpose, email);
        String resendKey = resendKey(purpose, email);
        Boolean allowed = redis.opsForValue().setIfAbsent(resendKey, "1", RESEND_COOLDOWN);
        if (!Boolean.TRUE.equals(allowed)) {
            throw new IllegalStateException("Please wait before requesting another OTP");
        }

        String otp = "%06d".formatted(random.nextInt(1_000_000));
        String hash = encoder.encode(otp);
        redis.opsForValue().set(otpKey, hash, TTL);
        redis.delete(attemptKey(purpose, email));
        try {
            emailService.sendSimpleMail(email,
                    purpose.equals("REGISTRATION")
                            ? "AI Job Agent - Verify your email"
                            : "AI Job Agent - Password reset OTP",
                    "Your OTP is: " + otp + "\n\nIt expires in 5 minutes.");
        } catch (RuntimeException exception) {
            redis.delete(otpKey);
            redis.delete(resendKey);
            throw exception;
        }
    }

    private void verify(String email, String otp, String purpose) {
        String otpKey = key(purpose, email);
        String attemptKey = attemptKey(purpose, email);
        String attemptsValue = redis.opsForValue().get(attemptKey);
        int attempts = attemptsValue == null ? 0 : Integer.parseInt(attemptsValue);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            redis.delete(otpKey);
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        String hash = redis.opsForValue().get(otpKey);
        if (hash == null || !encoder.matches(otp, hash)) {
            // Initialize with a TTL before incrementing so a process crash cannot leave a permanent key.
            redis.opsForValue().setIfAbsent(attemptKey, "0", TTL);
            Long failedAttempts = redis.opsForValue().increment(attemptKey);
            if (failedAttempts != null && failedAttempts >= MAX_FAILED_ATTEMPTS) {
                redis.delete(otpKey);
            }
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Compare-and-consume: only one concurrent verification can use this code.
        String consumedHash = redis.opsForValue().getAndDelete(otpKey);
        if (consumedHash == null || !encoder.matches(otp, consumedHash)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        redis.delete(attemptKey);
    }

    private String key(String purpose, String email) {
        return "auth:otp:" + purpose + ":" + email;
    }

    private String attemptKey(String purpose, String email) {
        return key(purpose, email) + ":attempts";
    }

    private String resendKey(String purpose, String email) {
        return key(purpose, email) + ":resend-cooldown";
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
