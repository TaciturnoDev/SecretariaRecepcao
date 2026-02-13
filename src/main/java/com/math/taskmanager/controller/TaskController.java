package com.math.taskmanager.controller;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ========= CREATE =========
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
            @RequestBody @Valid TaskRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(dto));
    }

    // ========= LIST ALL (PAGINATED) =========
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                taskService.findAll(page, size)
        );
    }

    // ========= FIND BY ID =========
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.findById(id)
        );
    }

    // ========= UPDATE =========
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequestDTO dto) {

        return ResponseEntity.ok(
                taskService.update(id, dto)
        );
    }

    // ========= DELETE =========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ========= FIND BY STATUS =========
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskResponseDTO>> findByStatus(
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                taskService.findByStatus(status, page, size)
        );
    }

    // ========= FIND BY USER =========
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TaskResponseDTO>> findByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                taskService.findByUserId(userId, page, size)
        );
    }
}