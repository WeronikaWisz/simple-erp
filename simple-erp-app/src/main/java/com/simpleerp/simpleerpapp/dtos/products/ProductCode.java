package com.simpleerp.simpleerpapp.dtos.products;

import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProductCode {
    private Long id;
    private String name;
    private String code;
    private EUnit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCode that = (ProductCode) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, unit);
    }
}
