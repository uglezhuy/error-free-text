package com.example.error_free_text.service;

import com.example.error_free_text.dto.CreateTaskRequest;
import com.example.error_free_text.dto.TaskResponse;

import com.example.error_free_text.entity.Task;
import com.example.error_free_text.entity.TaskStatus;
import com.example.error_free_text.exception.TaskNotFoundException;
import com.example.error_free_text.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {

        Task task = new Task();

        task.setId(UUID.randomUUID());
        task.setOriginalText(request.getText());
        task.setLanguage(request.getLanguage());
        task.setStatus(TaskStatus.NEW);

        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Task savedTask = taskRepository.save(task);

        return new TaskResponse(
                savedTask.getId(),
                savedTask.getStatus(),
                savedTask.getCorrectedText(),
                savedTask.getErrorMessage());
    }

    public TaskResponse getTask(UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task with id: " + id + " not found"));

        return new TaskResponse(
                task.getId(),
                task.getStatus(),
                task.getCorrectedText(),
                task.getErrorMessage());
    }
}