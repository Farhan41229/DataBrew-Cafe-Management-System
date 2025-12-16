package com.databrew.cafe.dao;

import com.databrew.cafe.model.Order;
import com.databrew.cafe.model.OrderItem;
import com.databrew.cafe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDao {

    public long insertOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_name, customer_type, tax_id, discount_id, subtotal, tax_amount, discount_amount, total) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getCustomerName());
            ps.setString(2, order.getCustomerType());
            if (order.getTaxId() == null) {
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(3, order.getTaxId());
            }
            if (order.getDiscountId() == null) {
                ps.setNull(4, java.sql.Types.BIGINT);
            } else {
                ps.setLong(4, order.getDiscountId());
            }
            ps.setDouble(5, order.getSubtotal());
            ps.setDouble(6, order.getTaxAmount());
            ps.setDouble(7, order.getDiscountAmount());
            ps.setDouble(8, order.getTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new SQLException("Order insert failed: no ID returned");
        }
    }

    public void insertItems(Connection conn, long orderId, List<OrderItem> items) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price, line_total) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderItem item : items) {
                ps.setLong(1, orderId);
                ps.setLong(2, item.getMenuItemId());
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getUnitPrice());
                ps.setDouble(5, item.getLineTotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public double getTodayRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total),0) AS revenue FROM orders WHERE DATE(created_at)=CURDATE() AND status <> 'CANCELLED'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
            return 0d;
        }
    }

    public int getActiveOrderCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM orders WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return 0;
        }
    }
}
