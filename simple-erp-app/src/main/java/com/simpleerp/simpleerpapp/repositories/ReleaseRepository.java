package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Order;
import com.simpleerp.simpleerpapp.models.Production;
import com.simpleerp.simpleerpapp.models.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {

    Optional<Release> findByOrder(Order order);

    Optional<Release> findByProduction(Production production);

    Optional<List<Release>> findByStatus(EStatus status);

    Optional<List<Release>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Release>> findByStatusAndDirection(EStatus status, EDirection direction);

    Optional<List<Release>> findByStatusInAndDirection(List<EStatus> statuses, EDirection direction);
}
