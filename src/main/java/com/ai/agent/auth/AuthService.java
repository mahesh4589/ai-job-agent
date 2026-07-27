package com.ai.agent.auth;

import com.ai.agent.dto.LoginDto;
import com.ai.agent.dto.LoginResponse;
import com.ai.agent.dto.UserResponse;
import com.ai.agent.dto.Userdto;
import com.ai.agent.entity.User;

public interface AuthService {


    UserResponse userRegister(Userdto userdto);


    LoginResponse userLogin(LoginDto loginDto);

    LoginResponse tokenRefresh(String refreshToken);

    void logout(String refreshToken);
}
