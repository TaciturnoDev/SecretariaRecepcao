package com.math.taskmanager.service;

import com.math.taskmanager.entity.Attachment;
import com.math.taskmanager.entity.TaskHistory;
import com.math.taskmanager.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.upload.base-path}")
    private String uploadBasePath;

}