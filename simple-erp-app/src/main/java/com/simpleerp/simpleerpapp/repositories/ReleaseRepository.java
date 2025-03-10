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

    List<Release> findByStatus(EStatus status);

    List<Release> findByStatusIn(List<EStatus> statuses);

    List<Release> findByStatusAndDirection(EStatus status, EDirection direction);

    List<Release> findByStatusInAndDirection(List<EStatus> statuses, EDirection direction);
}
