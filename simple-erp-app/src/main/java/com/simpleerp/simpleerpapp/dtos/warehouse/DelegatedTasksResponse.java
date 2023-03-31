package com.simpleerp.simpleerpapp.dtos.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class DelegatedTasksResponse {
    private List<DelegatedTaskListItem> tasksList;
    private Integer totalTasksLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegatedTasksResponse that = (DelegatedTasksResponse) o;
        return Objects.equals(tasksList, that.tasksList) && Objects.equals(totalTasksLength, that.totalTasksLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasksList, totalTasksLength);
    }
}
