package com.simpleerp.simpleerpapp.dtos.production;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProductionProductResponse {
    private List<ProductionProductListItem> productsList;
    private Integer totalProductsLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionProductResponse that = (ProductionProductResponse) o;
        return Objects.equals(productsList, that.productsList) && Objects.equals(totalProductsLength,
                that.totalProductsLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productsList, totalProductsLength);
    }
}
