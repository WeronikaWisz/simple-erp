package com.simpleerp.simpleerpapp.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ProductProduction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String productCode;

    @OneToMany(mappedBy="productProduction")
    List<ProductionStep> productionSteps = new ArrayList<>();

    @OneToMany(mappedBy = "productProduction")
    List<ProductProductionProducts> productProductionProducts = new ArrayList<>();

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public ProductProduction(String productCode, LocalDateTime creationDate) {
        this.productCode = productCode;
        this.creationDate = creationDate;
    }

    public void addProduct(Product product, BigDecimal quantity){
        ProductProductionProducts productProductionProducts =
                new ProductProductionProducts(this, product, quantity);
        this.productProductionProducts.add(productProductionProducts);
        product.getProductsProductions().add(productProductionProducts);
    }
}
