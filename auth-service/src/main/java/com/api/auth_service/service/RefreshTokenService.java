package com.api.auth_service.service;

import com.api.auth_service.entity.*;

public interface RefreshTokenService {
    RefreshToken create(User user);

    RefreshToken verify(String token);

    void revoke(String token);
}
