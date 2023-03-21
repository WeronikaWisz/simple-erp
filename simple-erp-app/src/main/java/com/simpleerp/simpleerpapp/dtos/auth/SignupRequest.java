package com.simpleerp.simpleerpapp.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
}
