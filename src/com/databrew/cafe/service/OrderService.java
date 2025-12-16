package com.databrew.cafe.service;

import com.databrew.cafe.dao.InvoiceDao;
import com.databrew.cafe.dao.OrderDao;
import com.databrew.cafe.dao.PaymentDao;
import com.databrew.cafe.model.Invoice;
import com.databrew.cafe.model.Order;
import com.databrew.cafe.model.OrderItem;
import com.databrew.cafe.model.Payment;
import com.databrew.cafe.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderDao orderDao = new OrderDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();

    public long createOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long orderId = orderDao.insertOrder(conn, order);
                orderDao.insertItems(conn, orderId, items);
                try (CallableStatement cs = conn.prepareCall("{call create_order_procedure(?,?,?,?)}")) {
                    cs.setString(1, order.getCustomerName());
                    cs.setString(2, order.getCustomerType());
                    if (order.getTaxId() == null) {
                        cs.setNull(3, java.sql.Types.BIGINT);
                    } else {
                        cs.setLong(3, order.getTaxId());
                    }
                    if (order.getDiscountId() == null) {
                        cs.setNull(4, java.sql.Types.BIGINT);
                    } else {
                        cs.setLong(4, order.getDiscountId());
                    }
                    cs.execute();
                }
                conn.commit();
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void recordPaymentAndInvoice(long orderId, double amount, String method) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Payment p = new Payment();
                p.setOrderId(orderId);
                p.setAmount(amount);
                p.setMethod(method);
                long paymentId = paymentDao.insert(conn, p);

                try (CallableStatement cs = conn.prepareCall("{call generate_invoice_procedure(?,?)}")) {
                    cs.setLong(1, orderId);
                    cs.setLong(2, paymentId);
                    cs.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
