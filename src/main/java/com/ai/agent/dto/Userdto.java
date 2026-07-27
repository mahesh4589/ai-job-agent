package com.ai.agent.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Userdto {

    int id;
    String firstName;
    String lastName;
    Long mobileNo;
    String emailId;
    String password;

}
