package com.databrew.cafe.dao;

import com.databrew.cafe.model.Invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvoiceDao {

    public void insert(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (order_id, invoice_number, payment_id, total) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoice.getOrderId());
            ps.setString(2, invoice.getInvoiceNumber());
            if (invoice.getPaymentId() == null) {
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(3, invoice.getPaymentId());
            }
            ps.setDouble(4, invoice.getTotal());
            ps.executeUpdate();
        }
    }
}
