package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByName(ETask name);

}
