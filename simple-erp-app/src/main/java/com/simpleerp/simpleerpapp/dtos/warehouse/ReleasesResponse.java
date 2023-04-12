package com.simpleerp.simpleerpapp.dtos.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ReleasesResponse {
    private List<ReleaseListItem> releasesList;
    private Integer totalTasksLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleasesResponse that = (ReleasesResponse) o;
        return Objects.equals(releasesList, that.releasesList) && Objects.equals(totalTasksLength, that.totalTasksLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releasesList, totalTasksLength);
    }
}
