package com.chakri.backendinternminiproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * User entity representing application users.
 * Implements UserDetails for Spring Security integration.
 * Each user can have multiple tasks in a one-to-many relationship.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated primary key

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false) // Unique constraint for login
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false) // Unique constraint to prevent duplicates
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Password only for input, never output
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Account creation time

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Last modification time

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent circular reference and avoid loading tasks unnecessarily
    private List<Task> tasks = new ArrayList<>();

    // Default constructor - sets timestamps automatically
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; } // Required by UserDetails interface
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; } // Required by UserDetails interface
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    // JPA callback to update timestamp before database update
    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Spring Security UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); } // No roles for this simple app

    @Override
    public boolean isAccountNonExpired() { return true; } // Accounts never expire

    @Override
    public boolean isAccountNonLocked() { return true; } // No account locking mechanism

    @Override
    public boolean isCredentialsNonExpired() { return true; } // Credentials never expire

    @Override
    public boolean isEnabled() { return true; } // All accounts are enabled
}
