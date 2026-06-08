package com.math.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

/*
 * Soft delete automático
 */
@SQLDelete(sql = "UPDATE attachments SET active = false WHERE id = ?")

/*
 * Ignora anexos inativos automaticamente
 */
@Where(clause = "active = true")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= HISTÓRICO ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private TaskHistory history;

    /* ================= NOME ORIGINAL ================= */

    @Column(nullable = false)
    private String originalFileName;

    /* ================= NOME FÍSICO ================= */

    @Column(nullable = false, unique = true)
    private String storedFileName;

    /*
     * Caminho relativo do arquivo
     *
     * Exemplos:
     * 2026/S1
     * 2026/S2
     */
    @Column(nullable = false, length = 1000)
    private String filePath;

    /* ================= TAMANHO ================= */

    @Column(nullable = false)
    private Long fileSize;

    /* ================= MIME TYPE ================= */

    @Column(nullable = false, length = 255)
    private String contentType;

    /* ================= SOFT DELETE ================= */

    @Column(nullable = false)
    private Boolean active = true;

    /* ================= DATA UPLOAD ================= */

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {

        if (active == null) {
            active = true;
        }

        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}