package com.math.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotBlank(message = "Login é obrigatório")
    String login,

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    String cpf,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String password,

    Long sectorId

) {}