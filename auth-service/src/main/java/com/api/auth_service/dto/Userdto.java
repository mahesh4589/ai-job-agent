package com.api.auth_service.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class Userdto {
 @NotBlank @Size(max=80) private String firstName;
 @NotBlank @Size(max=80) private String lastName;
 private Long mobileNo;
 @NotBlank @Email @Size(max=160) private String emailId;
 @NotBlank @Size(min=8,max=100) private String password;
}
