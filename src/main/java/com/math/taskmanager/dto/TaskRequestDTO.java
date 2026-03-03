package com.math.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        String title,

        String description,

        @NotNull(message = "ID do usuário é obrigatório")
        Long userId
) {}