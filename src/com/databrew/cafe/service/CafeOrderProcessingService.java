package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeOrderProcessingService
 * --------------------------
 * Handles order lifecycle: creation, item management,
 * validation, cancellation, and order state tracking.
 *
 * Designed as a verbose, stateful service.
 */
public class CafeOrderProcessingService {

    private final Map<String, Order> orders = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       ORDER CREATION
       ========================== */

    public String createOrder(String userId) {
        validateString(userId, "User ID");

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, userId, LocalDateTime.now());

        orders.put(orderId, order);
        log("Order created: " + orderId + " user=" + userId);

        return orderId;
    }

    /* ==========================
       ITEM MANAGEMENT
       ========================== */

    public void addItem(String orderId,
                        String productId,
                        String productName,
                        double price,
                        int quantity) {

        validateString(productId, "Product ID");
        validateString(productName, "Product name");
        validatePositive(price, "Price");
        validatePositive(quantity, "Quantity");

        Order order = getOrderOrThrow(orderId);
        ensureOrderOpen(order);

        OrderItem item = new OrderItem(
                productId,
                productName,
                price,
                quantity
        );

        order.items.add(item);
        log("Added item to order " + orderId + ": " + productName);
    }

    public void removeItem(String orderId, String productId) {
        Order order = getOrderOrThrow(orderId);
        ensureOrderOpen(order);

        Iterator<OrderItem> iterator = order.items.iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            OrderItem item = iterator.next();
            if (item.productId.equals(productId)) {
                iterator.remove();
                removed = true;
                log("Removed item from order " + orderId + ": " + item.productName);
                break;
            }
        }

        if (!removed) {
            throw new NoSuchElementException("Item not found in order");
        }
    }

    public void updateItemQuantity(String orderId,
                                   String productId,
                                   int newQuantity) {

        validatePositive(newQuantity, "New quantity");
        Order order = getOrderOrThrow(orderId);
        ensureOrderOpen(order);

        for (OrderItem item : order.items) {
            if (item.productId.equals(productId)) {
                item.quantity = newQuantity;
                log("Updated quantity for " + item.productName +
                        " in order " + orderId);
                return;
            }
        }

        throw new NoSuchElementException("Item not found in order");
    }

    /* ==========================
       ORDER FINALIZATION
       ========================== */

    public double finalizeOrder(String orderId) {
        Order order = getOrderOrThrow(orderId);
        ensureOrderOpen(order);

        if (order.items.isEmpty()) {
            throw new IllegalStateException("Cannot finalize empty order");
        }

        double total = calculateTotal(order);
        order.totalAmount = total;
        order.closed = true;
        order.closedAt = LocalDateTime.now();

        log("Order finalized: " + orderId + " total=" + total);
        return total;
    }

    public void cancelOrder(String orderId) {
        Order order = getOrderOrThrow(orderId);

        if (order.closed) {
            throw new IllegalStateException("Cannot cancel closed order");
        }

        order.cancelled = true;
        order.cancelledAt = LocalDateTime.now();

        log("Order cancelled: " + orderId);
    }

    /* ==========================
       ORDER QUERIES
       ========================== */

    public Order getOrder(String orderId) {
        return getOrderOrThrow(orderId);
    }

    public List<Order> listAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> listOpenOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (!order.closed && !order.cancelled) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> listClosedOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.closed) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> listOrdersForUser(String userId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.userId.equals(userId)) {
                result.add(order);
            }
        }
        return result;
    }

    /* ==========================
       CALCULATIONS
       ========================== */

    private double calculateTotal(Order order) {
        double sum = 0;
        for (OrderItem item : order.items) {
            sum += item.price * item.quantity;
        }
        return sum;
    }

    public int getTotalItemCount(String orderId) {
        Order order = getOrderOrThrow(orderId);
        int count = 0;
        for (OrderItem item : order.items) {
            count += item.quantity;
        }
        return count;
    }

    /* ==========================
       AUDIT LOG
       ========================== */

    private void log(String message) {
        auditLog.add(LocalDateTime.now() + " :: " + message);
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }

    /* ==========================
       VALIDATION HELPERS
       ========================== */

    private void validateString(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
    }

    private void validatePositive(double value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }

    private void validatePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }

    private void ensureOrderOpen(Order order) {
        if (order.closed) {
            throw new IllegalStateException("Order already closed");
        }
        if (order.cancelled) {
            throw new IllegalStateException("Order has been cancelled");
        }
    }

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private Order getOrderOrThrow(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new NoSuchElementException("Order not found: " + orderId);
        }
        return order;
    }

    /* ==========================
       INNER MODELS
       ========================== */

    public static class Order {
        public String id;
        public String userId;
        public List<OrderItem> items = new ArrayList<>();
        public boolean closed = false;
        public boolean cancelled = false;
        public double totalAmount = 0;
        public LocalDateTime createdAt;
        public LocalDateTime closedAt;
        public LocalDateTime cancelledAt;

        public Order(String id, String userId, LocalDateTime createdAt) {
            this.id = id;
            this.userId = userId;
            this.createdAt = createdAt;
        }
    }

    public static class OrderItem {
        public String productId;
        public String productName;
        public double price;
        public int quantity;

        public OrderItem(String productId,
                         String productName,
                         double price,
                         int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
