package com.simpleerp.simpleerpapp.models;

import com.simpleerp.simpleerpapp.dtos.forecasting.DailyForecastList;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ForecastingTrainingEvaluation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String productCode;

    @JdbcTypeCode(SqlTypes.JSON)
    private DailyForecastList forecast;

    @JdbcTypeCode(SqlTypes.JSON)
    private DailyForecastList real;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private LocalDateTime startDate;
    private Boolean isValid;

    public ForecastingTrainingEvaluation(String productCode, DailyForecastList forecast, DailyForecastList real,
                                         LocalDateTime creationDate, LocalDateTime startDate, Boolean isValid) {
        this.productCode = productCode;
        this.forecast = forecast;
        this.real = real;
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.isValid = isValid;
    }
}
