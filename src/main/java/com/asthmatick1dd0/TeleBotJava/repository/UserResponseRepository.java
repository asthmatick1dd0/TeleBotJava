package com.asthmatick1dd0.TeleBotJava.repository;

import com.asthmatick1dd0.TeleBotJava.model.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    List<UserResponse> findAll();
}
