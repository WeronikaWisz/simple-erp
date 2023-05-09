package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Order;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByProductAndStatusNotIn(Product product, List<EStatus> statuses);

    List<Purchase> findByStatusIn(List<EStatus> statuses);

    List<Purchase> findByStatus(EStatus statuse);

    List<Purchase> findByExecutionDateBetweenAndStatusNot(LocalDateTime startDate, LocalDateTime endDate,
                                                          EStatus status);
}
