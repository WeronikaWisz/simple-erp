package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ProductProduction;
import com.simpleerp.simpleerpapp.models.ProductionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionStepRepository extends JpaRepository<ProductionStep, Long> {
    Optional<List<ProductionStep>> findByProductProduction(ProductProduction productProduction);
    Optional<ProductionStep> findByProductProductionAndNumber(ProductProduction productProduction, Integer number);
    Optional<List<ProductionStep>> findByProductProductionAndNumberAfter(ProductProduction productProduction, Integer number);
}
