package com.api.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_info", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email_id"))
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    private Long mobileNo;
    @Column(name = "email_id", nullable = false, unique = true) private String emailId;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role = Role.ROLE_USER;
    @Column(nullable = false) private boolean emailVerified = false;
}
