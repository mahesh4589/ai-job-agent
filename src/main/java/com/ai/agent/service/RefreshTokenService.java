package com.ai.agent.service;

import com.ai.agent.entity.RefreshToken;
import com.ai.agent.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeRefreshToken(String token);

    void deleteByUser(User user);
}