package com.ai.agent.dao;

import com.ai.agent.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmailAndOtp(String email, String otp);

    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);
}