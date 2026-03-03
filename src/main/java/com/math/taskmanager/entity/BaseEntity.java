package com.math.taskmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*
 * Classe base para auditoria automática.
 * NÃO cria tabela no banco.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /*
     * Data de criação automática.
     * Preenchida apenas na primeira persistência.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*
     * Atualizado automaticamente sempre que houver update.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}