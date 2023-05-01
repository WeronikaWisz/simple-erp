package com.simpleerp.simpleerpapp.dtos.products;

import com.simpleerp.simpleerpapp.dtos.production.ProductionProductQuantity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSetInfo {
    private String code;
    private String name;
    private List<ProductionProductQuantity> productSet = new ArrayList<>();

    public ProductSetInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSetInfo that = (ProductSetInfo) o;
        return Objects.equals(code, that.code) && Objects.equals(name, that.name)
                && Objects.equals(productSet, that.productSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, productSet);
    }
}
