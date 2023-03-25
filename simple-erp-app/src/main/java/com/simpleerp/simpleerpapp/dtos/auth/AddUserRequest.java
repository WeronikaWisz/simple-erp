package com.simpleerp.simpleerpapp.dtos.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AddUserRequest {
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String phone;
    private List<String> roles = new ArrayList<>();
}
