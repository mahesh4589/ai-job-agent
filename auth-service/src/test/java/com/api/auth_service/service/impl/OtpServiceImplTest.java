package com.api.auth_service.service.impl;

import com.api.auth_service.dto.VerifyOtpRequest;
import com.api.auth_service.entity.User;
import com.api.auth_service.kafka.producer.OtpEventProducer;
import com.api.auth_service.repository.UserRepository;
import com.api.auth_service.service.RefreshTokenService;
import com.api.auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    private static final String EMAIL = "asha@example.com";
    private static final String OTP_KEY = "auth:otp:REGISTRATION:" + EMAIL;
    private static final String ATTEMPT_KEY = OTP_KEY + ":attempts";

    @Mock private StringRedisTemplate redis;
    @Mock private UserRepository users;
    @Mock private UserService userService;
    @Mock private OtpEventProducer otpEventProducer;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordEncoder encoder;
    @Mock private ValueOperations<String, String> values;
    @InjectMocks private OtpServiceImpl otpService;

    @BeforeEach
    void setUpRedisOperations() {
        when(redis.opsForValue()).thenReturn(values);
    }

    @Test
    void verifiesOtpAndMarksEmailVerified() {
        User user = new User();
        user.setId(42);
        user.setEmailId(EMAIL);
        when(users.findByEmailIdIgnoreCase(EMAIL)).thenReturn(java.util.Optional.of(user));
        when(values.get(ATTEMPT_KEY)).thenReturn(null);
        when(values.get(OTP_KEY)).thenReturn("encoded-otp");
        when(encoder.matches("123456", "encoded-otp")).thenReturn(true);
        when(values.getAndDelete(OTP_KEY)).thenReturn("encoded-otp");

        otpService.verifyRegistrationOtp(request("123456"));

        assertTrue(user.isEmailVerified());
        verify(users).save(user);
        verify(redis).delete(ATTEMPT_KEY);
    }

    @Test
    void invalidOtpIsRejectedAndFailureCountIncrements() {
        when(users.findByEmailIdIgnoreCase(EMAIL)).thenReturn(java.util.Optional.empty());
        when(values.get(ATTEMPT_KEY)).thenReturn(null);
        when(values.get(OTP_KEY)).thenReturn("encoded-otp");
        when(encoder.matches("000000", "encoded-otp")).thenReturn(false);
        when(values.increment(ATTEMPT_KEY)).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> otpService.verifyRegistrationOtp(request("000000")));

        verify(values).increment(ATTEMPT_KEY);
        verify(users, never()).save(any(User.class));
    }

    @Test
    void otpIsDeletedOnceMaximumAttemptsHaveBeenReached() {
        when(values.get(ATTEMPT_KEY)).thenReturn("5");

        assertThrows(IllegalArgumentException.class, () -> otpService.verifyRegistrationOtp(request("123456")));

        verify(redis).delete(OTP_KEY);
        verify(values, never()).get(OTP_KEY);
    }

    private VerifyOtpRequest request(String code) {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail(EMAIL);
        request.setOtp(code);
        return request;
    }
}
