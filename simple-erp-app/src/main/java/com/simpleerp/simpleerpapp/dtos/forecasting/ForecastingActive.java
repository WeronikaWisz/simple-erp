package com.simpleerp.simpleerpapp.dtos.forecasting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ForecastingActive {
    private Boolean active;
    private String rmsse;
    private String loss;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastingActive that = (ForecastingActive) o;
        return Objects.equals(active, that.active) && Objects.equals(rmsse, that.rmsse)
                && Objects.equals(loss, that.loss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, rmsse, loss);
    }
}
