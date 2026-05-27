package com.math.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= TAREFA ================= */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    /* ================= USUÁRIO ================= */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /* ================= AÇÃO ================= */
    @Column(columnDefinition = "TEXT")
    private String action;

    /* ================= DATA ================= */
    private LocalDateTime createdAt;
}