package com.math.taskmanager.config;

import com.math.taskmanager.entity.User;
import com.math.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Classe responsável por inserir dados iniciais no banco.
 * Executa automaticamente quando a aplicação sobe.
 * Ideal apenas para ambiente de desenvolvimento.
 */
@Configuration
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {

        /*
         * Evita recriar dados toda vez que a aplicação reinicia.
         */
        if (userRepository.count() == 0) {

            User user = User.builder()
                    .name("Marcos")
                    .cpf("564.321.653-58")
                    .active(true)
                    .build();

            userRepository.save(user);

            System.out.println("✔ Funcionário padrão criado com ID = " + user.getId());
        }
    }
}