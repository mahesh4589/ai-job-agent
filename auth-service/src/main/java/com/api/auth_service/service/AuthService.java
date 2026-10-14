package com.api.auth_service.service;

import com.api.auth_service.dto.*;

public interface AuthService {
    UserResponse userRegister(Userdto d);

    LoginResponse userLogin(LoginDto d);

    LoginResponse tokenRefresh(String token);

    void logout(String token);
}
