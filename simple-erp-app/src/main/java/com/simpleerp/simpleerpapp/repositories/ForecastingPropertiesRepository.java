package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ForecastingProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForecastingPropertiesRepository extends JpaRepository<ForecastingProperties, Long> {

    Optional<ForecastingProperties> findByCodeAndIsValid(String code, Boolean isValid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE ForecastingProperties SET value=?2 WHERE code=?1 AND isValid=true")
    void changeValue(String code, String newValue);
}
