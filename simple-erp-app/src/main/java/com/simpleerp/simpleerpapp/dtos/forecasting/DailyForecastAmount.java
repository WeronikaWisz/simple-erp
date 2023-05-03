package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecastAmount implements Serializable {
    private String amount;
}
