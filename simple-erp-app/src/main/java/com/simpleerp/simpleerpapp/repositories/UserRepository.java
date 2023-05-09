package com.simpleerp.simpleerpapp.repositories;


import com.simpleerp.simpleerpapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    List<User> findByIsDeleted(Boolean isDeleted);
}
