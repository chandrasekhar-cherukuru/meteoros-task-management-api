package com.chakri.backendinternminiproject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class for token generation, validation, and parsing.
 * Uses HMAC SHA-256 algorithm for signing tokens with configurable secret and expiration.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey1234567890123456789012345678901234567890}")
    private String secret; // JWT signing secret from application.properties

    @Value("${jwt.expiration:86400}")
    private int jwtExpiration; // Token expiration in seconds (default 24 hours)

    /**
     * Creates signing key from secret string for JWT operations.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Token parsing methods - extract specific claims from JWT

    /**
     * Extracts username from JWT token subject claim.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts expiration date from JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from JWT token using provided resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and validates JWT token, returning all claims.
     * Throws exception for invalid or malformed tokens.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if token has expired by comparing expiration date with current time.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Token generation methods - create JWT tokens

    /**
     * Generates JWT token from username string.
     * Used during login process when we have username directly.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Generates JWT token from UserDetails object.
     * Used when we have full UserDetails from Spring Security context.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates JWT token with claims, subject, and timestamps.
     * Sets issued time to current time and expiration based on configuration.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // Username stored as subject
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000L)) // Convert seconds to milliseconds
                .signWith(getSigningKey())
                .compact();
    }

    // Token validation methods - verify token authenticity and freshness

    /**
     * Validates JWT token against UserDetails.
     * Checks username match and expiration status.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validates JWT token against username string.
     * Alternative validation method when UserDetails is not available.
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
