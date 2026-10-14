package com.api.auth_service.service.impl;

import com.api.auth_service.auth.JwtService;
import com.api.auth_service.dto.*;
import com.api.auth_service.entity.*;
import com.api.auth_service.kafka.event.UserRegisteredEvent;
import com.api.auth_service.kafka.producer.UserEventProducer;
import com.api.auth_service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService users;
    private final OtpService otp;
    private final JwtService jwt;
    private final RefreshTokenService refresh;
    private final UserEventProducer events;

    public UserResponse userRegister(Userdto d) {
        UserResponse r = users.createNewUser(d);
        otp.sendRegistrationOtp(r.getEmailId());
        events.sendUserCreatedEvent(UserRegisteredEvent.of(r.getId(), r.getEmailId(), r.getFirstName()));
        log.info("Registration completed and user-registered event published userId={}", r.getId());
        return r;
    }

    public LoginResponse userLogin(LoginDto d) {
        User u = users.userLogin(d);
        String access = jwt.generateToken(u.getEmailId());
        String refreshToken = refresh.create(u);
        log.info("User login completed userId={}", u.getId());
        return LoginResponse.builder().accessToken(access).refreshToken(refreshToken).tokenType("Bearer").expiresIn(jwt.getAccessTtlSeconds()).email(u.getEmailId()).firstName(u.getFirstName()).build();
    }

    @Transactional
    public LoginResponse tokenRefresh(String token) {
        RefreshTokenService.RefreshTokenRotation rotation = refresh.rotate(token);
        User u = rotation.user();
        log.info("Refresh token rotated successfully userId={}", u.getId());
        return LoginResponse.builder().accessToken(jwt.generateToken(u.getEmailId())).refreshToken(rotation.token()).tokenType("Bearer").expiresIn(jwt.getAccessTtlSeconds()).email(u.getEmailId()).firstName(u.getFirstName()).build();
    }

    public void logout(String token) {
        refresh.revoke(token);
        log.info("Logout request processed");
    }
}
