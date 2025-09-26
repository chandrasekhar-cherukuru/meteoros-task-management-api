package com.chakri.backendinternminiproject.repo;

import com.chakri.backendinternminiproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides authentication and registration support with unique constraint checks.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username for authentication
    Optional<User> findByUsername(String username);

    // Find user by email (used for validation)
    Optional<User> findByEmail(String email);

    // Check if username already exists (for registration validation)
    boolean existsByUsername(String username);

    // Check if email already exists (for registration validation)
    boolean existsByEmail(String email);
}
