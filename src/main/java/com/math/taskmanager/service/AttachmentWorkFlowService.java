package com.math.taskmanager.service;

import com.math.taskmanager.entity.Attachment;
import com.math.taskmanager.entity.Task;
import com.math.taskmanager.entity.TaskHistory;
import com.math.taskmanager.entity.User;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.exception.BusinessRuleException;
import com.math.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class AttachmentWorkFlowService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final TaskHistoryService taskHistoryService;

    private final AttachmentService attachmentService;

    /**
     * =====================================================
     * Upload de arquivo para uma tarefa
     * =====================================================
     */
    public Attachment upload(
            Long taskId,
            MultipartFile file,
            String login
    ) throws IOException {

        /* ================= USUÁRIO ================= */

        User user =
                userService.findByLogin(login);

        /* ================= TAREFA ================= */

        Task task =
                taskRepository.findById(taskId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tarefa não encontrada."
                                )
                        );
        
        /*
         * Permissão
         */

        if (!user.isSuperAdmin()) {

            if (user.getSector() == null) {

                throw new RuntimeException(
                        "Usuário sem setor."
                );
            }

            if (!task
                    .getSector()
                    .getId()
                    .equals(
                            user.getSector().getId()
                    )) {

                throw new RuntimeException(
                        "Você não pode anexar arquivos em tarefas de outro setor."
                );
            }
        }

        /* ================= HISTÓRICO ================= */

        TaskHistory history =
                taskHistoryService.register(

                        task,

                        user,

                        "Anexou arquivo: "
                                + file.getOriginalFilename(),

                        null,
                        null,

                        null,
                        null
                );

        /* ================= ANEXO ================= */

        return attachmentService.saveAttachment(
                file,
                history
        );
    }
    
    /**
     * =====================================================
     * Download de arquivo com validação de acesso
     * =====================================================
     */
    public Path download(
            Long attachmentId,
            String login
    ) {

        User user =
                userService.findByLogin(login);

        Attachment attachment =
                attachmentService.findById(
                        attachmentId
                );

        TaskHistory history =
                attachment.getHistory();

        if (history == null
                || history.getTask() == null) {

            throw new ResourceNotFoundException(
                    "Histórico da tarefa não encontrado."
            );
        }

        Task task =
                history.getTask();

        /*
         * SUPERADMIN
         */
        if (user.isSuperAdmin()) {

            return attachmentService
                    .getAttachmentPath(
                            attachmentId
                    );
        }

        /*
         * Usuário precisa possuir setor
         */
        if (user.getSector() == null) {

            throw new BusinessRuleException(
                    "Usuário sem setor."
            );
        }

        /*
         * Verifica setor da tarefa
         */
        if (!task.getSector()
                .getId()
                .equals(
                        user.getSector().getId()
                )) {

            throw new BusinessRuleException(
                    "Você não possui acesso a este anexo."
            );
        }

        return attachmentService
                .getAttachmentPath(
                        attachmentId
                );
    }
    /**
     * =====================================================
     * Remover anexo (Soft Delete)
     * =====================================================
     */
    public void deactivateAttachment(
            Long attachmentId,
            String login
    ) {

        /*
         * Usuário logado
         */
        User user =
                userService.findByLogin(
                        login
                );

        /*
         * Desativa anexo
         */
        Attachment attachment =
                attachmentService
                        .deactivateAttachment(
                                attachmentId
                        );

        /*
         * Registra histórico
         */
        taskHistoryService.register(

                attachment
                        .getHistory()
                        .getTask(),

                user,

                "Removeu o anexo: "
                        + attachment.getOriginalFileName(),

                null,
                null,

                null,
                null
        );
    }
    
}