package com.math.taskmanager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/auth/me")
    public Map<String, Object> getUser(Authentication authentication) {

        return Map.of(
            "username", authentication.getName()
        );
    }
}