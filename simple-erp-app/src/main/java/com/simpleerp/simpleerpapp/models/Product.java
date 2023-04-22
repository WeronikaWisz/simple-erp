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
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String code;
    @Nonnull
    private String name;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private EUnit unit;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private EType type;
    @OneToMany(mappedBy = "product")
    List<ProductSetProducts> productsSets = new ArrayList<>();
    @OneToMany(mappedBy = "product")
    List<ProductProductionProducts> productsProductions = new ArrayList<>();

    @OneToOne
    private StockLevel stockLevel;

    @OneToMany(mappedBy = "product")
    List<OrderProducts> orderProductsSet = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    Contractor contractor;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public Product(String code, String name, BigDecimal purchasePrice, BigDecimal salePrice, EUnit unit, EType type,
                   LocalDateTime creationDate) {
        this.code = code;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.unit = unit;
        this.type = type;
        this.creationDate = creationDate;
    }
}
