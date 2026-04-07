package com.math.taskmanager.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.math.taskmanager.entity.User;
import com.math.taskmanager.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByLogin(login);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        User user = userOptional.get();

        // 🔥 CORREÇÃO 1: validar ativo
        if (!user.getActive()) {
            throw new UsernameNotFoundException("Usuário inativo");
        }

        // 🔥 CORREÇÃO 2: garantir role
        String role = (user.getRole() != null) 
                ? user.getRole().name() 
                : "USER";

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getLogin())
                .password(user.getPassword())
                .roles(role) // 🔥 MELHOR PRÁTICA
                .build();
    }
}