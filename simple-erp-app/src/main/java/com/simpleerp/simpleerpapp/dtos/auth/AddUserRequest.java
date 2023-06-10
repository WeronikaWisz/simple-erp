package com.simpleerp.simpleerpapp.dtos.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddUserRequest that = (AddUserRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email)
                && Objects.equals(password, that.password) && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname) && Objects.equals(phone, that.phone)
                && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, name, surname, phone, roles);
    }
}
