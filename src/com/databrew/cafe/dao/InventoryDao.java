package com.databrew.cafe.dao;

import com.databrew.cafe.model.InventoryItem;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {

    private static final String SELECT_ALL = "SELECT inv.id, inv.ingredient_id, ing.name AS ingredient_name, ing.unit, "
            + "inv.quantity, ing.min_threshold, inv.last_updated "
            + "FROM inventory inv JOIN ingredients ing ON ing.id = inv.ingredient_id "
            + "ORDER BY ing.name";

    public List<InventoryItem> listAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
                ResultSet rs = ps.executeQuery()) {
            List<InventoryItem> items = new ArrayList<>();
            while (rs.next()) {
                items.add(map(rs));
            }
            return items;
        }
    }

    public List<InventoryItem> findLowStock() throws SQLException {
        String sql = "SELECT inv.id, inv.ingredient_id, ing.name AS ingredient_name, ing.unit, "
                + "inv.quantity, ing.min_threshold, inv.last_updated "
                + "FROM inventory inv JOIN ingredients ing ON ing.id = inv.ingredient_id "
                + "WHERE inv.quantity < ing.min_threshold ORDER BY ing.name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<InventoryItem> items = new ArrayList<>();
            while (rs.next()) {
                items.add(map(rs));
            }
            return items;
        }
    }

    /** Insert a new ingredient + its inventory row in one go. */
    public long insertIngredientWithStock(String name, String unit, double minThreshold, double quantity)
            throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert ingredient
                String ingSql = "INSERT INTO ingredients (name, unit, min_threshold) VALUES (?,?,?)";
                long ingredientId;
                try (PreparedStatement ps = conn.prepareStatement(ingSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, name);
                    ps.setString(2, unit);
                    ps.setDouble(3, minThreshold);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            ingredientId = rs.getLong(1);
                        else
                            throw new SQLException("Failed to get ingredient ID");
                    }
                }
                // Insert inventory row
                String invSql = "INSERT INTO inventory (ingredient_id, quantity) VALUES (?,?)";
                long invId;
                try (PreparedStatement ps = conn.prepareStatement(invSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, ingredientId);
                    ps.setDouble(2, quantity);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            invId = rs.getLong(1);
                        else
                            throw new SQLException("Failed to get inventory ID");
                    }
                }
                conn.commit();
                return invId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Update quantity, and ingredient name/unit/threshold together. */
    public void updateItem(long inventoryId, long ingredientId, String name, String unit, double minThreshold,
            double quantity) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String ingSql = "UPDATE ingredients SET name=?, unit=?, min_threshold=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(ingSql)) {
                    ps.setString(1, name);
                    ps.setString(2, unit);
                    ps.setDouble(3, minThreshold);
                    ps.setLong(4, ingredientId);
                    ps.executeUpdate();
                }
                String invSql = "UPDATE inventory SET quantity=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(invSql)) {
                    ps.setDouble(1, quantity);
                    ps.setLong(2, inventoryId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Delete inventory row and corresponding ingredient. */
    public void deleteItem(long inventoryId, long ingredientId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inventory WHERE id=?")) {
                    ps.setLong(1, inventoryId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ingredients WHERE id=?")) {
                    ps.setLong(1, ingredientId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void adjustStock(Connection conn, long ingredientId, double delta) throws SQLException {
        String sql = "UPDATE inventory SET quantity = quantity + ? WHERE ingredient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setLong(2, ingredientId);
            ps.executeUpdate();
        }
    }

    private InventoryItem map(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setId(rs.getLong("id"));
        item.setIngredientId(rs.getLong("ingredient_id"));
        item.setIngredientName(rs.getString("ingredient_name"));
        item.setUnit(rs.getString("unit"));
        item.setQuantity(rs.getDouble("quantity"));
        item.setMinThreshold(rs.getDouble("min_threshold"));
        item.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
        return item;
    }
}
