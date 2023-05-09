package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ProductSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSetRepository extends JpaRepository<ProductSet, Long> {
    Optional<ProductSet> findByCode(String code);
    Optional<ProductSet> findByForecastingMapping(String mapping);
    List<ProductSet> findByIsDeleted(Boolean isDeleted);
}
