package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeCustomerService
 * -------------------
 * Handles customer registration, profile updates,
 * loyalty points, visit tracking, and customer analytics.
 */
public class CafeCustomerService {

    private final Map<String, Customer> customers = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       CUSTOMER REGISTRATION
       ========================== */

    public String registerCustomer(String name, String email) {
        validateString(name, "Name");
        validateString(email, "Email");

        String customerId = UUID.randomUUID().toString();
        Customer customer = new Customer(
                customerId,
                name,
                email,
                0,
                0,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        customers.put(customerId, customer);
        log("Registered customer: " + name);

        return customerId;
    }

    /* ==========================
       PROFILE MANAGEMENT
       ========================== */

    public void updateName(String customerId, String newName) {
        validateString(newName, "New name");
        Customer customer = getCustomerOrThrow(customerId);

        customer.name = newName;
        customer.lastUpdated = LocalDateTime.now();

        log("Updated customer name: " + customerId);
    }

    public void updateEmail(String customerId, String newEmail) {
        validateString(newEmail, "New email");
        Customer customer = getCustomerOrThrow(customerId);

        customer.email = newEmail;
        customer.lastUpdated = LocalDateTime.now();

        log("Updated customer email: " + customerId);
    }

    /* ==========================
       LOYALTY SYSTEM
       ========================== */

    public void addLoyaltyPoints(String customerId, int points) {
        validatePositive(points, "Points");
        Customer customer = getCustomerOrThrow(customerId);

        customer.loyaltyPoints += points;
        customer.lastUpdated = LocalDateTime.now();

        log("Added " + points + " points to customer " + customerId);
    }

    public boolean redeemPoints(String customerId, int points) {
        validatePositive(points, "Points");
        Customer customer = getCustomerOrThrow(customerId);

        if (customer.loyaltyPoints < points) {
            return false;
        }

        customer.loyaltyPoints -= points;
        customer.lastUpdated = LocalDateTime.now();

        log("Redeemed " + points + " points from customer " + customerId);
        return true;
    }

    public int getLoyaltyPoints(String customerId) {
        return getCustomerOrThrow(customerId).loyaltyPoints;
    }

    /* ==========================
       VISIT TRACKING
       ========================== */

    public void recordVisit(String customerId) {
        Customer customer = getCustomerOrThrow(customerId);

        customer.visitCount++;
        customer.lastVisit = LocalDateTime.now();
        customer.lastUpdated = LocalDateTime.now();

        log("Recorded visit for customer " + customerId);
    }

    public int getVisitCount(String customerId) {
        return getCustomerOrThrow(customerId).visitCount;
    }

    /* ==========================
       CUSTOMER STATUS
       ========================== */

    public void deactivateCustomer(String customerId) {
        Customer customer = getCustomerOrThrow(customerId);

        customer.active = false;
        customer.lastUpdated = LocalDateTime.now();

        log("Deactivated customer " + customerId);
    }

    public void activateCustomer(String customerId) {
        Customer customer = getCustomerOrThrow(customerId);

        customer.active = true;
        customer.lastUpdated = LocalDateTime.now();

        log("Activated customer " + customerId);
    }

    public boolean isCustomerActive(String customerId) {
        return getCustomerOrThrow(customerId).active;
    }

    /* ==========================
       CUSTOMER QUERIES
       ========================== */

    public Customer getCustomer(String customerId) {
        return getCustomerOrThrow(customerId);
    }

    public List<Customer> listAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public List<Customer> listActiveCustomers() {
        List<Customer> result = new ArrayList<>();
        for (Customer customer : customers.values()) {
            if (customer.active) {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Customer> getTopCustomersByVisits(int limit) {
        List<Customer> list = new ArrayList<>(customers.values());
        list.sort((a, b) -> Integer.compare(b.visitCount, a.visitCount));

        if (limit > list.size()) {
            limit = list.size();
        }
        return list.subList(0, limit);
    }

    /* ==========================
       ANALYTICS
       ========================== */

    public int getTotalCustomerCount() {
        return customers.size();
    }

    public int getActiveCustomerCount() {
        int count = 0;
        for (Customer customer : customers.values()) {
            if (customer.active) {
                count++;
            }
        }
        return count;
    }

    public double getAverageVisitsPerCustomer() {
        if (customers.isEmpty()) return 0;

        int totalVisits = 0;
        for (Customer customer : customers.values()) {
            totalVisits += customer.visitCount;
        }
        return (double) totalVisits / customers.size();
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
       VALIDATION
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

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private Customer getCustomerOrThrow(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new NoSuchElementException("Customer not found: " + customerId);
        }
        return customer;
    }

    /* ==========================
       INNER MODEL
       ========================== */

    public static class Customer {
        public String id;
        public String name;
        public String email;
        public int loyaltyPoints;
        public int visitCount;
        public boolean active;
        public LocalDateTime lastVisit;
        public LocalDateTime createdAt;
        public LocalDateTime lastUpdated;

        public Customer(String id,
                        String name,
                        String email,
                        int loyaltyPoints,
                        int visitCount,
                        boolean active,
                        LocalDateTime createdAt,
                        LocalDateTime lastUpdated) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.loyaltyPoints = loyaltyPoints;
            this.visitCount = visitCount;
            this.active = active;
            this.createdAt = createdAt;
            this.lastUpdated = lastUpdated;
        }
    }
}
