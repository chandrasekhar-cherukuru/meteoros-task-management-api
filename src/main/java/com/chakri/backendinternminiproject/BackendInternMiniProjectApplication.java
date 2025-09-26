package com.chakri.backendinternminiproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for the Task Management API.
 * Entry point for the Meteoros internship assignment application.
 * Configures auto-configuration, component scanning, and JPA repositories.
 */
@SpringBootApplication // Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
@EnableJpaRepositories(basePackages = "com.chakri.backendinternminiproject.repo") // Enable JPA repositories in specific package
public class BackendInternMiniProjectApplication {

    /**
     * Application entry point - starts the embedded Tomcat server on port 9876.
     * Initializes Spring context, auto-configures beans, and launches the REST API.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendInternMiniProjectApplication.class, args);
    }
}
