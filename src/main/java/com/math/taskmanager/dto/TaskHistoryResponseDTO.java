package com.math.taskmanager.dto;

import java.time.LocalDateTime;

public record TaskHistoryResponseDTO(

        Long id,

        String action,

        String userName,

        String oldTitle,
        String newTitle,

        String oldDescription,
        String newDescription,

        LocalDateTime createdAt

) {}