package com.api.auth_service.service.impl;

import com.api.auth_service.entity.RefreshToken;
import com.api.auth_service.entity.User;
import com.api.auth_service.repository.RefreshTokenRepository;
import com.api.auth_service.service.RefreshTokenService.RefreshTokenRotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock private RefreshTokenRepository repository;
    private RefreshTokenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenServiceImpl(repository);
        ReflectionTestUtils.setField(service, "ttlDays", 30L);
    }

    @Test
    void rotateRevokesOldTokenAndPersistsOnlyDigestOfReplacement() throws Exception {
        User user = user();
        RefreshToken current = refreshToken(user, "old-token-digest", false, Instant.now().plusSeconds(600));
        when(repository.findByTokenForUpdate(sha256("old-raw-token"))).thenReturn(Optional.of(current));
        when(repository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenRotation rotation = service.rotate("old-raw-token");

        assertSame(user, rotation.user());
        assertNotEquals("old-raw-token", rotation.token());
        assertTrue(current.isRevoked());
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository, times(2)).save(saved.capture());
        RefreshToken replacement = saved.getAllValues().get(1);
        assertSame(user, replacement.getUser());
        assertFalse(replacement.isRevoked());
        assertNotEquals(rotation.token(), replacement.getToken());
        assertEquals(sha256(rotation.token()), replacement.getToken());
        assertTrue(replacement.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void rotateRejectsUnknownRefreshToken() {
        when(repository.findByTokenForUpdate(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.rotate("unknown-token"));
        verify(repository, never()).save(any());
    }

    @Test
    void rotateRejectsAlreadyRevokedRefreshToken() {
        RefreshToken current = refreshToken(user(), "digest", true, Instant.now().plusSeconds(600));
        when(repository.findByTokenForUpdate(anyString())).thenReturn(Optional.of(current));

        assertThrows(IllegalArgumentException.class, () -> service.rotate("revoked-token"));
        verify(repository, never()).save(any());
    }

    @Test
    void rotateRejectsExpiredRefreshToken() {
        RefreshToken current = refreshToken(user(), "digest", false, Instant.now().minusSeconds(1));
        when(repository.findByTokenForUpdate(anyString())).thenReturn(Optional.of(current));

        assertThrows(IllegalArgumentException.class, () -> service.rotate("expired-token"));
        verify(repository, never()).save(any());
    }

    @Test
    void logoutRevokesKnownRefreshToken() {
        RefreshToken token = refreshToken(user(), "digest", false, Instant.now().plusSeconds(600));
        when(repository.findByTokenForUpdate(anyString())).thenReturn(Optional.of(token));
        when(repository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.revoke("raw-token");

        assertTrue(token.isRevoked());
        verify(repository).save(token);
    }

    @Test
    void logoutOfUnknownTokenIsIdempotent() {
        when(repository.findByTokenForUpdate(anyString())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.revoke("unknown-token"));
        verify(repository, never()).save(any());
    }

    private User user() {
        User user = new User();
        user.setId(42);
        user.setEmailId("asha@example.com");
        user.setFirstName("Asha");
        return user;
    }

    private RefreshToken refreshToken(User user, String token, boolean revoked, Instant expiry) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setRevoked(revoked);
        refreshToken.setExpiryDate(expiry);
        return refreshToken;
    }

    private static String sha256(String token) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
