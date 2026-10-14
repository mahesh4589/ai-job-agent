package com.api.auth_service.service.impl;

import com.api.auth_service.dto.UserResponse;
import com.api.auth_service.dto.Userdto;
import com.api.auth_service.dto.LoginDto;
import com.api.auth_service.entity.Role;
import com.api.auth_service.entity.User;
import com.api.auth_service.repository.UserRepository;
import com.api.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createNewUser(Userdto request) {
        String email = normalizeEmail(request.getEmailId());
        if (repository.existsByEmailIdIgnoreCase(email)) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setMobileNo(request.getMobileNo());
        user.setEmailId(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEmailVerified(false);
        return toResponse(repository.save(user));
    }

    @Override
    public User userLogin(LoginDto request) {
        User user = repository.findByEmailIdIgnoreCase(normalizeEmail(request.getEmailId()))
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));

        if (!user.isEmailVerified() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }
        return user;
    }

    @Override
    public User findRecordByEmailId(String email) {
        return repository.findByEmailIdIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmailId(), user.getMobileNo(), user.getRole(), user.isEmailVerified());
    }
}
