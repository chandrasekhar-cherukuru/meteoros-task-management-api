package com.chakri.backendinternminiproject.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main application configuration class.
 *
 * Enables property binding, transaction management, and web customization.
 */
@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
public class AppConfig implements WebMvcConfigurer {

    // Space for future configurations like CORS, interceptors, or custom beans
}
