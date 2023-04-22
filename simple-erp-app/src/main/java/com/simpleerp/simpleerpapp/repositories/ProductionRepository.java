package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.models.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

    Optional<List<Production>> findByProductAndStatusNotIn(Product product, List<EStatus> statuses);

    Optional<List<Production>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Production>> findByStatus(EStatus status);
}
