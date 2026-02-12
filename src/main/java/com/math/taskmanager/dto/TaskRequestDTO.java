package com.math.taskmanager.dto;

import com.math.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    String title,

    @Size(max = 255, message = "A descrição pode ter no máximo 255 caracteres")
    String description,

    TaskStatus status,

    @NotNull(message = "O userId é obrigatório")
    Long userId

) {}