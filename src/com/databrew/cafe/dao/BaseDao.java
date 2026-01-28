package com.databrew.cafe.dao;

import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Base DAO class providing common database operations and resource management utilities.
 * 
 * This class ensures proper resource cleanup using try-with-resources pattern
 * and provides reusable methods for common database operations.
 * 
 * Benefits:
 * - Automatic resource cleanup (Connection, PreparedStatement, ResultSet)
 * - Consistent error handling
 * - Reduced code duplication
 * - Better logging and debugging
 * 
 * @author DataBrew Team
 */
public abstract class BaseDao {

    /**
     * Executes a SELECT query that returns a single object.
     * Automatically manages Connection, PreparedStatement, and ResultSet resources.
     * 
     * @param sql The SQL query to execute
     * @param mapper Function to map ResultSet to object of type T
     * @param params Parameters for the prepared statement
     * @param <T> The type of object to return
     * @return The mapped object, or null if no results found
     * @throws SQLException if database operation fails
     */
    protected <T> T queryForObject(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logError("queryForObject", sql, e);
            throw e;
        }
    }

    /**
     * Executes a SELECT query that returns a list of objects.
     * Automatically manages Connection, PreparedStatement, and ResultSet resources.
     * 
     * @param sql The SQL query to execute
     * @param mapper Function to map ResultSet to object of type T
     * @param params Parameters for the prepared statement
     * @param <T> The type of objects in the list
     * @return List of mapped objects (never null, empty list if no results)
     * @throws SQLException if database operation fails
     */
    protected <T> List<T> queryForList(String sql, ResultSetMapper<T> mapper, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            logError("queryForList", sql, e);
            throw e;
        }
    }

    /**
     * Executes an UPDATE, INSERT, or DELETE statement.
     * Automatically manages Connection and PreparedStatement resources.
     * 
     * @param sql The SQL statement to execute
     * @param params Parameters for the prepared statement
     * @return Number of rows affected
     * @throws SQLException if database operation fails
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            return ps.executeUpdate();
            
        } catch (SQLException e) {
            logError("executeUpdate", sql, e);
            throw e;
        }
    }

    /**
     * Executes an INSERT statement and returns the generated key.
     * Automatically manages Connection, PreparedStatement, and ResultSet resources.
     * 
     * @param sql The INSERT SQL statement
     * @param params Parameters for the prepared statement
     * @return The generated key (ID)
     * @throws SQLException if database operation fails or no key generated
     */
    protected long executeInsertAndGetKey(String sql, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            setParameters(ps, params);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Insert failed: no generated key returned");
            }
            
        } catch (SQLException e) {
            logError("executeInsertAndGetKey", sql, e);
            throw e;
        }
    }

    /**
     * Executes a query and returns a single value (e.g., COUNT, SUM, MAX).
     * 
     * @param sql The SQL query
     * @param columnName The name of the column to retrieve
     * @param params Parameters for the prepared statement
     * @return The value as Long (null if no result)
     * @throws SQLException if database operation fails
     */
    protected Long queryForLong(String sql, String columnName, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long value = rs.getLong(columnName);
                    return rs.wasNull() ? null : value;
                }
                return null;
            }
        } catch (SQLException e) {
            logError("queryForLong", sql, e);
            throw e;
        }
    }

    /**
     * Executes a query and returns a single integer value (e.g., COUNT).
     * 
     * @param sql The SQL query
     * @param columnName The name of the column to retrieve
     * @param params Parameters for the prepared statement
     * @return The value as Integer (0 if no result)
     * @throws SQLException if database operation fails
     */
    protected int queryForInt(String sql, String columnName, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(columnName);
                }
                return 0;
            }
        } catch (SQLException e) {
            logError("queryForInt", sql, e);
            throw e;
        }
    }

    /**
     * Executes a query and returns a single double value (e.g., SUM, AVG).
     * 
     * @param sql The SQL query
     * @param columnName The name of the column to retrieve
     * @param params Parameters for the prepared statement
     * @return The value as Double (0.0 if no result)
     * @throws SQLException if database operation fails
     */
    protected double queryForDouble(String sql, String columnName, Object... params) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            setParameters(ps, params);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(columnName);
                }
                return 0.0;
            }
        } catch (SQLException e) {
            logError("queryForDouble", sql, e);
            throw e;
        }
    }

    /**
     * Sets parameters for a PreparedStatement, handling null values appropriately.
     * 
     * @param ps The PreparedStatement
     * @param params The parameters to set
     * @throws SQLException if parameter setting fails
     */
    protected void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int paramIndex = i + 1;
            
            if (param == null) {
                ps.setNull(paramIndex, Types.NULL);
            } else if (param instanceof String) {
                ps.setString(paramIndex, (String) param);
            } else if (param instanceof Integer) {
                ps.setInt(paramIndex, (Integer) param);
            } else if (param instanceof Long) {
                ps.setLong(paramIndex, (Long) param);
            } else if (param instanceof Double) {
                ps.setDouble(paramIndex, (Double) param);
            } else if (param instanceof Boolean) {
                ps.setBoolean(paramIndex, (Boolean) param);
            } else if (param instanceof java.sql.Date) {
                ps.setDate(paramIndex, (java.sql.Date) param);
            } else if (param instanceof java.sql.Timestamp) {
                ps.setTimestamp(paramIndex, (java.sql.Timestamp) param);
            } else {
                // Fallback to setObject for other types
                ps.setObject(paramIndex, param);
            }
        }
    }

    /**
     * Sets a nullable Long parameter in a PreparedStatement.
     * 
     * @param ps The PreparedStatement
     * @param index The parameter index (1-based)
     * @param value The value (can be null)
     * @throws SQLException if setting fails
     */
    protected void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }

    /**
     * Sets a nullable Integer parameter in a PreparedStatement.
     * 
     * @param ps The PreparedStatement
     * @param index The parameter index (1-based)
     * @param value The value (can be null)
     * @throws SQLException if setting fails
     */
    protected void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    /**
     * Sets a nullable String parameter in a PreparedStatement.
     * 
     * @param ps The PreparedStatement
     * @param index The parameter index (1-based)
     * @param value The value (can be null)
     * @throws SQLException if setting fails
     */
    protected void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }

    /**
     * Logs an error with SQL context for debugging.
     * 
     * @param operation The operation being performed
     * @param sql The SQL statement
     * @param e The exception that occurred
     */
    protected void logError(String operation, String sql, SQLException e) {
        System.err.println("═══════════════════════════════════════════════════════");
        System.err.println("DATABASE ERROR in " + this.getClass().getSimpleName());
        System.err.println("Operation: " + operation);
        System.err.println("SQL: " + sql);
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Message: " + e.getMessage());
        System.err.println("═══════════════════════════════════════════════════════");
    }

    /**
     * Functional interface for mapping ResultSet rows to objects.
     * 
     * @param <T> The type of object to create
     */
    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
