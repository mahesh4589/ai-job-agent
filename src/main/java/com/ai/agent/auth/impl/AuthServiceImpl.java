package com.ai.agent.auth.impl;

import com.ai.agent.auth.AuthService;
import com.ai.agent.dto.LoginDto;
import com.ai.agent.dto.LoginResponse;
import com.ai.agent.dto.UserResponse;
import com.ai.agent.dto.Userdto;
import com.ai.agent.entity.RefreshToken;
import com.ai.agent.entity.User;
import com.ai.agent.service.EmailService;
import com.ai.agent.service.RefreshTokenService;
import com.ai.agent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final EmailService emailService;

    private final RefreshTokenService service;


    private JwtService jwtService;

    public AuthServiceImpl(UserService userService, EmailService emailService, RefreshTokenService service, JwtService jwtService) {
        this.userService = userService;
        this.emailService = emailService;
        this.service = service;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse userRegister(Userdto userdto) {
        log.info("Registering new user with email: {}", userdto.getEmailId());
        UserResponse res = userService.createNewUser(userdto);
        emailService.sendSimpleMail(
                userdto.getEmailId(),
                "Welcome to AI Agent",
                "Hi " + userdto.getFirstName() +
                        ",\n\nWelcome to AI Agent!\n\nYour account has been created successfully.\n\nRegards,\nAI Agent Team");


        return res;

    }

    @Override
    public LoginResponse userLogin(LoginDto loginDto) {
        log.info("Authenticating user: {}", loginDto.getEmailId());
        User record = userService.userLogin(loginDto);
        log.debug("User found in database.");
        log.debug("Validating password.");
        log.info("Generating JWT token for {}", record.getEmailId());
        String res = jwtService.generateToken(record.getEmailId());
        log.info("JWT generated successfully.");


        RefreshToken refreshToken =
                service.createRefreshToken(record);

        LoginResponse d = new LoginResponse();
        d.setEmail(record.getEmailId());
        d.setExpiresIn(3600L);
        d.setFirstName(record.getFirstName());
        d.setTokenType("Bearer");
        d.setAccessToken(res);
        d.setRefreshToken(refreshToken.getToken());
        return d;
    }

    @Override
    public LoginResponse tokenRefresh(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        RefreshToken token =
                service.verifyRefreshToken(refreshToken);

        User user = token.getUser();

        String accessToken =
                jwtService.generateToken(user.getEmailId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);

        response.setRefreshToken(refreshToken);

        response.setTokenType("Bearer");

        response.setExpiresIn(3600L);

        response.setEmail(user.getEmailId());

        response.setFirstName(user.getFirstName());

        return response;
    }

    @Override
    public void logout(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        service.revokeRefreshToken(refreshToken);
    }
}
