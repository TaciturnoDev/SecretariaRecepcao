package com.math.taskmanager.controller;

import com.math.taskmanager.dto.TaskRequestDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.TaskStatus;
import com.math.taskmanager.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST responsável por expor os endpoints
 * relacionados a tarefas (Task).
 *
 * Atua como camada de entrada da aplicação,
 * delegando regras de negócio para o Service.
 */

@CrossOrigin(origins = "*") 
// Permite chamadas do frontend (JS) independente da origem.
// Em produção, isso deve ser restrito.
@RestController
// Indica que esta classe expõe endpoints REST
@RequestMapping("/tasks")
// Prefixo base da API: /tasks
public class TaskController {

    private final TaskService taskService;

    /**
     * Injeção de dependência via construtor (boa prática).
     * Evita uso de @Autowired em atributos.
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ========= CREATE =========

    /**
     * Cria uma nova tarefa.
     *
     * HTTP: POST /tasks
     * Body: TaskRequestDTO (validado)
     * Retorno: TaskResponseDTO
     * Status: 201 CREATED
     */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(
            @RequestBody @Valid TaskRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(dto));
    }

    // ========= LIST ALL (PAGINATED) =========

    /**
     * Lista todas as tarefas de forma paginada.
     *
     * HTTP: GET /tasks?page=0&size=5
     * Retorno: Page<TaskResponseDTO>
     *
     * page -> página atual (0-based)
     * size -> quantidade de registros por página
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                taskService.findAll(page, size)
        );
    }

    // ========= FIND BY ID =========

    /**
     * Busca uma tarefa pelo ID.
     *
     * HTTP: GET /tasks/{id}
     * Retorno: TaskResponseDTO
     *
     * Se não existir, o Service deve lançar exceção tratada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                taskService.findById(id)
        );
    }

    // ========= UPDATE =========

    /**
     * Atualiza uma tarefa existente.
     *
     * HTTP: PUT /tasks/{id}
     * Body: TaskRequestDTO
     * Retorno: TaskResponseDTO atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequestDTO dto) {

        return ResponseEntity.ok(
                taskService.update(id, dto)
        );
    }

    // ========= DELETE =========

    /**
     * Remove uma tarefa pelo ID.
     *
     * HTTP: DELETE /tasks/{id}
     * Retorno: 204 No Content
     *
     * Boa prática REST:
     * DELETE não retorna corpo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ========= FIND BY STATUS =========

    /**
     * Lista tarefas filtrando por status.
     *
     * HTTP: GET /tasks/status/PENDING?page=0&size=5
     *
     * TaskStatus é um enum, o Spring faz o bind automaticamente.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskResponseDTO>> findByStatus(
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                taskService.findByStatus(status, page, size)
        );
    }

    // ========= FIND BY USER =========

    /**
     * Lista tarefas de um usuário específico.
     *
     * HTTP: GET /tasks/user/{userId}?page=0&size=5
     */
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