package com.chakri.backendinternminiproject.controller;

import com.chakri.backendinternminiproject.dto.LoginDto;
import com.chakri.backendinternminiproject.dto.RegisterDto;
import com.chakri.backendinternminiproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for user registration and login.
 * Public endpoints that don't require JWT authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // User registration endpoint - creates new user account
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterDto registerDto) {
        try {
            Map<String, Object> result = authService.register(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result); // 201 Created
        } catch (Exception e) {
            // Return 400 Bad Request for validation errors or duplicate users
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // User login endpoint - validates credentials and returns JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            Map<String, Object> result = authService.login(loginDto);
            return ResponseEntity.ok(result); // 200 OK with JWT token
        } catch (Exception e) {
            // Return 401 Unauthorized for invalid credentials
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
