package com.math.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.TaskRepository;
import com.math.taskmanager.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void deveCriarTarefaComSucesso() {

        // =========================
        // ARRANGE
        // =========================

        User user = new User();
        user.setId(1L);
        user.setName("Marcos");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Teste");
        task.setDescription("Descrição");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUser(user);

        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição",
                1L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        // =========================
        // ACT
        // =========================

        TaskResponseDTO response = taskService.create(dto);

        // =========================
        // ASSERT
        // =========================

        assertNotNull(response);
        assertEquals("Teste", response.title());
        assertEquals(TaskStatus.PENDING, response.status());
        assertEquals(1L, response.userId());
        assertEquals("Marcos", response.userName());
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {

        // =========================
        // ARRANGE
        // =========================

        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição",
                99L
        );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // =========================
        // ACT + ASSERT
        // =========================

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.create(dto)
        );
    }
}