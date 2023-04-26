package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ForecastingTrainingData {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ForecastingTrainingElement> forecastingTrainingElementList = new ArrayList<>();
}
