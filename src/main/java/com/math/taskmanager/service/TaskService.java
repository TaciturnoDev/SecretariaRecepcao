package com.math.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.TaskRepository;
import com.math.taskmanager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /*
     * Criar tarefa vinculada ao usuário logado
     */
    public TaskResponseDTO create(TaskRequestDTO dto) {

        // 🔥 PEGA USUÁRIO LOGADO
        String login = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Task task = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(TaskStatus.PENDING)
                .user(user)
                .build();

        task = taskRepository.save(task);

        return mapToResponse(task); // 🔥 CORRIGIDO
    }

    /*
     * Paginação + filtros combináveis
     */
    public Page<TaskResponseDTO> findAll(
            Long userId,
            TaskStatus status,
            Pageable pageable
    ) {

        Page<Task> page;

        if (userId != null && status != null) {
            page = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } 
        else if (userId != null) {
            page = taskRepository.findByUserId(userId, pageable);
        } 
        else if (status != null) {
            page = taskRepository.findByStatus(status, pageable);
        } 
        else {
            page = taskRepository.findAll(pageable);
        }

        return page.map(this::mapToResponse);
    }

    /*
     * Soft delete
     */
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tarefa não encontrada")
                );

        taskRepository.delete(task);
    }

    /*
     * Mapper entidade -> DTO
     */
    private TaskResponseDTO mapToResponse(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUser().getId(),
                task.getUser().getName()
        );
    }
}