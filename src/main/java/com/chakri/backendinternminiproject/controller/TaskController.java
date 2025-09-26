package com.chakri.backendinternminiproject.controller;

import com.chakri.backendinternminiproject.model.Task;
import com.chakri.backendinternminiproject.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Task management controller for authenticated users.
 * All endpoints require valid JWT authentication and users can only access their own tasks.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*") // Allow cross-origin requests for frontend integration
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Creates a new task for the authenticated user
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Extract username from JWT token
        return taskService.createTask(task, username);
    }

    // Retrieves all tasks belonging to the authenticated user
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Extract username from JWT token
        return taskService.getAllTasksForUser(username);
    }

    // Updates an existing task (only if user owns it)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Extract username from JWT token
        return taskService.updateTask(id, task, username);
    }

    // Deletes a task (only if user owns it)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Extract username from JWT token
        return taskService.deleteTask(id, username);
    }
}
