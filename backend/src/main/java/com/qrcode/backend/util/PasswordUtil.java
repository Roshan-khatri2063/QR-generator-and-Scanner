package com.qrcode.backend.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Secure password hashing using BCrypt.
 *
 * <p>BCrypt automatically generates a salt and embeds it in the hash,
 * so you never need to store the salt separately. The cost factor (12)
 * controls how slow the hash is — higher = more secure, but slower.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * // On registration:
 * String hash = PasswordUtil.hash("mypassword");
 * Member.save(username, email, hash);
 *
 * // On login:
 * boolean ok = PasswordUtil.verify("mypassword", storedHash);
 * }</pre>
 */
public class PasswordUtil {

    /** BCrypt cost factor — 12 is a good balance of speed vs security (2024). */
    private static final int COST = 12;

    // Utility class — no instantiation
    private PasswordUtil() {}

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainPassword the raw password entered by the user
     * @return a BCrypt hash string (60 chars) safe to store in the database
     */
    public static String hash(String plainPassword) {
        return BCrypt.withDefaults()
                .hashToString(COST, plainPassword.toCharArray());
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword the raw password entered by the user
     * @param storedHash    the BCrypt hash from the database
     * @return true if the password matches the hash
     */
    public static boolean verify(String plainPassword, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer()
                .verify(plainPassword.toCharArray(), storedHash);
        return result.verified;
    }
}
