package com.math.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.service.TaskService;

import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /* ===================================================== */
    /* 📝 CRIAR TAREFA                                      */
    /* ===================================================== */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
            @Valid @RequestBody TaskRequestDTO dto,
            Authentication authentication
    ) {

        String login = authentication.getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(dto, login));
    }

    /* ===================================================== */
    /* ✏️ ATUALIZAR TAREFA (🔥 CORREÇÃO PRINCIPAL)          */
    /* ===================================================== */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO dto,
            Authentication authentication
    ) {

        String login = authentication.getName();

        return ResponseEntity.ok(
                taskService.update(id, dto, login)
        );
    }

    /* ===================================================== */
    /* 📋 LISTAR TAREFAS                                    */
    /* ===================================================== */
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication
    ) {

        String login = authentication.getName();

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                taskService.findAll(userId, status, pageable, login)
        );
    }

    /* ===================================================== */
    /* 🗑️ DELETAR TAREFA                                   */
    /* ===================================================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String login = authentication.getName();

        taskService.delete(id, login);

        return ResponseEntity.noContent().build();
    }
}