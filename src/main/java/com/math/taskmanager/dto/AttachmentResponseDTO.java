package com.math.taskmanager.dto;

public record AttachmentResponseDTO(

        Long id,

        String originalFileName,

        Long fileSize,

        String contentType

) {}