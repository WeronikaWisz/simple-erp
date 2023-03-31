package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.PurchaseTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseTaskRepository extends JpaRepository<PurchaseTask, Long> {

    Optional<List<PurchaseTask>> findByProductAndStatusNotIn(Product product, List<EStatus> statuses);

    Optional<List<PurchaseTask>> findByStatusIn(List<EStatus> statuses);
}
