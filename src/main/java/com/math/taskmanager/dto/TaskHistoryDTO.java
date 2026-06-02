package com.math.taskmanager.dto;

import java.time.LocalDateTime;

public record TaskHistoryDTO(

        Long id,

        String userName,

        String action,

        LocalDateTime createdAt

) {}