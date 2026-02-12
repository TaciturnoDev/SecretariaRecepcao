package com.math.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.TaskRepository;
import com.math.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // ========= DTO MAPPER =========
    private TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }

    // ========= CREATE =========
    public TaskResponseDTO create(TaskRequestDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário não encontrado com ID: " + dto.userId()
                        )
                );

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setUser(user);

        return toDTO(taskRepository.save(task));
    }

    // ========= LIST ALL =========
    public Page<TaskResponseDTO> findAll(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return taskRepository.findAll(pageable)
                .map(this::toDTO);
    }

    // ========= LIST BY USER =========
    public Page<TaskResponseDTO> findByUserId(
            Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return taskRepository.findByUserId(userId, pageable)
                .map(this::toDTO);
    }
}