package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByNumber(String number);

    Optional<List<Order>> findByStatus(EStatus status);

    Optional<List<Order>> findByStatusIn(List<EStatus> statuses);

    Optional<List<Order>> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
