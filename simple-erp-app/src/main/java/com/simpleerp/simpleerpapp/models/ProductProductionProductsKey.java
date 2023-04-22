package com.simpleerp.simpleerpapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ProductProductionProductsKey implements Serializable {
    @Column(name = "product_production_id")
    Long productProductionId;

    @Column(name = "product_id")
    Long productId;

    public ProductProductionProductsKey(Long productProductionId, Long productId) {
        this.productProductionId = productProductionId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductProductionProductsKey that = (ProductProductionProductsKey) o;
        return Objects.equals(productProductionId, that.productProductionId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productProductionId, productId);
    }
}
