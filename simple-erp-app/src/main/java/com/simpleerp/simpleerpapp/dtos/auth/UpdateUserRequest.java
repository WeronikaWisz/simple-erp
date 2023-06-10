package com.simpleerp.simpleerpapp.dtos.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class UpdateUserRequest {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private List<String> roles = new ArrayList<>();
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username)
                && Objects.equals(email, that.email) && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname) && Objects.equals(phone, that.phone)
                && Objects.equals(roles, that.roles) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, name, surname, phone, roles, password);
    }
}
