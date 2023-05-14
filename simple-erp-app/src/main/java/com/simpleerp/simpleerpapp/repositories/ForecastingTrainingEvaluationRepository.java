package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ForecastingTrainingEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForecastingTrainingEvaluationRepository extends JpaRepository<ForecastingTrainingEvaluation, Long> {
    Optional<ForecastingTrainingEvaluation> findByProductCode(String productCode);
}
