package com.simpleerp.simpleerpapp.dtos.products;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductRequest {
    private Long id;
    private EType type;
    private String code;
    private String name;
    private EUnit unit;
    private String purchasePrice;
    private String salePrice;
    private List<ProductQuantity> productSet = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateProductRequest that = (UpdateProductRequest) o;
        return Objects.equals(id, that.id) && type == that.type
                && Objects.equals(code, that.code) && Objects.equals(name, that.name) && unit == that.unit
                && Objects.equals(purchasePrice, that.purchasePrice) && Objects.equals(salePrice, that.salePrice)
                && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, code, name, unit, purchasePrice, salePrice, productSet);
    }
}
