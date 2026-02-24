package com.math.taskmanager.config;

import com.math.taskmanager.entity.User;
import com.math.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Classe responsável por inserir dados iniciais no banco
 * Útil para ambiente de desenvolvimento
 */
@Configuration
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {

        // Evita criar o usuário mais de uma vez
        if (userRepository.count() == 0) {

            User user = new User();
            user.setName("Usuário Padrão");
            user.setEmail("admin@taskmanager.local");
            user.setPassword("123456"); // depois entra criptografia

            userRepository.save(user);

            System.out.println("✔ Usuário padrão criado com ID = " + user.getId());
        }
    }
}