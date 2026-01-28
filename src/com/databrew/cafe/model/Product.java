package com.databrew.cafe.model;

import java.util.UUID;

public class Product {

    private String id;
    private String name;
    private double price;
    private int stockQuantity;
    private boolean available;

    public Product(String name, double price, int stockQuantity) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = stockQuantity > 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void reduceStock(int amount) {
        if (amount <= stockQuantity) {
            stockQuantity -= amount;
        }
        available = stockQuantity > 0;
    }

    public boolean isAvailable() {
        return available;
    }
}
