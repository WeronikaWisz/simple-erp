package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ForecastingTrainingElement {
    private Long id;
    private String itemId;
    private List<String> dayQuantity = new ArrayList<>();

    @Override
    public String toString() {
        return "ForecastingTrainingElement{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", dayQuantity=" + dayQuantity +
                '}';
    }
}
