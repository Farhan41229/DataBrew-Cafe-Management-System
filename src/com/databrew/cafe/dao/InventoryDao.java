package com.databrew.cafe.dao;

import com.databrew.cafe.model.InventoryItem;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryDao {

    public List<InventoryItem> listAll() throws SQLException {
        String sql = "SELECT id, ingredient_id, quantity, last_updated FROM inventory";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<InventoryItem> items = new ArrayList<>();
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.setId(rs.getLong("id"));
                item.setIngredientId(rs.getLong("ingredient_id"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                items.add(item);
            }
            return items;
        }
    }

    public List<InventoryItem> findLowStock() throws SQLException {
        String sql = "SELECT inv.id, inv.ingredient_id, inv.quantity, inv.last_updated "
                + "FROM inventory inv JOIN ingredients ing ON ing.id = inv.ingredient_id "
                + "WHERE inv.quantity < ing.min_threshold";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<InventoryItem> items = new ArrayList<>();
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.setId(rs.getLong("id"));
                item.setIngredientId(rs.getLong("ingredient_id"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                items.add(item);
            }
            return items;
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
}
