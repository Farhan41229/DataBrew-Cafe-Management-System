package com.databrew.cafe.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeFilePersistenceService
 * --------------------------
 * Handles file-based persistence for users, products,
 * and orders using plain text serialization.
 *
 * This class simulates a legacy persistence layer.
 */
public class CafeFilePersistenceService {

    private final File userFile;
    private final File productFile;
    private final File orderFile;

    public CafeFilePersistenceService(String baseDirectory) {
        this.userFile = new File(baseDirectory, "users.txt");
        this.productFile = new File(baseDirectory, "products.txt");
        this.orderFile = new File(baseDirectory, "orders.txt");
        ensureFilesExist();
    }

    /* ==========================
       FILE INITIALIZATION
       ========================== */

    private void ensureFilesExist() {
        try {
            if (!userFile.exists()) userFile.createNewFile();
            if (!productFile.exists()) productFile.createNewFile();
            if (!orderFile.exists()) orderFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage files", e);
        }
    }

    /* ==========================
       USER PERSISTENCE
       ========================== */

    public void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write(serializeUser(user));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = deserializeUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users", e);
        }
        return users;
    }

    private String serializeUser(User user) {
        return String.join("|",
                user.id,
                user.username,
                user.email,
                user.role,
                String.valueOf(user.active),
                user.createdAt.toString()
        );
    }

    private User deserializeUser(String line) {
        try {
            String[] parts = line.split("\\|");
            return new User(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    Boolean.parseBoolean(parts[4]),
                    LocalDateTime.parse(parts[5])
            );
        } catch (Exception e) {
            return null;
        }
    }

    /* ==========================
       PRODUCT PERSISTENCE
       ========================== */

    public void saveProduct(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(productFile, true))) {
            writer.write(serializeProduct(product));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(productFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = deserializeProduct(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products", e);
        }
        return products;
    }

    private String serializeProduct(Product product) {
        return String.join("|",
                product.id,
                product.name,
                String.valueOf(product.price),
                String.valueOf(product.stock)
        );
    }

    private Product deserializeProduct(String line) {
        try {
            String[] parts = line.split("\\|");
            return new Product(
                    parts[0],
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (Exception e) {
            return null;
        }
    }

    /* ==========================
       ORDER PERSISTENCE
       ========================== */

    public void saveOrder(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile, true))) {
            writer.write(serializeOrder(order));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    public List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order order = deserializeOrder(line);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load orders", e);
        }
        return orders;
    }

    private String serializeOrder(Order order) {
        StringBuilder builder = new StringBuilder();
        builder.append(order.id).append("|")
               .append(order.userId).append("|")
               .append(order.createdAt).append("|")
               .append(order.totalAmount).append("|");

        for (int i = 0; i < order.items.size(); i++) {
            OrderItem item = order.items.get(i);
            builder.append(item.productName)
                   .append(",")
                   .append(item.quantity);
            if (i < order.items.size() - 1) {
                builder.append(";");
            }
        }
        return builder.toString();
    }

    private Order deserializeOrder(String line) {
        try {
            String[] parts = line.split("\\|");
            Order order = new Order(
                    parts[0],
                    parts[1],
                    LocalDateTime.parse(parts[2]),
                    Double.parseDouble(parts[3])
            );

            if (parts.length > 4 && !parts[4].isEmpty()) {
                String[] items = parts[4].split(";");
                for (String itemData : items) {
                    String[] fields = itemData.split(",");
                    order.items.add(
                            new OrderItem(fields[0], Integer.parseInt(fields[1]))
                    );
                }
            }
            return order;
        } catch (Exception e) {
            return null;
        }
    }

    /* ==========================
       INNER DATA MODELS
       ========================== */

    static class User {
        String id;
        String username;
        String email;
        String role;
        boolean active;
        LocalDateTime createdAt;

        User(String id, String username, String email,
             String role, boolean active, LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.active = active;
            this.createdAt = createdAt;
        }
    }

    static class Product {
        String id;
        String name;
        double price;
        int stock;

        Product(String id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }

    static class Order {
        String id;
        String userId;
        LocalDateTime createdAt;
        double totalAmount;
        List<OrderItem> items = new ArrayList<>();

        Order(String id, String userId,
              LocalDateTime createdAt, double totalAmount) {
            this.id = id;
            this.userId = userId;
            this.createdAt = createdAt;
            this.totalAmount = totalAmount;
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
