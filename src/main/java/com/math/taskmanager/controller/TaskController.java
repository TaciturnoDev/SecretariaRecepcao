package com.math.taskmanager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.service.TaskService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    // ✅ Criar tarefa
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody @Valid TaskRequestDTO dto) {
        TaskResponseDTO savedTask = taskService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    // 📋 Listar todas
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTaskPaged(
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "5") int size) {
    	
    	return ResponseEntity.ok(taskService.getAllTasksPaged(page, size));
    }
    
    // 🔍 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // ✏️ Atualizar tarefa
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,
                                                      @RequestBody @Valid TaskRequestDTO dto) {
        TaskResponseDTO updatedTask = taskService.updateTask(id, dto);
        return ResponseEntity.ok(updatedTask);
    }

    // ❌ Deletar tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Buscar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskResponseDTO>> getByStatus(
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(taskService.getTasksByStatusPaged(status, page, size));
    }
}