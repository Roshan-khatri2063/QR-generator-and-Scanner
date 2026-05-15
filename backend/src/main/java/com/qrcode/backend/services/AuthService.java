package com.qrcode.backend.services;

import com.qrcode.backend.dao.Member;
import com.qrcode.backend.util.PasswordUtil;
import com.qrcode.backend.util.ValidationUtil;

import java.util.logging.Logger;

/**
 * Business logic for user authentication — registration and login.
 *
 * <p>This class sits between the servlet (controller) and the DAO (Member),
 * handling validation, password hashing, and duplicate checks before
 * any database write.</p>
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    /**
     * Represents the outcome of a service operation.
     * Contains either a success message or an error message.
     */
    public static class AuthResult {
        public final boolean success;
        public final String  message;
        public final int     userId;   // valid only on success

        private AuthResult(boolean success, String message, int userId) {
            this.success = success;
            this.message = message;
            this.userId  = userId;
        }

        public static AuthResult ok(String message, int userId) {
            return new AuthResult(true, message, userId);
        }

        public static AuthResult fail(String message) {
            return new AuthResult(false, message, -1);
        }
    }

    // ------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------

    /**
     * Registers a new user.
     *
     * Steps:
     *  1. Validate all input fields
     *  2. Check for duplicate email / username
     *  3. Hash the password with BCrypt
     *  4. Save to the database via Member DAO
     *
     * @param username plain username
     * @param email    plain email
     * @param password plain password (will be hashed before storage)
     * @return AuthResult with success=true and the new user ID, or fail with reason
     */
    public AuthResult register(String username, String email, String password) {

        // 1. Validate inputs
        String validationError = ValidationUtil.validateRegistration(username, email, password);
        if (validationError != null) {
            return AuthResult.fail(validationError);
        }

        // 2. Check duplicates
        if (Member.emailExists(email)) {
            return AuthResult.fail("An account with this email already exists.");
        }
        if (Member.usernameExists(username)) {
            return AuthResult.fail("This username is already taken.");
        }

        // 3. Hash password
        String passwordHash = PasswordUtil.hash(password);

        // 4. Persist
        int newId = Member.save(username.trim(), email.trim().toLowerCase(), passwordHash);

        if (newId == -1) {
            LOGGER.severe("Registration failed at DB layer for email: " + email);
            return AuthResult.fail("Registration failed. Please try again.");
        }

        LOGGER.info("New user registered: id=" + newId + ", username=" + username);
        return AuthResult.ok("Registration successful! Welcome, " + username + ".", newId);
    }
}
