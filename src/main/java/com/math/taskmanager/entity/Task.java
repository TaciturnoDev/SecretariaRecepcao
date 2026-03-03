package com.math.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

/*
 * Soft delete automático
 */
@SQLDelete(sql = "UPDATE tasks SET active = false WHERE id = ?")

/*
 * Ignora registros inativos automaticamente
 */
@Where(clause = "active = true")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /*
     * Soft delete flag
     */
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = TaskStatus.PENDING;
        }
        if (this.active == null) {
            this.active = true;
        }
    }
}