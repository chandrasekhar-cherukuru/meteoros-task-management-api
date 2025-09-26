package com.chakri.backendinternminiproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * Task entity representing user tasks in the system.
 * Each task belongs to one user and has automatic timestamp management.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated primary key

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT") // Allows longer descriptions
    private String description;

    @Pattern(regexp = "todo|in-progress|done",
            message = "Status must be: todo, in-progress, or done")
    @Column(nullable = false, length = 20)
    private String status = "todo"; // Default status

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Set once, never updated

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Updated automatically on changes

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Prevent circular reference in JSON serialization
    private User user; // Task owner

    // Default constructor - sets timestamps automatically
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Task(String title, String description, String status, User user) {
        this();
        this.title = title;
        this.description = description;
        this.status = status != null ? status : "todo";
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // JPA callback to update timestamp before database update
    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
}
