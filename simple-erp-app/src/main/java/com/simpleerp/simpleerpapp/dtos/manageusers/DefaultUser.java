package com.simpleerp.simpleerpapp.dtos.manageusers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultUser {
    private Long taskId;
    private Long employeeId;
    private String task;
    private String employee;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUser that = (DefaultUser) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(employeeId, that.employeeId)
                && Objects.equals(task, that.task) && Objects.equals(employee, that.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, employeeId, task, employee);
    }
}
