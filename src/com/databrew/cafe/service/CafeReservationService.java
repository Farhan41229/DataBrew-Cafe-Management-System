package com.databrew.cafe.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * CafeReservationService
 * ----------------------
 * Manages table reservations, availability checks,
 * cancellations, rescheduling, and reservation analytics.
 */
public class CafeReservationService {

    private final Map<String, Reservation> reservations = new HashMap<>();
    private final Map<Integer, Table> tables = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       TABLE MANAGEMENT
       ========================== */

    public void registerTable(int tableNumber, int capacity) {
        validatePositive(capacity, "Capacity");

        if (tables.containsKey(tableNumber)) {
            throw new IllegalStateException("Table already exists: " + tableNumber);
        }

        tables.put(tableNumber, new Table(tableNumber, capacity));
        log("Registered table " + tableNumber + " capacity=" + capacity);
    }

    public List<Table> listTables() {
        return new ArrayList<>(tables.values());
    }

    /* ==========================
       RESERVATION CREATION
       ========================== */

    public String createReservation(String customerId,
                                    int tableNumber,
                                    LocalDate date,
                                    LocalTime time,
                                    int guests) {

        validateString(customerId, "Customer ID");
        validatePositive(guests, "Guests");

        Table table = getTableOrThrow(tableNumber);

        if (guests > table.capacity) {
            throw new IllegalArgumentException("Guest count exceeds table capacity");
        }

        if (!isTableAvailable(tableNumber, date, time)) {
            throw new IllegalStateException("Table not available");
        }

        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
                reservationId,
                customerId,
                tableNumber,
                date,
                time,
                guests,
                ReservationStatus.ACTIVE,
                LocalDateTime.now()
        );

        reservations.put(reservationId, reservation);
        log("Created reservation " + reservationId + " for customer " + customerId);

        return reservationId;
    }

    /* ==========================
       AVAILABILITY CHECK
       ========================== */

    public boolean isTableAvailable(int tableNumber,
                                    LocalDate date,
                                    LocalTime time) {

        for (Reservation reservation : reservations.values()) {
            if (reservation.tableNumber == tableNumber &&
                reservation.date.equals(date) &&
                reservation.time.equals(time) &&
                reservation.status == ReservationStatus.ACTIVE) {
                return false;
            }
        }
        return true;
    }

    /* ==========================
       RESERVATION UPDATES
       ========================== */

    public void cancelReservation(String reservationId, String reason) {
        Reservation reservation = getReservationOrThrow(reservationId);

        if (reservation.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation not active");
        }

        reservation.status = ReservationStatus.CANCELLED;
        reservation.lastUpdated = LocalDateTime.now();

        log("Cancelled reservation " + reservationId + " reason=" + reason);
    }

    public void rescheduleReservation(String reservationId,
                                      LocalDate newDate,
                                      LocalTime newTime) {

        Reservation reservation = getReservationOrThrow(reservationId);

        if (reservation.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation not active");
        }

        if (!isTableAvailable(reservation.tableNumber, newDate, newTime)) {
            throw new IllegalStateException("Table not available at new time");
        }

        reservation.date = newDate;
        reservation.time = newTime;
        reservation.lastUpdated = LocalDateTime.now();

        log("Rescheduled reservation " + reservationId);
    }

    /* ==========================
       RESERVATION QUERIES
       ========================== */

    public Reservation getReservation(String reservationId) {
        return getReservationOrThrow(reservationId);
    }

    public List<Reservation> listAllReservations() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> listReservationsForDate(LocalDate date) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations.values()) {
            if (reservation.date.equals(date)) {
                result.add(reservation);
            }
        }
        return result;
    }

    public List<Reservation> listActiveReservations() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations.values()) {
            if (reservation.status == ReservationStatus.ACTIVE) {
                result.add(reservation);
            }
        }
        return result;
    }

    /* ==========================
       ANALYTICS
       ========================== */

    public int getTotalReservationCount() {
        return reservations.size();
    }

    public int getActiveReservationCount() {
        int count = 0;
        for (Reservation reservation : reservations.values()) {
            if (reservation.status == ReservationStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }

    public Map<Integer, Integer> getReservationCountByTable() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Reservation reservation : reservations.values()) {
            map.put(
                    reservation.tableNumber,
                    map.getOrDefault(reservation.tableNumber, 0) + 1
            );
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

    private Reservation getReservationOrThrow(String reservationId) {
        Reservation reservation = reservations.get(reservationId);
        if (reservation == null) {
            throw new NoSuchElementException("Reservation not found: " + reservationId);
        }
        return reservation;
    }

    private Table getTableOrThrow(int tableNumber) {
        Table table = tables.get(tableNumber);
        if (table == null) {
            throw new NoSuchElementException("Table not found: " + tableNumber);
        }
        return table;
    }

    /* ==========================
       INNER MODELS
       ========================== */

    public enum ReservationStatus {
        ACTIVE,
        CANCELLED
    }

    public static class Table {
        public int tableNumber;
        public int capacity;

        public Table(int tableNumber, int capacity) {
            this.tableNumber = tableNumber;
            this.capacity = capacity;
        }
    }

    public static class Reservation {
        public String id;
        public String customerId;
        public int tableNumber;
        public LocalDate date;
        public LocalTime time;
        public int guests;
        public ReservationStatus status;
        public LocalDateTime createdAt;
        public LocalDateTime lastUpdated;

        public Reservation(String id,
                           String customerId,
                           int tableNumber,
                           LocalDate date,
                           LocalTime time,
                           int guests,
                           ReservationStatus status,
                           LocalDateTime createdAt) {
            this.id = id;
            this.customerId = customerId;
            this.tableNumber = tableNumber;
            this.date = date;
            this.time = time;
            this.guests = guests;
            this.status = status;
            this.createdAt = createdAt;
            this.lastUpdated = createdAt;
        }
    }
}
