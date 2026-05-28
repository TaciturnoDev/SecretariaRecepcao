package com.math.taskmanager.repository;

import com.math.taskmanager.entity.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository
        extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    void deleteByTaskId(Long taskId);
}