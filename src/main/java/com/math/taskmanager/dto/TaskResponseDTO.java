package com.math.taskmanager.dto;

import com.math.taskmanager.entity.TaskStatus;
import java.time.LocalDateTime;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime createdAt
) {}
