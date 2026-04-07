package com.math.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.*;
import com.math.taskmanager.exception.BusinessRuleException;
import com.math.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private SectorService sectorService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void deveCriarTarefaComSucesso() {

        String login = "admin";

        // ===== SECTOR =====
        Sector sector = new Sector();
        sector.setId(1L);
        sector.setName("TI");

        // ===== USER =====
        User user = new User();
        user.setId(1L);
        user.setName("Marcos");
        user.setLogin(login);
        user.setSector(sector);
        user.setRole(Role.USER);

        // ===== TASK =====
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Teste");
        task.setDescription("Descrição");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUser(user);
        task.setSector(sector);

        // ⚠️ IMPORTANTE: se seu DTO tem sectorId
        TaskRequestDTO dto = new TaskRequestDTO(
        	    "Teste",
        	    "Descrição",
        	    null,   // status
        	    null    // sectorId
        	);

        when(userService.findByLogin(login)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = taskService.create(dto, login);

        assertNotNull(response);
        assertEquals("Teste", response.title());
        assertEquals(TaskStatus.PENDING, response.status());
        assertEquals(1L, response.userId());
        assertEquals("Marcos", response.userName());
    }

    @Test
    void deveLancarErroQuandoUsuarioSemSetor() {

        String login = "admin";

        User user = new User();
        user.setLogin(login);
        user.setRole(Role.USER);

        TaskRequestDTO dto = new TaskRequestDTO(
        	    "Teste",
        	    "Descrição",
        	    null,   // status
        	    null    // sectorId
        	);

        when(userService.findByLogin(login)).thenReturn(user);

        assertThrows(
                BusinessRuleException.class,
                () -> taskService.create(dto, login)
        );
    }
}