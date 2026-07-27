package com.ai.agent.dao;

import com.ai.agent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserServceRepository extends JpaRepository<User,Integer> {
  Optional <User> findByEmailId(String emailId);
}
