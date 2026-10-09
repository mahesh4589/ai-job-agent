package com.api.auth_service.service.impl;
import com.api.auth_service.dto.*; import com.api.auth_service.entity.*; import com.api.auth_service.repository.UserRepository; import com.api.auth_service.service.UserService;
import lombok.RequiredArgsConstructor; import org.springframework.security.authentication.BadCredentialsException; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class UserServiceImpl implements UserService {
 private final UserRepository repo; private final PasswordEncoder encoder;
 public UserResponse createNewUser(Userdto d){String email=d.getEmailId().trim().toLowerCase(); if(repo.existsByEmailIdIgnoreCase(email)) throw new IllegalStateException("Email already registered"); User u=new User(); u.setFirstName(d.getFirstName());u.setLastName(d.getLastName());u.setMobileNo(d.getMobileNo());u.setEmailId(email);u.setPassword(encoder.encode(d.getPassword()));u.setRole(Role.ROLE_USER);u.setEmailVerified(false); return map(repo.save(u));}
 public User userLogin(LoginDto d){User u=findRecordByEmailId(d.getEmailId()); if(!u.isEmailVerified()) throw new IllegalStateException("Email is not verified"); if(!encoder.matches(d.getPassword(),u.getPassword())) throw new BadCredentialsException("Invalid email or password"); return u;}
 public User findRecordByEmailId(String email){return repo.findByEmailIdIgnoreCase(email.trim()).orElseThrow(()->new BadCredentialsException("User not found"));}
 private UserResponse map(User u){return new UserResponse(u.getId(),u.getFirstName(),u.getLastName(),u.getEmailId(),u.getMobileNo(),u.getRole(),u.isEmailVerified());}
}
