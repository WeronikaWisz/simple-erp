package com.simpleerp.simpleerpapp.dtos.trade;

import com.simpleerp.simpleerpapp.enums.ETask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAssignedUserRequest {
    private List<Long> taskIds;
    private Long employeeId;
    private ETask task;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAssignedUserRequest that = (UpdateAssignedUserRequest) o;
        return Objects.equals(taskIds, that.taskIds) && Objects.equals(employeeId, that.employeeId) && task == that.task;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskIds, employeeId, task);
    }
}
