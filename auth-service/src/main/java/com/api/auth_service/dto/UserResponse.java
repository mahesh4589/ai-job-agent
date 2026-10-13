package com.api.auth_service.dto;

import com.api.auth_service.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    Integer id;
    String firstName;
    String lastName;
    String emailId;
    Long mobileNo;
    Role role;
    boolean emailVerified;
}
