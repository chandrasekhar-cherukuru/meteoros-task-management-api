package com.chakri.backendinternminiproject.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for user login requests.
 * Contains credentials needed for authentication.
 */
public class LoginDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor for JSON deserialization
    public LoginDto() {}

    // Constructor with all fields
    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters for field access
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
