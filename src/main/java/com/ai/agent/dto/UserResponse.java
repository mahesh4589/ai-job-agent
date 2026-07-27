package com.ai.agent.dto;

import com.ai.agent.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String emailId;
    private Long mobileNo;
    private Role role;
}