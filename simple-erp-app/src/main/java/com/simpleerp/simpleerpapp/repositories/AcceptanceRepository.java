package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Acceptance;
import com.simpleerp.simpleerpapp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Optional<Acceptance> findByPurchase(Purchase purchase);

    Optional<List<Acceptance>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Acceptance>> findByStatus(EStatus statuse);

    Optional<List<Acceptance>> findByStatusAndDirection(EStatus status, EDirection direction);

    Optional<List<Acceptance>> findByStatusInAndDirection(List<EStatus> statuses, EDirection direction);
}
