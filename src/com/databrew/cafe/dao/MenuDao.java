package com.databrew.cafe.dao;

import com.databrew.cafe.model.MenuItem;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDao {

    public List<MenuItem> findActive() throws SQLException {
        String sql = "SELECT id, category_id, name, description, price, is_active FROM menu_items WHERE is_active=1";
        return fetch(sql);
    }

    public List<MenuItem> findAll() throws SQLException {
        String sql = "SELECT id, category_id, name, description, price, is_active FROM menu_items";
        return fetch(sql);
    }

    private List<MenuItem> fetch(String sql) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<MenuItem> list = new ArrayList<>();
            while (rs.next()) {
                MenuItem m = new MenuItem();
                m.setId(rs.getLong("id"));
                m.setCategoryId(rs.getLong("category_id"));
                m.setName(rs.getString("name"));
                m.setDescription(rs.getString("description"));
                m.setPrice(rs.getDouble("price"));
                m.setActive(rs.getBoolean("is_active"));
                list.add(m);
            }
            return list;
        }
    }

    public void insert(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu_items (category_id, name, description, price, is_active) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getCategoryId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setDouble(4, item.getPrice());
            ps.setBoolean(5, item.isActive());
            ps.executeUpdate();
        }
    }

    public void update(MenuItem item) throws SQLException {
        String sql = "UPDATE menu_items SET category_id=?, name=?, description=?, price=?, is_active=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getCategoryId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setDouble(4, item.getPrice());
            ps.setBoolean(5, item.isActive());
            ps.setLong(6, item.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
