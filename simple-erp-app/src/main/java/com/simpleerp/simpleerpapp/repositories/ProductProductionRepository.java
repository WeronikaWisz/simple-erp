package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.ProductProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductProductionRepository extends JpaRepository<ProductProduction, Long> {
    Optional<ProductProduction> findByProductCode(String code);
}
