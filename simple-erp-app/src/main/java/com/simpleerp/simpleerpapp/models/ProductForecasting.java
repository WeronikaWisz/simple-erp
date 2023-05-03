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
public class ProductForecasting {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Nonnull
    private String productCode;
    private LocalDateTime startDate;
    @JdbcTypeCode(SqlTypes.JSON)
    private DailyForecastList dailyForecast;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public ProductForecasting(String productCode, LocalDateTime startDate,
                              DailyForecastList dailyForecast, LocalDateTime creationDate) {
        this.productCode = productCode;
        this.startDate = startDate;
        this.dailyForecast = dailyForecast;
        this.creationDate = creationDate;
    }
}
