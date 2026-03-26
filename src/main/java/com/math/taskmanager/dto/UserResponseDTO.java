package com.math.taskmanager.dto;

import com.math.taskmanager.entity.User;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String login;
    private String role;
    private boolean active;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.login = user.getLogin();
        this.role = user.getRole() != null ? user.getRole().name() : "USER";
        this.active = user.getActive();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLogin() { return login; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
}