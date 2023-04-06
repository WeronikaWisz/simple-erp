package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNameAndSurnameAndEmail(String name, String surname, String email);
}
