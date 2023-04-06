package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.enums.EStatus;
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
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"number"})})
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String number;
    @Nonnull
    private LocalDateTime orderDate;
    private BigDecimal discount;
    private BigDecimal delivery;
    private BigDecimal price;
    @Nonnull
    private BigDecimal calculatedPrice;
    @Nonnull
    private BigDecimal totalPrice;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private EStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable=false)
    private Customer customer;

    @OneToMany(mappedBy = "order")
    List<OrderProducts> orderProductsSet = new ArrayList<>();

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public Order(String number, LocalDateTime orderDate, EStatus status, Customer customer, LocalDateTime creationDate) {
        this.number = number;
        this.orderDate = orderDate;
        this.status = status;
        this.customer = customer;
        this.creationDate = creationDate;
    }

    public void addProduct(Product product, BigDecimal quantity){
        OrderProducts orderProducts = new OrderProducts(this, product, quantity);
        orderProductsSet.add(orderProducts);
        product.getOrderProductsSet().add(orderProducts);
    }

}
