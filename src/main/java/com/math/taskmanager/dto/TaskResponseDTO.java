package com.math.taskmanager.dto;

import com.math.taskmanager.entity.TaskPriority;
import com.math.taskmanager.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(

        Long id,
        String title,
        String description,

        TaskStatus status,
        TaskPriority priority,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        Long assignedToId,
        String assignedToName,

        Long createdById,
        String createdByName

) {}