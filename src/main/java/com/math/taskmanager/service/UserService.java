package com.math.taskmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	
	// * Criar usuário
	public User create(User user) {
		return userRepository.save(user);
	}
	
	// * Listar todos os usuários
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	// * Buscar usuário por ID
	public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado com ID: " + id)
                );
    }
	
	// * Deletar usuário
    public void delete(Long id) {
    	User user = findById(id);
    	userRepository.delete(user);
    }
	
}
