package com.simpleerp.simpleerpapp.dtos.production;

import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionProductListItem {
    private Long productId;
    private String code;
    private String name;
    private EUnit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionProductListItem that = (ProductionProductListItem) o;
        return Objects.equals(productId, that.productId) && Objects.equals(code, that.code)
                && Objects.equals(name, that.name) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, code, name, unit);
    }
}
