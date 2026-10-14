package com.api.auth_service.service.impl;

import com.api.auth_service.auth.JwtService;
import com.api.auth_service.dto.LoginDto;
import com.api.auth_service.dto.LoginResponse;
import com.api.auth_service.dto.UserResponse;
import com.api.auth_service.dto.Userdto;
import com.api.auth_service.entity.Role;
import com.api.auth_service.entity.User;
import com.api.auth_service.kafka.event.UserRegisteredEvent;
import com.api.auth_service.kafka.producer.UserEventProducer;
import com.api.auth_service.service.OtpService;
import com.api.auth_service.service.RefreshTokenService;
import com.api.auth_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserService users;
    @Mock private OtpService otp;
    @Mock private JwtService jwt;
    @Mock private RefreshTokenService refresh;
    @Mock private UserEventProducer events;
    @InjectMocks private AuthServiceImpl authService;

    @Test
    void registrationCreatesAccountSendsOtpAndPublishesUserEvent() {
        Userdto request = new Userdto();
        request.setEmailId("asha@example.com");
        UserResponse created = new UserResponse(42, "Asha", "Patil", "asha@example.com", null, Role.ROLE_USER, false);
        when(users.createNewUser(request)).thenReturn(created);

        UserResponse result = authService.userRegister(request);

        assertSame(created, result);
        verify(otp).sendRegistrationOtp("asha@example.com");
        ArgumentCaptor<UserRegisteredEvent> event = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(events).sendUserCreatedEvent(event.capture());
        assertEquals(42, event.getValue().userId());
        assertEquals("asha@example.com", event.getValue().email());
        assertEquals("Asha", event.getValue().firstName());
    }

    @Test
    void loginReturnsAccessAndRefreshTokensForAuthenticatedUser() {
        User user = user();
        LoginDto request = new LoginDto();
        request.setEmailId(user.getEmailId());
        request.setPassword("not-asserted-or-logged");
        when(users.userLogin(request)).thenReturn(user);
        when(jwt.generateToken(user.getEmailId())).thenReturn("access-jwt");
        when(jwt.getAccessTtlSeconds()).thenReturn(900L);
        when(refresh.create(user)).thenReturn("refresh-token");

        LoginResponse response = authService.userLogin(request);

        assertEquals("access-jwt", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());
        assertEquals(user.getEmailId(), response.getEmail());
        verify(refresh).create(user);
    }

    @Test
    void refreshReturnsRotatedTokensAndUsesUserIdentity() {
        User user = user();
        when(refresh.rotate("old-refresh-token"))
                .thenReturn(new RefreshTokenService.RefreshTokenRotation(user, "new-refresh-token"));
        when(jwt.generateToken(user.getEmailId())).thenReturn("new-access-jwt");
        when(jwt.getAccessTtlSeconds()).thenReturn(900L);

        LoginResponse response = authService.tokenRefresh("old-refresh-token");

        assertEquals("new-access-jwt", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(user.getEmailId(), response.getEmail());
    }

    @Test
    void refreshPropagatesInvalidTokenFailure() {
        when(refresh.rotate("bad-token")).thenThrow(new IllegalArgumentException("Invalid refresh token"));

        assertThrows(IllegalArgumentException.class, () -> authService.tokenRefresh("bad-token"));
        verifyNoInteractions(jwt);
    }

    @Test
    void logoutRevokesSubmittedRefreshToken() {
        authService.logout("refresh-token");

        verify(refresh).revoke("refresh-token");
    }

    private User user() {
        User user = new User();
        user.setId(42);
        user.setFirstName("Asha");
        user.setLastName("Patil");
        user.setEmailId("asha@example.com");
        user.setRole(Role.ROLE_USER);
        user.setEmailVerified(true);
        return user;
    }
}
