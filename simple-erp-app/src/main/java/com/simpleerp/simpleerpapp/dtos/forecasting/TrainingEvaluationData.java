package com.simpleerp.simpleerpapp.dtos.forecasting;

import com.simpleerp.simpleerpapp.dtos.trade.ChartData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TrainingEvaluationData {
    private List<ChartData> forecast = new ArrayList<>();
    private List<ChartData> real = new ArrayList<>();
}
