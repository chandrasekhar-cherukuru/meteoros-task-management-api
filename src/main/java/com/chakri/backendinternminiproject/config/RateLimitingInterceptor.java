package com.chakri.backendinternminiproject.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that enforces rate limiting on all incoming requests.
 *
 * Applies different limits based on authentication status and endpoint type.
 * Returns HTTP 429 with Retry-After header when limits are exceeded.
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingConfig rateLimitingConfig;

    public RateLimitingInterceptor(RateLimitingConfig rateLimitingConfig) {
        this.rateLimitingConfig = rateLimitingConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Apply IP-based rate limiting for public authentication endpoints
        if (path.contains("/register") || path.contains("/login")) {
            return handleIpRateLimit(request, response);
        } else {
            // Apply user-based rate limiting for protected endpoints
            return handleUserRateLimit(request, response);
        }
    }

    /**
     * Handles rate limiting for unauthenticated endpoints (register/login).
     * Limits: 3 requests per minute per IP address.
     */
    private boolean handleIpRateLimit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String clientIp = getClientIP(request);
        Bucket bucket = rateLimitingConfig.getIpBucket(clientIp);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return true; // Allow request
        } else {
            // Rate limit exceeded - return 429 with error details
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Max 3 requests per minute for unauthenticated endpoints\", \"retryAfter\": 60}");
            return false; // Block request
        }
    }

    /**
     * Handles rate limiting for authenticated endpoints.
     * Limits: 10 requests per minute per authenticated user.
     */
    private boolean handleUserRateLimit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String username = authentication.getName();
            Bucket bucket = rateLimitingConfig.getUserBucket(username);

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                return true; // Allow request
            } else {
                // Rate limit exceeded - return 429 with error details
                response.setStatus(429);
                response.setHeader("Retry-After", "60");
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Rate limit exceeded. Max 10 requests per minute for authenticated users\", \"retryAfter\": 60}");
                return false; // Block request
            }
        }

        return true; // Allow unauthenticated requests to proceed
    }

    /**
     * Extracts client IP address from request headers.
     * Handles X-Forwarded-For header for proxy/load balancer scenarios.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
