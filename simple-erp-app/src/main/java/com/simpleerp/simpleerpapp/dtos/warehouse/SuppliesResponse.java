package com.simpleerp.simpleerpapp.dtos.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SuppliesResponse {
    private List<SuppliesListItem> suppliesList;
    private Integer totalProductsLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuppliesResponse that = (SuppliesResponse) o;
        return Objects.equals(suppliesList, that.suppliesList)
                && Objects.equals(totalProductsLength, that.totalProductsLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suppliesList, totalProductsLength);
    }
}
