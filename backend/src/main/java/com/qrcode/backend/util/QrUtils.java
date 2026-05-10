package com.qrcode.backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility helpers for the QR backend.
 */
public class QrUtils {

    private QrUtils() {}   // utility class — no instantiation

    /**
     * Returns true if the string is null or blank.
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Produces a simple SHA-256 hex hash of the input.
     * Not suitable for password hashing in production — use BCrypt/Argon2 there.
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Builds a JSON error response string.
     */
    public static String errorJson(String message) {
        return "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
    }

    /**
     * Builds a JSON success response string.
     */
    public static String successJson(String message) {
        return "{\"message\":\"" + message.replace("\"", "\\\"") + "\"}";
    }
}
