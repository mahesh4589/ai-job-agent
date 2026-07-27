package com.ai.agent.service.impl;

import com.ai.agent.dao.UserServceRepository;
import com.ai.agent.dto.ApiResponse;
import com.ai.agent.dto.LoginDto;
import com.ai.agent.dto.UserResponse;
import com.ai.agent.dto.Userdto;


import com.ai.agent.entity.Role;
import com.ai.agent.entity.User;
import com.ai.agent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServceImpl implements UserService {

    @Autowired
    UserServceRepository userServceRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public UserResponse createNewUser(Userdto user) {
        log.info("Saving new user: {}", user.getEmailId());
        User u = new User();
        String password = passwordEncoder.encode(user.getPassword());
        u.setPassword(password);
        u.setEmailId(user.getEmailId());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setMobileNo(user.getMobileNo());
        u.setRole(Role.ROLE_USER);
        User newUser = userServceRepository.save(u);
        UserResponse res = new UserResponse();
        res.setId(newUser.getId());
        res.setRole(newUser.getRole());
        res.setMobileNo(newUser.getMobileNo());
        res.setLastName(newUser.getLastName());
        res.setFirstName(newUser.getLastName());
        res.setEmailId(newUser.getEmailId());
        res.setRole(newUser.getRole());

        log.info("User saved successfully with id: {}", res.getId());
        return res;
    }

    @Override
    public User getUserRecordById(int id) {
        log.info("Finding user with id: {}", id);
        return userServceRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found : " + id));
    }

    @Override

    public List<User> getAllUserRecord() {
        log.info("Fetching all users from database");
        return userServceRepository.findAll();
    }

    @Override
    public User updateUserRecord(int id, Userdto user) {
        log.info("Updating user with id: {}", id);
        User getRecord = userServceRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        System.out.println(getRecord);

        getRecord.setFirstName(user.getFirstName());
        getRecord.setLastName(user.getLastName());
        getRecord.setMobileNo(user.getMobileNo());
        log.debug("Updating firstName, lastName and mobile number.");
        return userServceRepository.save(getRecord);
    }

    @Override
    public User userLogin(LoginDto loginDto) {


        User user = userServceRepository
                .findByEmailId(loginDto.getEmailId())
                .orElseThrow(() ->
                        new RuntimeException("Invalid Email or Password"));

        if (!passwordEncoder.matches(
                loginDto.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid Email or Password");
        }


        return user;
    }

    @Override
    public User findRecordByEmailId(String emailId) {
        log.info("Searching user by email: {}", emailId);
        return userServceRepository.
                findByEmailId(emailId).orElseThrow(() -> new RuntimeException("User record not found.."));

    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Deleting user with id: {}", id);
        userServceRepository.deleteById(id);
        log.info("User deleted successfully.");
    }
}
