package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
    Optional<StockLevel> findByProduct(Product product);
}
