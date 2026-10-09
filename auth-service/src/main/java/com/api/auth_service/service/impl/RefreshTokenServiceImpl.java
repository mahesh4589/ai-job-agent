package com.api.auth_service.service.impl;
import com.api.auth_service.entity.*; import com.api.auth_service.repository.RefreshTokenRepository; import com.api.auth_service.service.RefreshTokenService; import lombok.RequiredArgsConstructor; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import java.time.Instant; import java.time.temporal.ChronoUnit; import java.util.UUID;
@Service @RequiredArgsConstructor public class RefreshTokenServiceImpl implements RefreshTokenService { private final RefreshTokenRepository repo; @Value("${app.jwt.refresh-token-ttl-days:30}") long ttl;
 public RefreshToken create(User user){RefreshToken r=new RefreshToken();r.setUser(user);r.setToken(UUID.randomUUID()+"-"+UUID.randomUUID());r.setExpiryDate(Instant.now().plus(ttl,ChronoUnit.DAYS));r.setRevoked(false);return repo.save(r);}
 public RefreshToken verify(String token){RefreshToken r=repo.findByToken(token).orElseThrow(()->new IllegalArgumentException("Invalid refresh token"));if(r.isRevoked()||r.getExpiryDate().isBefore(Instant.now()))throw new IllegalArgumentException("Refresh token expired or revoked");return r;}
 public void revoke(String token){repo.findByToken(token).ifPresent(r->{r.setRevoked(true);repo.save(r);});}
}
