package com.simpleerp.simpleerpapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductSetProducts {

    @EmbeddedId
    ProductSetProductsKey id;

    @ManyToOne
    @MapsId("productSetId")
    @JoinColumn(name = "product_set_id")
    ProductSet productSet;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;

    BigDecimal quantity;

    public ProductSetProducts(ProductSet productSet, Product product, BigDecimal quantity) {
        this.productSet = productSet;
        this.product = product;
        this.quantity = quantity;
        this.id = new ProductSetProductsKey(productSet.getId(), product.getId());
    }
}
