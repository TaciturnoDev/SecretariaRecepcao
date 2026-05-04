package com.math.taskmanager.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import com.math.taskmanager.dto.AdminUserDTO;
import com.math.taskmanager.dto.TaskResponseDTO;
import com.math.taskmanager.entity.Role;
import com.math.taskmanager.service.AdminService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminController {

    private final AdminService adminService;

    /* ===================================================== */
    /*  USUÁRIOS                                           */
    /* ===================================================== */

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                adminService.getAllUsers(PageRequest.of(page, size))
        );
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Void> changeRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        adminService.changeUserRole(id, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/sector")
    public ResponseEntity<Void> changeSector(
            @PathVariable Long id,
            @RequestParam Long sectorId
    ) {
        adminService.changeUserSector(id, sectorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /*
     *  DELETE REAL (usar com cuidado)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /* ===================================================== */
    /*  TAREFAS                                            */
    /* ===================================================== */

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                adminService.getAllTasks(PageRequest.of(page, size))
        );
    }
}