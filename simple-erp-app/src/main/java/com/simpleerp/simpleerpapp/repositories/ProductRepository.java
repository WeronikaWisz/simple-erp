package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.models.Contractor;
import com.simpleerp.simpleerpapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    List<Product> findByContractor(Contractor contractor);
    Optional<Product> findByForecastingMapping(String mapping);
    List<Product> findByIsDeletedFalse();
    List<Product> findByTypeAndIsDeletedFalse(EType type);
    List<Product> findByForecastingMappingIsNullAndIsDeletedFalseAndSalePriceIsNotNullAndCreationDateBefore(LocalDateTime date);
}
