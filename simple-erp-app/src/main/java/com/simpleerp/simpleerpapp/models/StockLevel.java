package com.simpleerp.simpleerpapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class StockLevel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private BigDecimal quantity;
    private BigDecimal minQuantity;

    private Integer daysUntilStockLasts;

    private Boolean isDeleted;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    private LocalDateTime deleteDate;

    public StockLevel(Product product, BigDecimal quantity, BigDecimal minQuantity, Integer daysUntilStockLasts,
                      LocalDateTime creationDate) {
        this.product = product;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.daysUntilStockLasts = daysUntilStockLasts;
        this.creationDate = creationDate;
        this.isDeleted = false;
    }
}
