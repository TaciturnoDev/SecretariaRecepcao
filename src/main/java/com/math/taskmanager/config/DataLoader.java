package com.math.taskmanager.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.math.taskmanager.entity.Role;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.repository.UserRepository;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {

            User user = new User();
            user.setName("Administrador");
            user.setLogin("admin");
            user.setCpf("00000000000");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(Role.SUPER_ADMIN);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            System.out.println("✅ Usuário inicial criado!");
            System.out.println("Login: admin");
            System.out.println("Senha: admin123");
        }
    }
}