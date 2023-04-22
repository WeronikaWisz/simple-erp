package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
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
public class Release {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Nonnull
    private String number;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id")
    private Production production;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user_id", nullable=false)
    private User requestingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", nullable=false)
    private User assignedUser;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private EDirection direction;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private LocalDateTime executionDate;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private EStatus status;

    public Release(Order order, User requestingUser, User assignedUser, EDirection direction,
                   LocalDateTime creationDate, EStatus status) {
        this.order = order;
        this.requestingUser = requestingUser;
        this.assignedUser = assignedUser;
        this.direction = direction;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Release(Production production, User requestingUser, User assignedUser, EDirection direction,
                   LocalDateTime creationDate, EStatus status) {
        this.production = production;
        this.requestingUser = requestingUser;
        this.assignedUser = assignedUser;
        this.direction = direction;
        this.creationDate = creationDate;
        this.status = status;
    }
}
