package com.math.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        // arrange
        User user = new User();
        user.setId(1L);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Teste");

        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição",
                TaskStatus.PENDING,
                1L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(task);

        // act
        TaskResponseDTO response = taskService.create(dto);

        // assert
        assertNotNull(response);
        assertEquals("Teste", response.title());
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        // arrange
        TaskRequestDTO dto = new TaskRequestDTO(
                "Teste",
                "Descrição",
                TaskStatus.PENDING,
                99L
        );

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // act + assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.create(dto)
        );
    }
}