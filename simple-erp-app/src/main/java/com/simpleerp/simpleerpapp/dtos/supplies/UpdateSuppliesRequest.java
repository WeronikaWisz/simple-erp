package com.simpleerp.simpleerpapp.dtos.supplies;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UpdateSuppliesRequest {
    private Long id;
    private String quantity;
    private String minQuantity;
    private Integer days;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateSuppliesRequest that = (UpdateSuppliesRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(quantity, that.quantity)
                && Objects.equals(minQuantity, that.minQuantity) && Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, minQuantity, days);
    }
}
