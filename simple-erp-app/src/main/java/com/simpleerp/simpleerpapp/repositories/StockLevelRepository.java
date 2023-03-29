package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
}
