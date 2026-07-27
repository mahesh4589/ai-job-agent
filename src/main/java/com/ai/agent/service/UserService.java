package com.ai.agent.service;

import com.ai.agent.dto.ApiResponse;
import com.ai.agent.dto.LoginDto;
import com.ai.agent.dto.UserResponse;
import com.ai.agent.dto.Userdto;
import com.ai.agent.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserResponse createNewUser(Userdto user);

    User getUserRecordById(int id);

    List<User> getAllUserRecord();

    User updateUserRecord(int id, Userdto user);

    User userLogin(LoginDto loginDto);


    User findRecordByEmailId(String emailId);

    void deleteUser(Integer id);
}
