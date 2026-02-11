package com.math.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.math.taskmanager.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}