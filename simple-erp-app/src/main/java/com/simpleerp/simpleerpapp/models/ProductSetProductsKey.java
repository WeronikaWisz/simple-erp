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
public class ProductSetProductsKey implements Serializable {

    @Column(name = "product_set_id")
    Long productSetId;

    @Column(name = "product_id")
    Long productId;

    public ProductSetProductsKey(Long productSetId, Long productId) {
        this.productSetId = productSetId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSetProductsKey that = (ProductSetProductsKey) o;
        return Objects.equals(productSetId, that.productSetId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productSetId, productId);
    }
}
