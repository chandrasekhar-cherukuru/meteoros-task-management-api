package com.chakri.backendinternminiproject.repo;

import com.chakri.backendinternminiproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations.
 * Extends JpaRepository to provide CRUD operations and custom user-specific queries.
 * Ensures tasks are isolated by user for security.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all tasks for a specific user, ordered by creation date (newest first)
    List<Task> findByUserUsernameOrderByCreatedAtDesc(String username);

    // Find a specific task only if it belongs to the given user
    Optional<Task> findByIdAndUserUsername(Long id, String username);

    // Custom delete query that ensures user ownership before deletion
    @Transactional // Required for modifying operations
    @Modifying // Indicates this query modifies data
    @Query("DELETE FROM Task t WHERE t.id = :id AND t.user.username = :username")
    void deleteByIdAndUserUsername(@Param("id") Long id, @Param("username") String username);
}
