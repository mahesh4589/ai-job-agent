package com.ai.agent.service.impl;


import com.ai.agent.auth.impl.JwtService;
import com.ai.agent.dao.RefreshTokenRepository;
import com.ai.agent.entity.RefreshToken;
import com.ai.agent.entity.User;
import com.ai.agent.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtService jwtService;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository repository,
            JwtService jwtService) {

        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Override
    public RefreshToken createRefreshToken(User user) {

        repository.findByUser(user)
                .ifPresent(repository::delete);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(
                jwtService.generateRefreshToken(user.getEmailId()));

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7));

        refreshToken.setRevoked(false);

        return repository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh Token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh Token already revoked");
        }

        refreshToken.setRevoked(true);

        repository.save(refreshToken);
    }

    @Override
    public void deleteByUser(User user) {
        repository.deleteByUser(user);
    }
}