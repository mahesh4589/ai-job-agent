package com.api.auth_service.service;

import com.api.auth_service.entity.*;

public interface RefreshTokenService {
    String create(User user);

    RefreshTokenRotation rotate(String token);

    void revoke(String token);

    void revokeAllForUser(User user);

    record RefreshTokenRotation(User user, String token) { }
}
