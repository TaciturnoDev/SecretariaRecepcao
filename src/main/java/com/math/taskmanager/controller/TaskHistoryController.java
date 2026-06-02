package com.math.taskmanager.controller;

import com.math.taskmanager.dto.TaskHistoryDTO;
import com.math.taskmanager.entity.TaskHistory;
import com.math.taskmanager.service.TaskHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/history")
@RequiredArgsConstructor
public class TaskHistoryController {

    private final TaskHistoryService service;

    @GetMapping("/{taskId}")
    public List<TaskHistoryDTO> findByTask(
            @PathVariable Long taskId
    ) {

        List<TaskHistory> history =
                service.findByTask(taskId);

        return history.stream()
                .map(h -> new TaskHistoryDTO(

                        h.getId(),

                        h.getUser() != null
                                ? h.getUser().getName()
                                : "Sistema",

                        h.getAction(),

                        h.getCreatedAt()

                ))
                .toList();
    }
}