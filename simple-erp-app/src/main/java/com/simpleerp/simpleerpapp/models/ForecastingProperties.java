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
public class ForecastingProperties {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String code;
    @Nonnull
    private String name;
    @Nonnull
    private String value;

    private LocalDateTime creationDate;
    private Boolean isValid;
}
