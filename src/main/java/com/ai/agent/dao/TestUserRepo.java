package com.ai.agent.dao;

import com.ai.agent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepo extends JpaRepository<User,Integer> {
}
