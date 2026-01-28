package com.databrew.cafe.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order {

    private String id;
    private User customer;
    private List<OrderItem> items;
    private LocalDateTime createdAt;

    public Order(User customer) {
        this.id = UUID.randomUUID().toString();
        this.customer = customer;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(Product product, int quantity) {
        items.add(new OrderItem(product, quantity));
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
