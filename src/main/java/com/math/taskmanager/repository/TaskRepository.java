package com.math.taskmanager.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    Page<Task> findAll(Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByUserId(Long userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(
            Long userId,
            TaskStatus status,
            Pageable pageable
    );

    // 🔥 CORRETO
    Page<Task> findBySectorId(Long sectorId, Pageable pageable);
}