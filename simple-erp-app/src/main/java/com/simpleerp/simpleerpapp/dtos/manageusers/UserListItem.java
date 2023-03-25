package com.simpleerp.simpleerpapp.dtos.manageusers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UserListItem {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private List<String> roles = new ArrayList<>();

    public UserListItem(Long id, String username, String email, String name, String surname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserListItem that = (UserListItem) o;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(phone, that.phone) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, name, surname, phone, roles);
    }
}
