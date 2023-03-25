package com.simpleerp.simpleerpapp.dtos.manageusers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UsersResponse {
    private List<UserListItem> userList;
    private Integer totalUsersLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersResponse that = (UsersResponse) o;
        return Objects.equals(userList, that.userList) && Objects.equals(totalUsersLength, that.totalUsersLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userList, totalUsersLength);
    }
}
