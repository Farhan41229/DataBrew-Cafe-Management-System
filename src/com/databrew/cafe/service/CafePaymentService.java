package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafePaymentService
 * ------------------
 * Handles payment processing, refunds, payment history,
 * validation, and basic financial reporting.
 *
 * Designed as a verbose service class.
 */
public class CafePaymentService {

    private final Map<String, Payment> payments = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       PAYMENT CREATION
       ========================== */

    public String createPayment(String orderId,
                                double amount,
                                PaymentMethod method) {

        validateString(orderId, "Order ID");
        validatePositive(amount, "Amount");

        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(
                paymentId,
                orderId,
                amount,
                method,
                PaymentStatus.PENDING,
                LocalDateTime.now()
        );

        payments.put(paymentId, payment);
        log("Payment created: " + paymentId + " order=" + orderId);

        return paymentId;
    }

    /* ==========================
       PAYMENT PROCESSING
       ========================== */

    public void markPaymentSuccessful(String paymentId) {
        Payment payment = getPaymentOrThrow(paymentId);

        if (payment.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment not in pending state");
        }

        payment.status = PaymentStatus.COMPLETED;
        payment.completedAt = LocalDateTime.now();

        log("Payment completed: " + paymentId);
    }

    public void markPaymentFailed(String paymentId, String reason) {
        Payment payment = getPaymentOrThrow(paymentId);

        if (payment.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment not in pending state");
        }

        payment.status = PaymentStatus.FAILED;
        payment.failureReason = reason;
        payment.completedAt = LocalDateTime.now();

        log("Payment failed: " + paymentId + " reason=" + reason);
    }

    /* ==========================
       REFUNDS
       ========================== */

    public void refundPayment(String paymentId, String reason) {
        Payment payment = getPaymentOrThrow(paymentId);

        if (payment.status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        payment.status = PaymentStatus.REFUNDED;
        payment.refundReason = reason;
        payment.refundedAt = LocalDateTime.now();

        log("Payment refunded: " + paymentId + " reason=" + reason);
    }

    /* ==========================
       PAYMENT QUERIES
       ========================== */

    public Payment getPayment(String paymentId) {
        return getPaymentOrThrow(paymentId);
    }

    public List<Payment> listAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public List<Payment> listPaymentsForOrder(String orderId) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.orderId.equals(orderId)) {
                result.add(payment);
            }
        }
        return result;
    }

    public List<Payment> listCompletedPayments() {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.status == PaymentStatus.COMPLETED) {
                result.add(payment);
            }
        }
        return result;
    }

    public List<Payment> listRefundedPayments() {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.status == PaymentStatus.REFUNDED) {
                result.add(payment);
            }
        }
        return result;
    }

    /* ==========================
       FINANCIAL CALCULATIONS
       ========================== */

    public double getTotalRevenue() {
        double sum = 0;
        for (Payment payment : payments.values()) {
            if (payment.status == PaymentStatus.COMPLETED) {
                sum += payment.amount;
            }
        }
        return sum;
    }

    public double getTotalRefundedAmount() {
        double sum = 0;
        for (Payment payment : payments.values()) {
            if (payment.status == PaymentStatus.REFUNDED) {
                sum += payment.amount;
            }
        }
        return sum;
    }

    public double getNetRevenue() {
        return getTotalRevenue() - getTotalRefundedAmount();
    }

    public Map<PaymentMethod, Double> getRevenueByMethod() {
        Map<PaymentMethod, Double> map = new EnumMap<>(PaymentMethod.class);
        for (PaymentMethod method : PaymentMethod.values()) {
            map.put(method, 0.0);
        }

        for (Payment payment : payments.values()) {
            if (payment.status == PaymentStatus.COMPLETED) {
                map.put(
                        payment.method,
                        map.get(payment.method) + payment.amount
                );
            }
        }
        return map;
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

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private Payment getPaymentOrThrow(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new NoSuchElementException("Payment not found: " + paymentId);
        }
        return payment;
    }

    /* ==========================
       INNER MODELS
       ========================== */

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public enum PaymentMethod {
        CASH,
        CARD,
        MOBILE_WALLET,
        ONLINE
    }

    public static class Payment {
        public String id;
        public String orderId;
        public double amount;
        public PaymentMethod method;
        public PaymentStatus status;
        public String failureReason;
        public String refundReason;
        public LocalDateTime createdAt;
        public LocalDateTime completedAt;
        public LocalDateTime refundedAt;

        public Payment(String id,
                       String orderId,
                       double amount,
                       PaymentMethod method,
                       PaymentStatus status,
                       LocalDateTime createdAt) {
            this.id = id;
            this.orderId = orderId;
            this.amount = amount;
            this.method = method;
            this.status = status;
            this.createdAt = createdAt;
        }
    }
}
