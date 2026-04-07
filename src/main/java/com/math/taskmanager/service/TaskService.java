package com.math.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.*;
import com.math.taskmanager.exception.BusinessRuleException;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final SectorService sectorService;

    /* ===================================================== */
    /* 📝 CRIAR TAREFA                                       */
    /* ===================================================== */
    public TaskResponseDTO create(TaskRequestDTO dto, String login) {

        User user = userService.findByLogin(login);

        Sector sector;

        if (user.getRole() == Role.SUPERADMIN) {

            if (dto.sectorId() == null) {
                throw new BusinessRuleException("SUPERADMIN deve informar o setor.");
            }

            sector = sectorService.findById(dto.sectorId());

        } else {

            if (user.getSector() == null) {
                throw new BusinessRuleException("Usuário não está vinculado a um setor.");
            }

            sector = user.getSector();
        }

        Task task = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .status(dto.status() != null ? dto.status() : TaskStatus.PENDING)
                .user(user)
                .sector(sector)
                .build();

        task = taskRepository.save(task);

        return mapToResponse(task);
    }

    /* ===================================================== */
    /* 📋 LISTAR TAREFAS                                     */
    /* ===================================================== */
    public Page<TaskResponseDTO> findAll(
            Long userId,
            TaskStatus status,
            Pageable pageable,
            String login
    ) {

        User user = userService.findByLogin(login);

        Page<Task> page;

        // 🔥 SUPERADMIN vê tudo
        if (user.getRole() == Role.SUPERADMIN) {

            if (userId != null && status != null) {
                page = taskRepository.findByUserIdAndStatus(userId, status, pageable);
            } else if (userId != null) {
                page = taskRepository.findByUserId(userId, pageable);
            } else if (status != null) {
                page = taskRepository.findByStatus(status, pageable);
            } else {
                page = taskRepository.findAll(pageable);
            }

        } else {

            // 🚨 proteção extra
            if (user.getSector() == null) {
                throw new BusinessRuleException("Usuário sem setor.");
            }

            page = taskRepository.findBySectorId(user.getSector().getId(), pageable);
        }

        return page.map(this::mapToResponse);
    }
    /* ===================================================== */
    /* ✏️ ATUALIZAR TAREFA                                   */
    /* ===================================================== */
    public TaskResponseDTO update(Long id, TaskRequestDTO dto, String login) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tarefa não encontrada")
                );

        User user = userService.findByLogin(login);

        // 🔥 SUPERADMIN pode tudo
        if (user.getRole() == Role.SUPERADMIN) {
            task.setTitle(dto.title());
            task.setDescription(dto.description());

            if (dto.status() != null) {
                task.setStatus(dto.status());
            }

            return mapToResponse(taskRepository.save(task));
        }

        // 🚨 proteção extra
        if (user.getSector() == null) {
            throw new BusinessRuleException("Usuário sem setor.");
        }

        if (!task.getSector().getId().equals(user.getSector().getId())) {
            throw new BusinessRuleException("Você não pode editar tarefas de outro setor.");
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());

        if (dto.status() != null) {
            task.setStatus(dto.status());
        }

        return mapToResponse(taskRepository.save(task));
    }

    /* ===================================================== */
    /* 🗑️ DELETAR TAREFA                                    */
    /* ===================================================== */
    public void delete(Long id, String login) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tarefa não encontrada")
                );

        User user = userService.findByLogin(login);

        // 🔥 SUPERADMIN pode tudo
        if (user.getRole() == Role.SUPERADMIN) {
            taskRepository.delete(task);
            return;
        }

        // 🚨 proteção extra
        if (user.getSector() == null) {
            throw new BusinessRuleException("Usuário sem setor.");
        }

        if (!task.getSector().getId().equals(user.getSector().getId())) {
            throw new BusinessRuleException("Você não pode deletar tarefas de outro setor.");
        }

        taskRepository.delete(task);
    }

    /* ===================================================== */
    /* 🔄 MAPPER                                             */
    /* ===================================================== */
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