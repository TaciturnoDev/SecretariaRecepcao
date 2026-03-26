package com.math.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        String title,

        String description

) {}