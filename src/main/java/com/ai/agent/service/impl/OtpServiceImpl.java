package com.ai.agent.service.impl;

import com.ai.agent.dao.OtpRepository;
import com.ai.agent.dao.UserServceRepository;
import com.ai.agent.dto.ForgotPasswordRequest;
import com.ai.agent.dto.ResetPasswordRequest;
import com.ai.agent.dto.VerifyOtpRequest;
import com.ai.agent.entity.Otp;
import com.ai.agent.entity.User;
import com.ai.agent.service.EmailService;
import com.ai.agent.service.OtpService;
import com.ai.agent.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserService userService;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServceRepository userServceRepository;

    @Override
    public void sendOtp(ForgotPasswordRequest request) {

        log.info("Forgot password request received for email: {}", request.getEmail());

        User user = userService.findRecordByEmailId(request.getEmail());

        // Invalidate previous unused OTP
        Optional<Otp> existingOtp =
                otpRepository.findTopByEmailOrderByCreatedAtDesc(request.getEmail());

        existingOtp.ifPresent(otp -> {
            if (!otp.isUsed()) {
                otp.setUsed(true);
                otpRepository.save(otp);
                log.info("Previous OTP invalidated for email: {}", request.getEmail());
            }
        });

        // Generate 6-digit OTP
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);

        Otp newOtp = Otp.builder()
                .email(request.getEmail())
                .otp(String.valueOf(otp))
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(newOtp);

        log.info("OTP saved successfully for email: {}", request.getEmail());

        emailService.sendSimpleMail(
                user.getEmailId(),
                "Your OTP Verification Code",
                "Hello,\n\n"
                        + "Your One-Time Password (OTP) is: " + otp + "\n\n"
                        + "This OTP is valid for 5 minutes.\n"
                        + "Please do not share this OTP with anyone.\n\n"
                        + "Regards,\n"
                        + "AI Job Agent Team"
        );

        log.info("OTP email sent successfully to {}", request.getEmail());
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {

        log.info("Verifying OTP for email: {}", request.getEmail());

        Otp otpRecord = otpRepository
                .findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpRecord.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        log.info("OTP verified successfully for email: {}", request.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        log.info("Reset password request received for email: {}", request.getEmail());

        Otp otpRecord = otpRepository
                .findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpRecord.isUsed()) {
            throw new RuntimeException("OTP already used");
        }

        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        User user = userService.findRecordByEmailId(request.getEmail());

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userServceRepository.save(user);

        // Mark OTP as used
        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);

        log.info("Password reset successfully for email: {}", request.getEmail());
    }
}