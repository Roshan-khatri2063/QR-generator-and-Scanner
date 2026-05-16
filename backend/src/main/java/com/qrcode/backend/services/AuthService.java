package com.qrcode.backend.services;

import com.qrcode.backend.dao.Member;
import com.qrcode.backend.util.JwtUtil;
import com.qrcode.backend.util.PasswordUtil;
import com.qrcode.backend.util.ValidationUtil;

import java.util.Optional;
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
        public final String  token;    // JWT — valid only on login success

        private AuthResult(boolean success, String message, int userId, String token) {
            this.success = success;
            this.message = message;
            this.userId  = userId;
            this.token   = token;
        }

        public static AuthResult ok(String message, int userId) {
            return new AuthResult(true, message, userId, null);
        }

        public static AuthResult okWithToken(String message, int userId, String token) {
            return new AuthResult(true, message, userId, token);
        }

        public static AuthResult fail(String message) {
            return new AuthResult(false, message, -1, null);
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

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------

    /**
     * Authenticates an existing user and returns a JWT on success.
     *
     * Steps:
     *  1. Validate inputs
     *  2. Look up user by email
     *  3. Verify password with BCrypt
     *  4. Generate and return a JWT
     *
     * @param email    the user's email address
     * @param password the plain-text password to verify
     * @return AuthResult with token on success, or fail with reason
     */
    public AuthResult login(String email, String password) {

        // 1. Validate inputs
        String validationError = ValidationUtil.validateLogin(email, password);
        if (validationError != null) {
            return AuthResult.fail(validationError);
        }

        // 2. Find user by email
        Optional<Member> memberOpt = Member.findByEmail(email.trim().toLowerCase());
        if (memberOpt.isEmpty()) {
            // Use a generic message to avoid leaking whether the email exists
            return AuthResult.fail("Invalid email or password.");
        }

        Member member = memberOpt.get();

        // 3. Verify password
        if (!PasswordUtil.verify(password, member.getPasswordHash())) {
            return AuthResult.fail("Invalid email or password.");
        }

        // 4. Generate JWT
        String token = JwtUtil.generateToken(member.getId(), member.getUsername(), member.getEmail());

        LOGGER.info("User logged in: id=" + member.getId() + ", username=" + member.getUsername());
        return AuthResult.okWithToken(
                "Welcome back, " + member.getUsername() + "!",
                member.getId(),
                token
        );
    }
}
