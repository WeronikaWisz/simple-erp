package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ProductForecasting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductForecastingRepository extends JpaRepository<ProductForecasting, Long> {

    Optional<ProductForecasting> findByProductCode(String productCode);

}
