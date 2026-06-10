package com.math.taskmanager.controller;

import com.math.taskmanager.entity.Attachment;
import com.math.taskmanager.service.AttachmentWorkFlowService;
import com.math.taskmanager.service.AttachmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

import java.net.MalformedURLException;

import java.io.IOException;

import java.nio.file.Path;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentWorkFlowService attachmentWorkFlowService;

    private final AttachmentService attachmentService;

    /*
     * =====================================================
     * Upload de arquivo
     * =====================================================
     */
    @PostMapping("/upload/{taskId}")
    public ResponseEntity<Attachment> upload(

            @PathVariable Long taskId,

            @RequestParam("file")
            MultipartFile file,

            Authentication authentication

    ) throws IOException {

        String login =
                authentication.getName();

        Attachment attachment =
                attachmentWorkFlowService.upload(
                        taskId,
                        file,
                        login
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attachment);
    }

    
    
    /*
     * =====================================================
     * Download de arquivo
     * =====================================================
     */
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> download(

            @PathVariable Long attachmentId,

            Authentication authentication

    ) throws IOException {

        String login =
                authentication.getName();

        Attachment attachment =
                attachmentService.findById(
                        attachmentId
                );

        Path path =
                attachmentWorkFlowService.download(
                        attachmentId,
                        login
                );

        Resource resource =
                new UrlResource(
                        path.toUri()
                );

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + attachment.getOriginalFileName()
                                + "\""
                )

                .body(resource);
    }
    
    /*
     * =====================================================
     * Soft Delete do anexo
     * =====================================================
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> delete(

            @PathVariable
            Long attachmentId,

            Authentication authentication

    ) {

        String login =
                authentication.getName();

        attachmentWorkFlowService
                .deactivateAttachment(

                        attachmentId,

                        login
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}