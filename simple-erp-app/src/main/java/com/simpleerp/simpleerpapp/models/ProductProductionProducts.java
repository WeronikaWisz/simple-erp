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
public class ProductProductionProducts {

    @EmbeddedId
    ProductProductionProductsKey id;

    @ManyToOne
    @MapsId("productProductionId")
    @JoinColumn(name = "product_production_id")
    ProductProduction productProduction;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;

    BigDecimal quantity;

    public ProductProductionProducts(ProductProduction productProduction, Product product, BigDecimal quantity) {
        this.productProduction = productProduction;
        this.product = product;
        this.quantity = quantity;
        this.id = new ProductProductionProductsKey(productProduction.getId(), product.getId());
    }
}
