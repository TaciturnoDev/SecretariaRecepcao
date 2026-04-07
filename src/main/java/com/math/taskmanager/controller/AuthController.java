package com.math.taskmanager.controller;

import com.math.taskmanager.dto.UserRequestDTO;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    /* ===================================================== */
    /* 🔐 USUÁRIO LOGADO                                     */
    /* ===================================================== */
    @GetMapping("/me")
    public ResponseEntity<?> getUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Usuário não autenticado"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "username", authentication.getName()
                )
        );
    }

    /* ===================================================== */
    /* 👤 PRIMEIRO ACESSO / CRIAÇÃO DE USUÁRIO               */
    /* ===================================================== */
    @PostMapping("/first-access")
    public ResponseEntity<?> firstAccess(@RequestBody UserRequestDTO dto) {

        try {

            User savedUser = userService.create(dto);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Usuário criado com sucesso",
                            "userId", savedUser.getId()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error", e.getMessage()
                            )
                    );
        }
    }
}