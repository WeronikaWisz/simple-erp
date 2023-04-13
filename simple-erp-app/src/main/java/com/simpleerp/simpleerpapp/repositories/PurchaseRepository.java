package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<List<Purchase>> findByProductAndStatusNotIn(Product product, List<EStatus> statuses);

    Optional<List<Purchase>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Purchase>> findByStatus(EStatus statuse);
}
