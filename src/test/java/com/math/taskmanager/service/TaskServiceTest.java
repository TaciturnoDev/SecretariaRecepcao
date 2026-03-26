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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.entity.User;
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
        // MOCK USUÁRIO LOGADO
        // =========================
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // =========================
        // ARRANGE
        // =========================
        User user = new User();
        user.setId(1L);
        user.setName("Marcos");
        user.setLogin("admin");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Teste");
        task.setDescription("Descrição");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUser(user);

        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição"
        );

        when(userRepository.findByLogin("admin"))
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
        // MOCK USUÁRIO LOGADO
        // =========================
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // =========================
        // ARRANGE
        // =========================
        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição"
        );

        when(userRepository.findByLogin("admin"))
                .thenReturn(Optional.empty());

        // =========================
        // ACT + ASSERT
        // =========================
        assertThrows(
                RuntimeException.class, // 🔥 ajustado ao seu service
                () -> taskService.create(dto)
        );
    }
}