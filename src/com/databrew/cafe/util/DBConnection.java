package com.databrew.cafe.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection utility with HikariCP connection pooling.
 * Provides high-performance, production-ready database connection management.
 */
public final class DBConnection {
    private static final HikariDataSource dataSource;
    private static volatile boolean isInitialized = false;

    static {
        try {
            Properties props = loadConfiguration();
            HikariConfig config = createHikariConfig(props);
            dataSource = new HikariDataSource(config);
            isInitialized = true;
            
            System.out.println("✓ HikariCP Connection Pool initialized successfully");
            System.out.println("  - Pool Name: " + dataSource.getPoolName());
            System.out.println("  - Max Pool Size: " + config.getMaximumPoolSize());
            System.out.println("  - Min Idle: " + config.getMinimumIdle());
            
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize database connection pool", e);
        }
    }

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Loads database configuration from config.properties file
     */
    private static Properties loadConfiguration() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Unable to find config.properties file in resources folder");
            }
            props.load(input);
            
            // Validate required properties
            String[] required = {"db.url", "db.username", "db.password"};
            for (String key : required) {
                if (props.getProperty(key) == null) {
                    throw new IllegalStateException("Missing required property: " + key);
                }
            }
        }
        return props;
    }

    /**
     * Creates and configures HikariCP configuration
     */
    private static HikariConfig createHikariConfig(Properties props) {
        HikariConfig config = new HikariConfig();
        
        // Basic connection settings
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Pool name for monitoring
        config.setPoolName("DataBrewCafePool");
        
        // Pool size configuration
        config.setMaximumPoolSize(getIntProperty(props, "db.pool.maxPoolSize", 10));
        config.setMinimumIdle(getIntProperty(props, "db.pool.minIdle", 5));
        
        // Timeout configuration (in milliseconds)
        config.setConnectionTimeout(getLongProperty(props, "db.pool.connectionTimeout", 30000));
        config.setIdleTimeout(getLongProperty(props, "db.pool.idleTimeout", 600000));
        config.setMaxLifetime(getLongProperty(props, "db.pool.maxLifetime", 1800000));
        
        // Leak detection (0 to disable)
        long leakThreshold = getLongProperty(props, "db.pool.leakDetectionThreshold", 60000);
        if (leakThreshold > 0) {
            config.setLeakDetectionThreshold(leakThreshold);
        }
        
        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        // Connection test query
        config.setConnectionTestQuery("SELECT 1");
        
        return config;
    }

    /**
     * Gets a connection from the pool
     * @return A database connection from the pool
     * @throws SQLException if unable to get a connection
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized || dataSource == null) {
            throw new SQLException("Database connection pool is not initialized");
        }
        
        if (dataSource.isClosed()) {
            throw new SQLException("Database connection pool has been closed");
        }
        
        return dataSource.getConnection();
    }

    /**
     * Gets pool statistics for monitoring
     */
    public static String getPoolStats() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool is not available";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }

    /**
     * Closes the connection pool and releases all resources
     * Should be called on application shutdown
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            System.out.println("Closing HikariCP connection pool...");
            System.out.println(getPoolStats());
            dataSource.close();
            isInitialized = false;
            System.out.println("✓ Connection pool closed successfully");
        }
    }

    /**
     * Helper method to get integer property with default value
     */
    private static int getIntProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer value for " + key + ", using default: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Helper method to get long property with default value
     */
    private static long getLongProperty(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid long value for " + key + ", using default: " + defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Checks if the connection pool is healthy and available
     */
    public static boolean isPoolHealthy() {
        return isInitialized && dataSource != null && !dataSource.isClosed();
    }
}
