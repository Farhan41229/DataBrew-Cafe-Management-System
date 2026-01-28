package com.databrew.cafe.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeStaffSchedulingService
 * --------------------------
 * Manages staff registration, shift scheduling,
 * availability checks, attendance, and reports.
 */
public class CafeStaffSchedulingService {

    private final Map<String, Staff> staffMembers = new HashMap<>();
    private final Map<String, List<Shift>> schedules = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       STAFF REGISTRATION
       ========================== */

    public void registerStaff(String staffId,
                              String name,
                              String role) {

        validateString(staffId, "Staff ID");
        validateString(name, "Name");
        validateString(role, "Role");

        if (staffMembers.containsKey(staffId)) {
            throw new IllegalStateException("Staff already exists: " + staffId);
        }

        Staff staff = new Staff(
                staffId,
                name,
                role,
                true,
                LocalDateTime.now()
        );

        staffMembers.put(staffId, staff);
        schedules.put(staffId, new ArrayList<>());

        log("Registered staff: " + name + " role=" + role);
    }

    /* ==========================
       STAFF STATUS
       ========================== */

    public void deactivateStaff(String staffId) {
        Staff staff = getStaffOrThrow(staffId);
        staff.active = false;
        log("Deactivated staff: " + staff.name);
    }

    public void activateStaff(String staffId) {
        Staff staff = getStaffOrThrow(staffId);
        staff.active = true;
        log("Activated staff: " + staff.name);
    }

    public boolean isStaffActive(String staffId) {
        return getStaffOrThrow(staffId).active;
    }

    /* ==========================
       SHIFT SCHEDULING
       ========================== */

    public void scheduleShift(String staffId,
                              LocalDate date,
                              int startHour,
                              int endHour) {

        validateHour(startHour);
        validateHour(endHour);

        if (startHour >= endHour) {
            throw new IllegalArgumentException("Start hour must be before end hour");
        }

        Staff staff = getStaffOrThrow(staffId);

        if (!staff.active) {
            throw new IllegalStateException("Staff is not active");
        }

        Shift shift = new Shift(
                date,
                startHour,
                endHour,
                ShiftStatus.SCHEDULED
        );

        if (hasConflict(staffId, shift)) {
            throw new IllegalStateException("Shift conflict detected");
        }

        schedules.get(staffId).add(shift);
        log("Scheduled shift for " + staff.name + " on " + date);
    }

    public void cancelShift(String staffId,
                            LocalDate date,
                            int startHour) {

        List<Shift> shifts = schedules.get(staffId);
        if (shifts == null) {
            throw new NoSuchElementException("Staff not found");
        }

        for (Shift shift : shifts) {
            if (shift.date.equals(date) &&
                shift.startHour == startHour &&
                shift.status == ShiftStatus.SCHEDULED) {

                shift.status = ShiftStatus.CANCELLED;
                log("Cancelled shift for staff " + staffId);
                return;
            }
        }

        throw new NoSuchElementException("Shift not found");
    }

    /* ==========================
       ATTENDANCE
       ========================== */

    public void markAttendance(String staffId,
                               LocalDate date,
                               int startHour) {

        Shift shift = getShiftOrThrow(staffId, date, startHour);

        if (shift.status != ShiftStatus.SCHEDULED) {
            throw new IllegalStateException("Shift not active");
        }

        shift.status = ShiftStatus.COMPLETED;
        shift.completedAt = LocalDateTime.now();

        log("Marked attendance for staff " + staffId);
    }

    /* ==========================
       QUERIES
       ========================== */

    public List<Shift> getShiftsForStaff(String staffId) {
        return new ArrayList<>(schedules.getOrDefault(staffId, new ArrayList<>()));
    }

    public List<Shift> getShiftsForDate(LocalDate date) {
        List<Shift> result = new ArrayList<>();
        for (List<Shift> shifts : schedules.values()) {
            for (Shift shift : shifts) {
                if (shift.date.equals(date)) {
                    result.add(shift);
                }
            }
        }
        return result;
    }

    public int getTotalScheduledHours(String staffId) {
        int total = 0;
        for (Shift shift : getShiftsForStaff(staffId)) {
            if (shift.status == ShiftStatus.SCHEDULED ||
                shift.status == ShiftStatus.COMPLETED) {
                total += shift.endHour - shift.startHour;
            }
        }
        return total;
    }

    public Map<DayOfWeek, Integer> getWeeklyHours(String staffId) {
        Map<DayOfWeek, Integer> map = new EnumMap<>(DayOfWeek.class);
        for (Shift shift : getShiftsForStaff(staffId)) {
            DayOfWeek day = shift.date.getDayOfWeek();
            int hours = shift.endHour - shift.startHour;
            map.put(day, map.getOrDefault(day, 0) + hours);
        }
        return map;
    }

    /* ==========================
       CONFLICT CHECK
       ========================== */

    private boolean hasConflict(String staffId, Shift newShift) {
        for (Shift shift : schedules.get(staffId)) {
            if (shift.date.equals(newShift.date)) {
                if (newShift.startHour < shift.endHour &&
                    newShift.endHour > shift.startHour) {
                    return true;
                }
            }
        }
        return false;
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

    private void validateHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
    }

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private Staff getStaffOrThrow(String staffId) {
        Staff staff = staffMembers.get(staffId);
        if (staff == null) {
            throw new NoSuchElementException("Staff not found: " + staffId);
        }
        return staff;
    }

    private Shift getShiftOrThrow(String staffId,
                                  LocalDate date,
                                  int startHour) {

        for (Shift shift : schedules.getOrDefault(staffId, new ArrayList<>())) {
            if (shift.date.equals(date) && shift.startHour == startHour) {
                return shift;
            }
        }
        throw new NoSuchElementException("Shift not found");
    }

    /* ==========================
       INNER MODELS
       ========================== */

    public static class Staff {
        public String id;
        public String name;
        public String role;
        public boolean active;
        public LocalDateTime createdAt;

        public Staff(String id,
                     String name,
                     String role,
                     boolean active,
                     LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.active = active;
            this.createdAt = createdAt;
        }
    }

    public enum ShiftStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }

    public static class Shift {
        public LocalDate date;
        public int startHour;
        public int endHour;
        public ShiftStatus status;
        public LocalDateTime completedAt;

        public Shift(LocalDate date,
                     int startHour,
                     int endHour,
                     ShiftStatus status) {
            this.date = date;
            this.startHour = startHour;
            this.endHour = endHour;
            this.status = status;
        }
    }
}

