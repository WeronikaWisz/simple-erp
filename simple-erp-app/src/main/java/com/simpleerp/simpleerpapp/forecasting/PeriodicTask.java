package com.simpleerp.simpleerpapp.forecasting;

import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.ForecastingProperties;
import com.simpleerp.simpleerpapp.repositories.ForecastingPropertiesRepository;
import com.simpleerp.simpleerpapp.services.ForecastingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class PeriodicTask {

    private final ForecastingService forecastingService;
    private final ForecastingPropertiesRepository forecastingPropertiesRepository;

    @Autowired
    public PeriodicTask(ForecastingPropertiesRepository forecastingPropertiesRepository,
                        ForecastingService forecastingService) {
        this.forecastingPropertiesRepository = forecastingPropertiesRepository;
        this.forecastingService = forecastingService;
    }

    @Scheduled(cron = "@midnight")
    public void predictDemandDaily() {
        if(checkIfForecastingActive()) {
            forecastingService.predictDemandDaily();
        }
    }

    @Scheduled(cron = "@monthly")
    public void trainDemandMonthly() {
        if(checkIfForecastingActive()) {
            forecastingService.trainDemandMonthly();
        }
    }

    private boolean checkIfForecastingActive(){
        ForecastingProperties forecastingProperties = forecastingPropertiesRepository.findByCodeAndIsValid("FORECASTING_ACTIVE", true)
                .orElseThrow( () -> new ApiNotFoundException("exception.forecastingPropertyNotFound"));
        return forecastingProperties.getValue().equals("YES");
    }
}
