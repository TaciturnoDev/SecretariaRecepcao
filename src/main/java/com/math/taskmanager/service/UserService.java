package com.math.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.BusinessRuleException;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /*
     * Criação de funcionário.
     * Valida se CPF já existe.
     */
    public User create(User user) {

        userRepository.findByCpf(user.getCpf())
                .ifPresent(existing -> {
                    throw new BusinessRuleException("CPF já cadastrado no sistema.");
                });

        user.setActive(true);
        return userRepository.save(user);
    }

    /*
     * Lista todos os usuários (inclusive inativos).
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /*
     * Busca por ID.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado com ID: " + id)
                );
    }

    /*
     * Busca por nome (somente ativos).
     * Esse método simula seu "perfil automático".
     */
    public User findActiveByName(String name) {
        return userRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Funcionário ativo não encontrado com nome: " + name)
                );
    }

    /*
     * Inativação lógica (não deleta).
     */
    public void deactivate(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    /*
     * Exclusão física (usar apenas se realmente necessário).
     */
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}