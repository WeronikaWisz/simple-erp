package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    Optional<Contractor> findByEmail(String email);
}
