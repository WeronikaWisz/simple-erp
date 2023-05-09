package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.models.Contractor;
import com.simpleerp.simpleerpapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    List<Product> findByContractor(Contractor contractor);
    Optional<Product> findByForecastingMapping(String mapping);
    List<Product> findByIsDeleted(Boolean isDeleted);
    List<Product> findByTypeAndIsDeleted(EType type, Boolean isDeleted);
}
