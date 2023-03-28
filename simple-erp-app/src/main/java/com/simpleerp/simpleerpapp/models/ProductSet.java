package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.enums.EUnit;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ProductSet {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String code;
    @Nonnull
    private String name;
    private BigDecimal salePrice;
    @OneToMany(mappedBy = "productSet")
    List<ProductSetProducts> productsSets = new ArrayList<>();

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public ProductSet(String code, String name, BigDecimal salePrice, LocalDateTime creationDate) {
        this.code = code;
        this.name = name;
        this.salePrice = salePrice;
        this.creationDate = creationDate;
    }

    public void addProduct(Product product, BigDecimal quantity){
        ProductSetProducts productSetProducts = new ProductSetProducts(this, product, quantity);
        productsSets.add(productSetProducts);
        product.getProductsSets().add(productSetProducts);
    }

    public void removeProduct(Product product){
        for (Iterator<ProductSetProducts> iterator = productsSets.iterator();
             iterator.hasNext(); ) {
            ProductSetProducts productSetProducts = iterator.next();

            if (productSetProducts.getProductSet().equals(this) &&
                    productSetProducts.getProduct().equals(product)) {
                iterator.remove();
                productSetProducts.getProduct().getProductsSets().remove(productSetProducts);
                productSetProducts.setProduct(null);
                productSetProducts.setProductSet(null);
            }
        }
    }
}
