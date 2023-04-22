package com.simpleerp.simpleerpapp.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ProductionStep {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private Integer number;
    @Nonnull
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_production_id", nullable=false)
    ProductProduction productProduction;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public ProductionStep(Integer number, String description, ProductProduction productProduction,
                          LocalDateTime creationDate) {
        this.number = number;
        this.description = description;
        this.productProduction = productProduction;
        this.creationDate = creationDate;
    }
}
