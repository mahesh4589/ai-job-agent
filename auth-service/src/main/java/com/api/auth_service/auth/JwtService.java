package com.api.auth_service.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-ttl-seconds:3600}") long ttl) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must not be empty");
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must be at least 32 bytes");
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.accessTtlSeconds = ttl;
    }

    public String generateToken(String email) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(
                                now.plusSeconds(accessTtlSeconds)
                        )
                )
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails details) {

        try {
            String email = getEmailFromToken(token);

            return details.getUsername()
                    .equalsIgnoreCase(email)
                    && !isExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {

        Date expiration = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date());
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }
}