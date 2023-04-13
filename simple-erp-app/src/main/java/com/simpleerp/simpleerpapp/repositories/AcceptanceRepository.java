package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Acceptance;
import com.simpleerp.simpleerpapp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Optional<Acceptance> findByPurchaseAndStatusNotIn(Purchase purchase, List<EStatus> statuses);

    Optional<List<Acceptance>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Acceptance>> findByStatus(EStatus statuse);
}
