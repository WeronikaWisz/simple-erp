package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import jakarta.annotation.Nonnull;
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
public class Purchase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Nonnull
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable=false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user_id", nullable=false)
    private User requestingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", nullable=false)
    private User assignedUser;

    private BigDecimal quantity;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private EStatus status;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private LocalDateTime executionDate;
}
