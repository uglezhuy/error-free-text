package com.example.error_free_text.scheduler;

import com.example.error_free_text.entity.Task;
import com.example.error_free_text.entity.TaskStatus;
import com.example.error_free_text.repository.TaskRepository;
import com.example.error_free_text.service.TaskProcessingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskProcessingScheduler {

    private final TaskRepository taskRepository;
    private final TaskProcessingService taskProcessingService;

    public TaskProcessingScheduler(
            TaskRepository taskRepository,
            TaskProcessingService taskProcessingService) {
        this.taskRepository = taskRepository;
        this.taskProcessingService = taskProcessingService;
    }

    @Scheduled(fixedDelay = 5000)
    public void processTasks() {

        List<Task> tasks = taskRepository.findByStatus(TaskStatus.NEW);

        System.out.println(
                "Найдено новых задач: " + tasks.size());

        for (Task task : tasks) {

            taskProcessingService.processTask(
                    task.getId());
        }
    }
}