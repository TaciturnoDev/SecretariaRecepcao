package com.math.taskmanager.controller;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
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

        TaskResponseDTO task = taskService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    // ========= LIST ALL =========
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(taskService.findAll(page, size));
    }

    // ========= LIST BY USER =========
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