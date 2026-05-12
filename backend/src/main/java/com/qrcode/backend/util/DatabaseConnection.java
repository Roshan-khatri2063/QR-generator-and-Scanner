package com.qrcode.backend.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages MySQL database connections for QRHub.
 *
 * <p>Reads credentials from {@code db.properties} on the classpath.
 * Call {@link #getConnection()} to obtain a connection and always
 * close it in a try-with-resources block.</p>
 *
 * <pre>{@code
 * try (Connection conn = DatabaseConnection.getConnection()) {
 *     // use conn
 * }
 * }</pre>
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String PROPS_FILE = "db.properties";

    private static String url;
    private static String username;
    private static String password;
    private static boolean initialized = false;

    // Private constructor — static utility class
    private DatabaseConnection() {}

    // ------------------------------------------------------------------
    // Initialisation
    // ------------------------------------------------------------------

    /**
     * Loads db.properties from the classpath (once).
     * Throws RuntimeException if the file is missing or a required key is absent.
     */
    private static synchronized void init() {
        if (initialized) return;

        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream(PROPS_FILE)) {

            if (input == null) {
                throw new RuntimeException(
                        "'" + PROPS_FILE + "' not found on classpath. " +
                                "Copy db.properties.example → db.properties and fill in your credentials."
                );
            }

            Properties props = new Properties();
            props.load(input);

            String driver = props.getProperty("db.driver");
            url      = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            if (QrUtils.isBlank(url) || QrUtils.isBlank(username) || QrUtils.isBlank(password)) {
                throw new RuntimeException(
                        "db.properties is missing one or more required keys: db.url, db.username, db.password"
                );
            }

            // Explicitly load the JDBC driver (required for some servlet containers)
            Class.forName(driver);

            initialized = true;
            LOGGER.info("DatabaseConnection initialised — URL: " + url);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + PROPS_FILE, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath", e);
        }
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Returns a new {@link Connection} from the driver manager.
     * The caller is responsible for closing it.
     *
     * @throws SQLException if a connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        init();
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Quick connectivity check — returns true if a test connection succeeds.
     * Useful for a health check endpoint.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean valid = conn.isValid(3);   // 3-second timeout
            LOGGER.info("DB connection test: " + (valid ? "OK" : "FAILED"));
            return valid;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DB connection test failed: " + e.getMessage(), e);
            return false;
        }
    }
}
