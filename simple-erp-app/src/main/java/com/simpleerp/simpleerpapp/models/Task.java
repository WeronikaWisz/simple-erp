package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.enums.ETask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ETask name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false)
    private User defaultUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable=false)
    private Role role;
}
