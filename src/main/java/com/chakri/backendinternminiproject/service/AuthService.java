package com.chakri.backendinternminiproject.service;

import com.chakri.backendinternminiproject.dto.LoginDto;
import com.chakri.backendinternminiproject.dto.RegisterDto;
import com.chakri.backendinternminiproject.model.User;
import com.chakri.backendinternminiproject.repo.UserRepository;
import com.chakri.backendinternminiproject.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for user authentication and registration.
 * Handles business logic for user signup, login, and JWT token generation.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt encoder for secure password hashing

    @Autowired
    private AuthenticationManager authenticationManager; // Spring Security authentication

    @Autowired
    private JwtUtil jwtUtil; // JWT token utility

    /**
     * Registers a new user account with validation checks.
     * Ensures username and email uniqueness before creating user.
     */
    public Map<String, Object> register(RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate username uniqueness
            if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists");
            }

            // Validate email uniqueness
            if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }

            // Create and populate new user entity
            User user = new User();
            user.setUsername(registerDto.getUsername());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword())); // Hash password
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user to database
            User savedUser = userRepository.save(user);

            // Build success response with user details (excluding password)
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                    "id", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail()
            ));

            return response;
        } catch (Exception e) {
            // Return error response for any validation or database issues
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * Authenticates user credentials and generates JWT token.
     * Uses Spring Security's AuthenticationManager for credential verification.
     */
    public Map<String, Object> login(LoginDto loginDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Authenticate credentials using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            // Fetch user details after successful authentication
            User user = userRepository.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token for authenticated user
            String token = jwtUtil.generateToken(user.getUsername());

            // Build success response with token and user details
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
            ));

            return response;
        } catch (Exception e) {
            // Return generic error message to prevent information disclosure
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return response;
        }
    }
}
