package com.math.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // 🔄 Converte Entity → DTO de resposta
    private TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }

    // ✅ Criar tarefa usando DTO
    public TaskResponseDTO save(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());

        Task savedTask = taskRepository.save(task);
        return toDTO(savedTask);
    }

 // 📋 Listar tarefas com paginação
   /*public Page<TaskResponseDTO> getAllTasksPaged(int page, int size) {
	   
	   Pageable pageable = PageRequest.of(page,  size);
	   
	   Page<Task> taskPage = taskRepository.findAll(pageable);
	   
	   return taskPage.map(this::toDTO);
   }*/
    // 📋 Listar tarefas com paginação
    public Page<TaskResponseDTO> getAllTasksPaged(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findAll(pageable);

        return taskPage.map(this::toDTO);
    }
    // 📋 ordena pela mais recente
    public Page<TaskResponseDTO> getTasksByStatusPaged(TaskStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Task> taskPage = taskRepository.findByStatus(status, pageable);

        return taskPage.map(this::toDTO);
    }



    // 🔍 Buscar por ID
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        return toDTO(task);
    }

    // ✏️ Atualizar tarefa
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());

        Task updatedTask = taskRepository.save(task);
        return toDTO(updatedTask);
    }

    // ❌ Deletar tarefa
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        taskRepository.delete(task);
    }
}

    // 🔎 Buscar por status
   /* public List<TaskResponseDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}*/
