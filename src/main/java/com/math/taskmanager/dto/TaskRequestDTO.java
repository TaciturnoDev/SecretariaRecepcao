package com.math.taskmanager.dto;

import com.math.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        String title,

        String description,

        TaskStatus status,

        Long sectorId

) {}