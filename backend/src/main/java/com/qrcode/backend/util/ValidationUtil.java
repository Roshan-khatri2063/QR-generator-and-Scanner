package com.qrcode.backend.util;

import java.util.regex.Pattern;

/**
 * Input validation helpers for registration and login fields.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 50;
    private static final int PASSWORD_MIN = 6;

    // Utility class — no instantiation
    private ValidationUtil() {}

    /**
     * Validates a registration request.
     * Returns null if all fields are valid, or an error message string.
     */
    public static String validateRegistration(String username, String email, String password) {
        if (QrUtils.isBlank(username)) return "Username is required.";
        if (username.length() < USERNAME_MIN)
            return "Username must be at least " + USERNAME_MIN + " characters.";
        if (username.length() > USERNAME_MAX)
            return "Username must be at most " + USERNAME_MAX + " characters.";
        if (!username.matches("[A-Za-z0-9_]+"))
            return "Username may only contain letters, numbers, and underscores.";

        if (QrUtils.isBlank(email)) return "Email is required.";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "Invalid email address.";

        if (QrUtils.isBlank(password)) return "Password is required.";
        if (password.length() < PASSWORD_MIN)
            return "Password must be at least " + PASSWORD_MIN + " characters.";

        return null; // all good
    }

    /**
     * Validates a login request.
     * Returns null if valid, or an error message string.
     */
    public static String validateLogin(String email, String password) {
        if (QrUtils.isBlank(email))    return "Email is required.";
        if (QrUtils.isBlank(password)) return "Password is required.";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "Invalid email address.";
        return null;
    }
}
