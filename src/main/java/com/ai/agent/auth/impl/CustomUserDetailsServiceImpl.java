package com.ai.agent.auth.impl;

import com.ai.agent.auth.CustomUserDetailsService;
import com.ai.agent.auth.impl.CustomUserDetails;
import com.ai.agent.dao.UserServceRepository;
import com.ai.agent.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserServceRepository userRepository;

    public CustomUserDetailsServiceImpl(UserServceRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) {
        log.debug("Loading user details for email: {}", emailId);
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() ->

                        new RuntimeException("User not found"));
        log.debug("User details loaded successfully.");
        return new CustomUserDetails(user);
    }
}