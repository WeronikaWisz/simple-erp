package com.simpleerp.simpleerpapp.dtos.manageusers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDefaultUserRequest {
    private Long taskId;
    private Long employeeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateDefaultUserRequest that = (UpdateDefaultUserRequest) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, employeeId);
    }
}
