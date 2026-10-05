package com.example.error_free_text.repository;

import com.example.error_free_text.entity.Task;
import com.example.error_free_text.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStatus(TaskStatus status);
}