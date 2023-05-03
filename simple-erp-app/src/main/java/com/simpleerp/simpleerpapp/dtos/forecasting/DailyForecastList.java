package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class DailyForecastList implements Serializable {
    private List<DailyForecastAmount> dailyForecastAmountList;
}
