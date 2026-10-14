package com.api.auth_service.auth;

import com.api.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public UserDetails loadUserByUsername(String username) {
        return new CustomUserDetails(repo.findByEmailIdIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
