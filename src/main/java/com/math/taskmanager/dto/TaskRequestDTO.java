package com.math.taskmanager.dto;

import com.math.taskmanager.entity.TaskPriority;
import com.math.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(

    String title,

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    String description,

    TaskStatus status,

    TaskPriority priority,

    Long sectorId

) {}