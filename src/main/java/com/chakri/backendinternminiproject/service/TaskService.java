package com.chakri.backendinternminiproject.service;

import com.chakri.backendinternminiproject.model.Task;
import com.chakri.backendinternminiproject.model.User;
import com.chakri.backendinternminiproject.repo.TaskRepository;
import com.chakri.backendinternminiproject.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for task management operations.
 * Ensures user ownership validation for all task operations to maintain data security.
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new task for the authenticated user.
     * Associates task with user and returns HTTP 201 Created status.
     */
    public ResponseEntity<?> createTask(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user); // Associate task with current user
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    /**
     * Retrieves all tasks belonging to the authenticated user.
     * Tasks are ordered by creation date (newest first).
     */
    public ResponseEntity<List<Task>> getAllTasksForUser(String username) {
        List<Task> tasks = taskRepository.findByUserUsernameOrderByCreatedAtDesc(username);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Updates an existing task if user owns it.
     * Validates ownership before allowing any modifications.
     */
    public ResponseEntity<?> updateTask(Long id, Task taskRequest, String username) {
        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        // Update task fields
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());

        Task updatedTask = taskRepository.save(task); // Automatic timestamp update via @PreUpdate
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Deletes a task if user owns it.
     * Uses custom repository method to ensure ownership validation at database level.
     */
    @Transactional // Required for delete operations
    public ResponseEntity<?> deleteTask(Long id, String username) {
        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.deleteByIdAndUserUsername(id, username); // Owner-validated delete

        // Return success response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Task deleted successfully!");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
