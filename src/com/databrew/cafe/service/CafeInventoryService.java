package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeInventoryService
 * --------------------
 * Handles inventory management, stock tracking,
 * low-stock alerts, restocking, and audit logging.
 *
 * This class is intentionally verbose and stateful
 * to simulate a real-world inventory service.
 */
public class CafeInventoryService {

    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       INVENTORY REGISTRATION
       ========================== */

    public void registerItem(String productId, String name, int initialStock) {
        validateString(productId, "Product ID");
        validateString(name, "Product name");
        validateNonNegative(initialStock, "Initial stock");

        if (inventory.containsKey(productId)) {
            throw new IllegalStateException("Item already exists: " + productId);
        }

        InventoryItem item = new InventoryItem(
                productId,
                name,
                initialStock,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        inventory.put(productId, item);
        log("Registered inventory item: " + name + " stock=" + initialStock);
    }

    /* ==========================
       STOCK OPERATIONS
       ========================== */

    public void increaseStock(String productId, int amount) {
        validatePositive(amount, "Increase amount");
        InventoryItem item = getItemOrThrow(productId);

        item.stock += amount;
        item.lastUpdated = LocalDateTime.now();

        log("Increased stock for " + item.name + " by " + amount);
    }

    public void decreaseStock(String productId, int amount) {
        validatePositive(amount, "Decrease amount");
        InventoryItem item = getItemOrThrow(productId);

        if (item.stock < amount) {
            throw new IllegalStateException(
                    "Insufficient stock for " + item.name +
                    " (available=" + item.stock + ")"
            );
        }

        item.stock -= amount;
        item.lastUpdated = LocalDateTime.now();

        log("Decreased stock for " + item.name + " by " + amount);
    }

    public void setStock(String productId, int newStock) {
        validateNonNegative(newStock, "New stock");
        InventoryItem item = getItemOrThrow(productId);

        item.stock = newStock;
        item.lastUpdated = LocalDateTime.now();

        log("Stock set for " + item.name + " to " + newStock);
    }

    /* ==========================
       STOCK QUERIES
       ========================== */

    public int getStock(String productId) {
        return getItemOrThrow(productId).stock;
    }

    public boolean isInStock(String productId) {
        return getStock(productId) > 0;
    }

    public List<InventoryItem> listAllItems() {
        return new ArrayList<>(inventory.values());
    }

    public List<InventoryItem> listAvailableItems() {
        List<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : inventory.values()) {
            if (item.stock > 0) {
                result.add(item);
            }
        }
        return result;
    }

    /* ==========================
       LOW STOCK MONITORING
       ========================== */

    public List<InventoryItem> getLowStockItems(int threshold) {
        validateNonNegative(threshold, "Threshold");

        List<InventoryItem> lowStock = new ArrayList<>();
        for (InventoryItem item : inventory.values()) {
            if (item.stock <= threshold) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    public boolean hasLowStockItems(int threshold) {
        return !getLowStockItems(threshold).isEmpty();
    }

    /* ==========================
       RESTOCKING
       ========================== */

    public void restockAllLowItems(int threshold, int restockAmount) {
        validateNonNegative(threshold, "Threshold");
        validatePositive(restockAmount, "Restock amount");

        for (InventoryItem item : inventory.values()) {
            if (item.stock <= threshold) {
                item.stock += restockAmount;
                item.lastUpdated = LocalDateTime.now();
                log("Auto-restocked " + item.name + " by " + restockAmount);
            }
        }
    }

    /* ==========================
       REPORTING
       ========================== */

    public int getTotalItemCount() {
        return inventory.size();
    }

    public int getTotalUnitsInStock() {
        int sum = 0;
        for (InventoryItem item : inventory.values()) {
            sum += item.stock;
        }
        return sum;
    }

    public Map<String, Integer> getStockSnapshot() {
        Map<String, Integer> snapshot = new LinkedHashMap<>();
        for (InventoryItem item : inventory.values()) {
            snapshot.put(item.name, item.stock);
        }
        return snapshot;
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

    public void clearAuditLog() {
        auditLog.clear();
    }

    /* ==========================
       VALIDATION HELPERS
       ========================== */

    private void validateString(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
    }

    private void validatePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }

    private void validateNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " cannot be negative");
        }
    }

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private InventoryItem getItemOrThrow(String productId) {
        InventoryItem item = inventory.get(productId);
        if (item == null) {
            throw new NoSuchElementException("Inventory item not found: " + productId);
        }
        return item;
    }

    /* ==========================
       INNER MODEL
       ========================== */

    public static class InventoryItem {
        public String productId;
        public String name;
        public int stock;
        public LocalDateTime createdAt;
        public LocalDateTime lastUpdated;

        public InventoryItem(String productId,
                             String name,
                             int stock,
                             LocalDateTime createdAt,
                             LocalDateTime lastUpdated) {
            this.productId = productId;
            this.name = name;
            this.stock = stock;
            this.createdAt = createdAt;
            this.lastUpdated = lastUpdated;
        }

        @Override
        public String toString() {
            return "InventoryItem{" +
                    "productId='" + productId + '\'' +
                    ", name='" + name + '\'' +
                    ", stock=" + stock +
                    ", createdAt=" + createdAt +
                    ", lastUpdated=" + lastUpdated +
                    '}';
        }
    }
}
