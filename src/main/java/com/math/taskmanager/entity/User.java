package com.math.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /*
     * ID gerado automaticamente pelo banco.
     * Estratégia IDENTITY = auto incremento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Um usuário pode ter várias tarefas.
     * mappedBy indica que o relacionamento é controlado pela entidade Task.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<Task> tasks;
    
    /*
     * Nome do funcionário.
     * Não pode ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /*
     * CPF único no sistema.
     * unique = true garante que não existam dois iguais.
     */
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    /*
     * Indica se o funcionário está ativo.
     * Nunca deletamos fisicamente — apenas desativamos.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /*
     * Data de criação do registro.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*
     * Executado automaticamente antes de persistir no banco.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }
}