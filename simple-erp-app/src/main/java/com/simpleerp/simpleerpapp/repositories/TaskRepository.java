package com.simpleerp.simpleerpapp.repositories;

import com.simpleerp.simpleerpapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
