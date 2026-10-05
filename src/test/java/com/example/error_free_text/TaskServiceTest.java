package com.example.error_free_text;

import com.example.error_free_text.dto.CreateTaskRequest;
import com.example.error_free_text.dto.TaskResponse;
import com.example.error_free_text.entity.Task;
import com.example.error_free_text.entity.TaskStatus;
import com.example.error_free_text.exception.TaskNotFoundException;
import com.example.error_free_text.repository.TaskRepository;
import com.example.error_free_text.service.TaskService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldCreateNewTask() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setText("Привет мир");
        request.setLanguage("RU");

        UUID id = UUID.randomUUID();

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task task = invocation.getArgument(0);
                    task.setId(id);
                    return task;
                });

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(TaskStatus.NEW, response.getStatus());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTask_shouldReturnTask() {
        UUID id = UUID.randomUUID();

        Task task = new Task();
        task.setId(id);
        task.setOriginalText("Привет мир");
        task.setLanguage("RU");
        task.setStatus(TaskStatus.DONE);
        task.setCorrectedText("Привет, мир!");

        when(taskRepository.findById(id))
                .thenReturn(java.util.Optional.of(task));

        TaskResponse response = taskService.getTask(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(TaskStatus.DONE, response.getStatus());
        assertEquals("Привет, мир!", response.getCorrectedText());

        verify(taskRepository).findById(id);
    }

    @Test
    void getTask_shouldThrowExceptionWhenTaskNotFound() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTask(id));

        verify(taskRepository).findById(id);
    }
}