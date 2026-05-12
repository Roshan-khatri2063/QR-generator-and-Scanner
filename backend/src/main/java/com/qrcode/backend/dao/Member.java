package com.qrcode.backend.dao;

import com.qrcode.backend.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code users} table.
 *
 * <p>All methods open and close their own {@link Connection} via
 * {@link DatabaseConnection#getConnection()}, so callers do not
 * need to manage connections directly.</p>
 *
 * <p>Password hashing (BCrypt) will be added in Day 3 when the
 * auth endpoints are wired up. For now the raw hash string is
 * stored as-is.</p>
 */
public class Member {

    private static final Logger LOGGER = Logger.getLogger(Member.class.getName());

    // ------------------------------------------------------------------
    // Model fields
    // ------------------------------------------------------------------

    private int    id;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public Member() {}

    public Member(int id, String username, String email, String passwordHash, Timestamp createdAt) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.createdAt    = createdAt;
    }

    // ------------------------------------------------------------------
    // Getters & Setters
    // ------------------------------------------------------------------

    public int       getId()           { return id; }
    public void      setId(int id)     { this.id = id; }

    public String    getUsername()                   { return username; }
    public void      setUsername(String username)    { this.username = username; }

    public String    getEmail()                      { return email; }
    public void      setEmail(String email)          { this.email = email; }

    public String    getPasswordHash()                        { return passwordHash; }
    public void      setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void      setCreatedAt(Timestamp t)       { this.createdAt = t; }

    // ------------------------------------------------------------------
    // DAO — Write operations
    // ------------------------------------------------------------------

    /**
     * Inserts a new user into the {@code users} table.
     *
     * @param username     the desired username (must be unique)
     * @param email        the user's email (must be unique)
     * @param passwordHash the pre-hashed password string
     * @return the auto-generated user ID, or -1 on failure
     */
    public static int save(String username, String email, String passwordHash) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    LOGGER.info("User saved with id=" + newId);
                    return newId;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save user: " + e.getMessage(), e);
        }

        return -1;
    }

    // ------------------------------------------------------------------
    // DAO — Read operations
    // ------------------------------------------------------------------

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an {@link Optional} containing the Member, or empty if not found
     */
    public static Optional<Member> findByEmail(String email) {
        String sql = "SELECT id, username, email, password, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find user by email: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Finds a user by their numeric ID.
     *
     * @param id the user ID
     * @return an {@link Optional} containing the Member, or empty if not found
     */
    public static Optional<Member> findById(int id) {
        String sql = "SELECT id, username, email, password, created_at FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find user by id: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Checks whether an email address is already registered.
     *
     * @param email the email to check
     * @return true if a user with this email exists
     */
    public static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check email existence: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Checks whether a username is already taken.
     *
     * @param username the username to check
     * @return true if a user with this username exists
     */
    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check username existence: " + e.getMessage(), e);
        }

        return false;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /** Maps a ResultSet row to a Member object. */
    private static Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getInt("id"));
        m.setUsername(rs.getString("username"));
        m.setEmail(rs.getString("email"));
        m.setPasswordHash(rs.getString("password"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        return m;
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
