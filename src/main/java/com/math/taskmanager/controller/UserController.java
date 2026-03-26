package com.math.taskmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.math.taskmanager.dto.UserResponseDTO; // 🔥 IMPORT IMPORTANTE
import com.math.taskmanager.entity.User;
import com.math.taskmanager.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
     * Criar funcionário
     */
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /*
     * Listar todos
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
     * Buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponseDTO(userService.findById(id)));
    }

    /*
     * Buscar por nome (somente ativo)
     */
    @GetMapping("/profile/{name}")
    public ResponseEntity<UserResponseDTO> findByName(@PathVariable String name) {
        return ResponseEntity.ok(new UserResponseDTO(userService.findActiveByName(name)));
    }

    /*
     * Inativar funcionário
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Exclusão física
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}