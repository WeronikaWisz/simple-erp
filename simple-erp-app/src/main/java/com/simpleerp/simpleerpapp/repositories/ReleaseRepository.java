package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Order;
import com.simpleerp.simpleerpapp.models.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {
    Optional<Release> findByOrder(Order order);
}
