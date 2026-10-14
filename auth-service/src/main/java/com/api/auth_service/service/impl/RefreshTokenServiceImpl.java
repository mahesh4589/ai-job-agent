package com.api.auth_service.service.impl;

import com.api.auth_service.entity.RefreshToken;
import com.api.auth_service.entity.User;
import com.api.auth_service.repository.RefreshTokenRepository;
import com.api.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.refresh-token-ttl-days:30}")
    private long ttlDays;

    @Override
    public String create(User user) {
        String rawToken = newRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        // Store only a one-way digest. The raw token is returned once to the caller.
        refreshToken.setToken(digest(rawToken));
        refreshToken.setExpiryDate(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);
        repository.save(refreshToken);
        return rawToken;
    }

    @Override
    @Transactional
    public RefreshTokenRotation rotate(String rawToken) {
        RefreshToken current = repository.findByTokenForUpdate(digest(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (current.isRevoked() || !current.getExpiryDate().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }

        current.setRevoked(true);
        repository.save(current);

        User user = current.getUser();
        String replacement = create(user);
        return new RefreshTokenRotation(user, replacement);
    }

    @Override
    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenForUpdate(digest(rawToken)).ifPresent(token -> {
            token.setRevoked(true);
            repository.save(token);
        });
    }

    @Override
    @Transactional
    public void revokeAllForUser(User user) {
        repository.revokeAllByUserId(user.getId());
    }

    private String newRawToken() {
        if (ttlDays <= 0) {
            throw new IllegalStateException("Refresh-token lifetime must be positive");
        }

        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String digest(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
