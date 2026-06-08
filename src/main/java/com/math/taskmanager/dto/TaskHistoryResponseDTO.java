package com.math.taskmanager.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TaskHistoryResponseDTO(

        Long id,

        String action,

        String userName,

        String oldTitle,
        String newTitle,

        String oldDescription,
        String newDescription,

        LocalDateTime createdAt,

        List<AttachmentResponseDTO> attachments

) {}