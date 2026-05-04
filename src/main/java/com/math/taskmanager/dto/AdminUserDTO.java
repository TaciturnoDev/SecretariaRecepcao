package com.math.taskmanager.dto;

import com.math.taskmanager.entity.User;

public record AdminUserDTO(
        Long id,
        String name,
        String login,
        String role,
        boolean active,
        String sectorName
) {

    public static AdminUserDTO from(User user) {
        return new AdminUserDTO(
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getRole() != null ? user.getRole().name() : "USER",
                user.getActive(),
                user.getSector() != null ? user.getSector().getName() : null
        );
    }
}