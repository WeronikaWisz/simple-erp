package com.simpleerp.simpleerpapp.forecasting;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.dataset.FieldName;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Products {

    private ProductsData data;

    public TimeSeriesData get(NDManager manager) {
        NDArray target = manager.create(data.target);
        TimeSeriesData ret = new TimeSeriesData(10);
        // A TimeSeriesData must contain start time and target value.
        ret.setStartTime(data.start);
        ret.setField(FieldName.TARGET, target);
        return ret;
    }

//    prepare

    private static class ProductsData {
        LocalDateTime start;
        float[] target;
    }
}
