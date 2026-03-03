package com.math.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.math.taskmanager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * Busca usuário pelo CPF.
     */
    Optional<User> findByCpf(String cpf);

    /*
     * Busca usuário ativo pelo nome.
     * Útil para seu modelo de "login por nome".
     */
    Optional<User> findByNameAndActiveTrue(String name);
}