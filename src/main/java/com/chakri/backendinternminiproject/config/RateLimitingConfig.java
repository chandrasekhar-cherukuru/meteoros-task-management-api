package com.chakri.backendinternminiproject.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using Bucket4j token bucket algorithm.
 *
 * Implements different rate limits for authenticated users (10/min)
 * and unauthenticated requests (3/min per IP).
 */
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    // Thread-safe storage for user and IP-based rate limiting buckets
    private final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    /**
     * Gets or creates a rate limiting bucket for authenticated users.
     * Allows 10 requests per minute per user.
     */
    public Bucket getUserBucket(String username) {
        return userBuckets.computeIfAbsent(username, this::createUserBucket);
    }

    /**
     * Gets or creates a rate limiting bucket for IP addresses.
     * Allows 3 requests per minute per IP for unauthenticated endpoints.
     */
    public Bucket getIpBucket(String ip) {
        return ipBuckets.computeIfAbsent(ip, this::createIpBucket);
    }

    // Creates bucket with 10 requests per minute limit
    private Bucket createUserBucket(String username) {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    // Creates bucket with 3 requests per minute limit
    private Bucket createIpBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the rate limiting interceptor to handle all incoming requests
        registry.addInterceptor(new RateLimitingInterceptor(this));
    }
}
