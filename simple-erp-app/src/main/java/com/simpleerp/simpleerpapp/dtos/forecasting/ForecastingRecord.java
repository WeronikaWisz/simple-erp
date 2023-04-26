package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ForecastingRecord {
    private LocalDateTime date;
    private String productCode;
    private String quantity;
}
