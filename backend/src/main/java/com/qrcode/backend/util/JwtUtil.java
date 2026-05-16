package com.qrcode.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JWT (JSON Web Token) utility for QRHub.
 *
 * <p>Generates signed tokens on login and validates them on
 * protected endpoints. Tokens expire after {@link #EXPIRY_MS}.</p>
 *
 * <p><strong>Important:</strong> In production, move the secret to an
 * environment variable or secrets manager — never hard-code it.</p>
 *
 * <pre>{@code
 * // On login:
 * String token = JwtUtil.generateToken(userId, username);
 *
 * // On protected request:
 * Claims claims = JwtUtil.validateToken(token);
 * int userId = claims.get("userId", Integer.class);
 * }</pre>
 */
public class JwtUtil {

    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());

    /** Token lifetime: 24 hours */
    private static final long EXPIRY_MS = 24 * 60 * 60 * 1000L;

    /**
     * Secret key — in production read from environment:
     *   System.getenv("JWT_SECRET")
     * Must be at least 256 bits (32 chars) for HMAC-SHA256.
     */
    private static final String SECRET =
            System.getenv("JWT_SECRET") != null
                    ? System.getenv("JWT_SECRET")
                    : "QRHub-super-secret-key-change-in-production-2024!";

    private static final SecretKey SIGNING_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Utility class — no instantiation
    private JwtUtil() {}

    // ------------------------------------------------------------------
    // Token generation
    // ------------------------------------------------------------------

    /**
     * Generates a signed JWT for an authenticated user.
     *
     * @param userId   the user's database ID
     * @param username the user's username
     * @param email    the user's email
     * @return a compact JWT string (3 base64url parts joined by dots)
     */
    public static String generateToken(int userId, String username, String email) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId",   userId)
                .claim("username", username)
                .claim("email",    email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(SIGNING_KEY)
                .compact();
    }

    // ------------------------------------------------------------------
    // Token validation
    // ------------------------------------------------------------------

    /**
     * Validates a JWT string and returns its claims.
     *
     * @param token the raw JWT string (without "Bearer " prefix)
     * @return the parsed {@link Claims}, never null
     * @throws JwtException if the token is invalid, expired, or tampered
     */
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns true if the token is valid and not expired; false otherwise.
     * Safe to call without try/catch — all exceptions are swallowed.
     */
    public static boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the Bearer token from an Authorization header value.
     * Returns null if the header is missing or malformed.
     *
     * @param authHeader value of the Authorization header
     * @return the raw token string, or null
     */
    public static String extractBearerToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
