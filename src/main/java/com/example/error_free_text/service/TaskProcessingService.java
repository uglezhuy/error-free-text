package com.example.error_free_text.service;

import com.example.error_free_text.entity.Task;
import com.example.error_free_text.entity.TaskStatus;
import com.example.error_free_text.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TaskProcessingService {

    private final TaskRepository taskRepository;
    private final YandexSpellerService yandexSpellerService;

    public TaskProcessingService(
            TaskRepository taskRepository,
            YandexSpellerService yandexSpellerService) {
        this.taskRepository = taskRepository;
        this.yandexSpellerService = yandexSpellerService;
    }

    public void processTask(UUID taskId) {

        Task task = taskRepository.findById(taskId)
                .orElse(null);

        if (task == null) {
            return;
        }

        try {
            task.setStatus(TaskStatus.PROCESSING);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);

            String correctedText = yandexSpellerService.correctText(
                    task.getOriginalText(),
                    task.getLanguage());

            task.setCorrectedText(correctedText);
            task.setStatus(TaskStatus.DONE);
            task.setErrorMessage(null);
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

        } catch (Exception exception) {

            task.setStatus(TaskStatus.ERROR);
            task.setErrorMessage(exception.getMessage());
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);
        }
    }
}