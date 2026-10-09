package com.api.auth_service.auth;
import com.api.auth_service.entity.User; import org.springframework.security.core.*; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.userdetails.UserDetails; import java.util.List;
public record CustomUserDetails(User user) implements UserDetails {
 public java.util.Collection<? extends GrantedAuthority> getAuthorities(){return List.of(new SimpleGrantedAuthority(user.getRole().name()));}
 public String getPassword(){return user.getPassword();} public String getUsername(){return user.getEmailId();} public boolean isAccountNonExpired(){return true;} public boolean isAccountNonLocked(){return true;} public boolean isCredentialsNonExpired(){return true;} public boolean isEnabled(){return user.isEmailVerified();}
}
