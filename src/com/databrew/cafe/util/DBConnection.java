package com.databrew.cafe.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple JDBC connection utility with externalized configuration.
 * For production use a pool such as HikariCP.
 */
public final class DBConnection {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find config.properties file in resources folder");
            }
            props.load(input);
            
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.username");
            PASSWORD = props.getProperty("db.password");
            
            if (URL == null || USER == null || PASSWORD == null) {
                throw new IllegalStateException("Database configuration incomplete. Check config.properties file.");
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL driver not found", e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closePool() {
        // No pool in use; placeholder for future pooling support.
    }
}
