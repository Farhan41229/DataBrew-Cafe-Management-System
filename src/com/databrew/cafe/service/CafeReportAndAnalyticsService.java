package com.databrew.cafe.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeReportAndAnalyticsService
 * -----------------------------
 * Provides reporting, analytics, and statistics
 * for orders, products, users, and revenue.
 *
 * This class is intentionally large and verbose
 * to simulate a real reporting service.
 */
public class CafeReportAndAnalyticsService {

    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    /* ==========================
       DATA REGISTRATION
       ========================== */

    public void registerUser(String id, String name, String role) {
        users.put(id, new User(id, name, role, LocalDateTime.now()));
    }

    public void registerProduct(String id, String name, double price) {
        products.put(id, new Product(id, name, price));
    }

    public void registerOrder(Order order) {
        orders.put(order.id, order);
    }

    /* ==========================
       USER ANALYTICS
       ========================== */

    public int getTotalUsers() {
        return users.size();
    }

    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> map = new HashMap<>();
        for (User user : users.values()) {
            map.put(user.role, map.getOrDefault(user.role, 0) + 1);
        }
        return map;
    }

    public List<User> getUsersRegisteredAfter(LocalDateTime date) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.createdAt.isAfter(date)) {
                result.add(user);
            }
        }
        return result;
    }

    /* ==========================
       PRODUCT ANALYTICS
       ========================== */

    public int getTotalProducts() {
        return products.size();
    }

    public double getAverageProductPrice() {
        if (products.isEmpty()) return 0;
        double sum = 0;
        for (Product p : products.values()) {
            sum += p.price;
        }
        return sum / products.size();
    }

    public Product getMostExpensiveProduct() {
        Product max = null;
        for (Product p : products.values()) {
            if (max == null || p.price > max.price) {
                max = p;
            }
        }
        return max;
    }

    /* ==========================
       ORDER ANALYTICS
       ========================== */

    public int getTotalOrders() {
        return orders.size();
    }

    public double getTotalRevenue() {
        double sum = 0;
        for (Order order : orders.values()) {
            sum += order.totalAmount;
        }
        return sum;
    }

    public double getAverageOrderValue() {
        if (orders.isEmpty()) return 0;
        return getTotalRevenue() / orders.size();
    }

    public List<Order> getOrdersOnDate(LocalDate date) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.createdAt.toLocalDate().equals(date)) {
                result.add(order);
            }
        }
        return result;
    }

    /* ==========================
       TREND ANALYTICS
       ========================== */

    public Map<LocalDate, Double> getDailyRevenue() {
        Map<LocalDate, Double> map = new TreeMap<>();
        for (Order order : orders.values()) {
            LocalDate date = order.createdAt.toLocalDate();
            map.put(date, map.getOrDefault(date, 0.0) + order.totalAmount);
        }
        return map;
    }

    public Map<String, Integer> getTopSellingProducts() {
        Map<String, Integer> countMap = new HashMap<>();
        for (Order order : orders.values()) {
            for (OrderItem item : order.items) {
                countMap.put(
                        item.productName,
                        countMap.getOrDefault(item.productName, 0) + item.quantity
                );
            }
        }
        return sortDescending(countMap);
    }

    private Map<String, Integer> sortDescending(Map<String, Integer> input) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(input.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());

        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /* ==========================
       EXPORT UTILITIES
       ========================== */

    public String exportRevenueReport() {
        StringBuilder builder = new StringBuilder();
        builder.append("DATE,REVENUE\n");

        Map<LocalDate, Double> revenue = getDailyRevenue();
        for (Map.Entry<LocalDate, Double> entry : revenue.entrySet()) {
            builder.append(entry.getKey())
                   .append(",")
                   .append(entry.getValue())
                   .append("\n");
        }
        return builder.toString();
    }

    public String exportUserSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("ROLE,COUNT\n");

        Map<String, Integer> roleCount = getUserCountByRole();
        for (String role : roleCount.keySet()) {
            builder.append(role)
                   .append(",")
                   .append(roleCount.get(role))
                   .append("\n");
        }
        return builder.toString();
    }

    /* ==========================
       INNER DATA MODELS
       ========================== */

    static class User {
        String id;
        String name;
        String role;
        LocalDateTime createdAt;

        User(String id, String name, String role, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.createdAt = createdAt;
        }
    }

    static class Product {
        String id;
        String name;
        double price;

        Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    static class Order {
        String id;
        List<OrderItem> items = new ArrayList<>();
        double totalAmount;
        LocalDateTime createdAt;

        Order(String id, double totalAmount, LocalDateTime createdAt) {
            this.id = id;
            this.totalAmount = totalAmount;
            this.createdAt = createdAt;
        }
    }

    static class OrderItem {
        String productName;
        int quantity;

        OrderItem(String productName, int quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }
    }
}
