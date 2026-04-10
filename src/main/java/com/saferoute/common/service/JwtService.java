package com.saferoute.common.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service for JWT token generation and validation.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Generate an access token for a user with multiple roles.
     */
    public String generateAccessToken(UUID userId, String email, Set<String> roles) {
        return buildToken(userId, email, roles, accessTokenExpiration);
    }

    /**
     * Generate a refresh token for a user with multiple roles.
     */
    public String generateRefreshToken(UUID userId, String email, Set<String> roles) {
        return buildToken(userId, email, roles, refreshTokenExpiration);
    }

    /**
     * Build a JWT token with the given claims.
     */
    private String buildToken(UUID userId, String email, Set<String> roles, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", new ArrayList<>(roles))  // Set to List for JWT
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get the signing key for JWT.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extract user ID from token.
     */
    public UUID extractUserId(String token) {
        String subject = extractSubject(token);
        return UUID.fromString(subject);
    }

    /**
     * Extract email from token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, "email", String.class);
    }

    /**
     * Extract roles from token (returns Set).
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        List<String> rolesList = extractClaim(token, "roles", List.class);
        return rolesList != null ? new HashSet<>(rolesList) : Collections.emptySet();
    }

    /**
     * Extract primary role from token (first role in the set).
     * For backwards compatibility.
     */
    public String extractRole(String token) {
        Set<String> roles = extractRoles(token);
        return roles.isEmpty() ? null : roles.iterator().next();
    }

    /**
     * Extract subject (user ID) from token.
     */
    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extract a specific claim from token.
     */
    public <T> T extractClaim(String token, String claim, Class<T> clazz) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claim, clazz);
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Check if token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Get expiration time in seconds for response.
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }
}
