package com.api.auth_service.service.impl;

import com.api.auth_service.auth.JwtService;
import com.api.auth_service.dto.*;
import com.api.auth_service.entity.*;
import com.api.auth_service.kafka.event.UserRegisteredEvent;
import com.api.auth_service.kafka.producer.UserEventProducer;
import com.api.auth_service.repository.UserRepository;
import com.api.auth_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService users;
    private final UserRepository userRepo;
    private final OtpService otp;
    private final JwtService jwt;
    private final RefreshTokenService refresh;
    private final UserEventProducer events;

    public UserResponse userRegister(Userdto d) {
        UserResponse r = users.createNewUser(d);
        otp.sendRegistrationOtp(r.getEmailId());
        events.sendUserCreatedEvent(UserRegisteredEvent.of(r.getId(), r.getEmailId(), r.getFirstName()));
        return r;
    }

    public LoginResponse userLogin(LoginDto d) {
        User u = users.userLogin(d);
        String access = jwt.generateToken(u.getEmailId());
        RefreshToken rt = refresh.create(u);
        return LoginResponse.builder().accessToken(access).refreshToken(rt.getToken()).tokenType("Bearer").expiresIn(jwt.getAccessTtlSeconds()).email(u.getEmailId()).firstName(u.getFirstName()).build();
    }

    public LoginResponse tokenRefresh(String token) {
        RefreshToken rt = refresh.verify(token);
        User u = rt.getUser();
        return LoginResponse.builder().accessToken(jwt.generateToken(u.getEmailId())).refreshToken(token).tokenType("Bearer").expiresIn(jwt.getAccessTtlSeconds()).email(u.getEmailId()).firstName(u.getFirstName()).build();
    }

    public void logout(String token) {
        refresh.revoke(token);
    }
}
