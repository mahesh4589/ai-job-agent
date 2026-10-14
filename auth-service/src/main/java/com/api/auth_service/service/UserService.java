package com.api.auth_service.service;

import com.api.auth_service.dto.*;
import com.api.auth_service.entity.User;

public interface UserService {
    UserResponse createNewUser(Userdto dto);

    User userLogin(LoginDto dto);

    User findRecordByEmailId(String email);
}
