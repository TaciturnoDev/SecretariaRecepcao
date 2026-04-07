package com.math.taskmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.math.taskmanager.dto.UserRequestDTO;
import com.math.taskmanager.dto.UserResponseDTO;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * ==============================
     * 🔥 CRIAR USUÁRIO COM SETOR
     * ==============================
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO dto) {

        User created = userService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponseDTO(created));
    }

    /*
     * ==============================
     * LISTAR TODOS
     * ==============================
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {

        List<UserResponseDTO> users = userService.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    /*
     * ==============================
     * BUSCAR POR ID
     * ==============================
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponseDTO(userService.findById(id)));
    }

    /*
     * ==============================
     * BUSCAR POR NOME (ATIVO)
     * ==============================
     */
    @GetMapping("/profile/{name}")
    public ResponseEntity<UserResponseDTO> findByName(@PathVariable String name) {
        return ResponseEntity.ok(new UserResponseDTO(userService.findActiveByName(name)));
    }

    /*
     * ==============================
     * DESATIVAR USUÁRIO
     * ==============================
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * ==============================
     * DELETE REAL
     * ==============================
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * ==============================
     * 🔥 SUPERADMIN - ALTERAR SETOR
     * ==============================
     */
    @PutMapping("/{id}/sector/{sectorId}")
    public ResponseEntity<Void> updateSector(
            @PathVariable Long id,
            @PathVariable Long sectorId
    ) {

        userService.updateSector(id, sectorId);

        return ResponseEntity.noContent().build();
    }
}