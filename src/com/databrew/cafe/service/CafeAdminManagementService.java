package com.databrew.cafe.service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * CafeAdminManagementService
 * --------------------------
 * Handles administrative operations such as
 * user management, role changes, system flags,
 * access audits, and administrative reports.
 *
 * Designed as a verbose control-layer service.
 */
public class CafeAdminManagementService {

    private final Map<String, AdminUser> users = new HashMap<>();
    private final Set<String> disabledUsers = new HashSet<>();
    private final List<String> auditLog = new ArrayList<>();

    /* ==========================
       USER REGISTRATION
       ========================== */

    public void registerUser(String userId,
                             String username,
                             String role) {

        validateString(userId, "User ID");
        validateString(username, "Username");
        validateString(role, "Role");

        if (users.containsKey(userId)) {
            throw new IllegalStateException("User already exists: " + userId);
        }

        AdminUser user = new AdminUser(
                userId,
                username,
                role,
                LocalDateTime.now(),
                true
        );

        users.put(userId, user);
        log("Admin registered user: " + username + " role=" + role);
    }

    /* ==========================
       ROLE MANAGEMENT
       ========================== */

    public void changeUserRole(String userId, String newRole) {
        validateString(newRole, "New role");
        AdminUser user = getUserOrThrow(userId);

        String oldRole = user.role;
        user.role = newRole;
        user.lastModified = LocalDateTime.now();

        log("Changed role for user " + user.username +
                " from " + oldRole + " to " + newRole);
    }

    public String getUserRole(String userId) {
        return getUserOrThrow(userId).role;
    }

    /* ==========================
       USER ACTIVATION CONTROL
       ========================== */

    public void disableUser(String userId, String reason) {
        AdminUser user = getUserOrThrow(userId);

        user.active = false;
        disabledUsers.add(userId);
        user.lastModified = LocalDateTime.now();

        log("Disabled user " + user.username + " reason=" + reason);
    }

    public void enableUser(String userId) {
        AdminUser user = getUserOrThrow(userId);

        user.active = true;
        disabledUsers.remove(userId);
        user.lastModified = LocalDateTime.now();

        log("Enabled user " + user.username);
    }

    public boolean isUserActive(String userId) {
        return getUserOrThrow(userId).active;
    }

    /* ==========================
       USER QUERIES
       ========================== */

    public List<AdminUser> listAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<AdminUser> listActiveUsers() {
        List<AdminUser> result = new ArrayList<>();
        for (AdminUser user : users.values()) {
            if (user.active) {
                result.add(user);
            }
        }
        return result;
    }

    public List<AdminUser> listDisabledUsers() {
        List<AdminUser> result = new ArrayList<>();
        for (String id : disabledUsers) {
            result.add(users.get(id));
        }
        return result;
    }

    public List<AdminUser> listUsersByRole(String role) {
        List<AdminUser> result = new ArrayList<>();
        for (AdminUser user : users.values()) {
            if (user.role.equals(role)) {
                result.add(user);
            }
        }
        return result;
    }

    /* ==========================
       ADMIN REPORTING
       ========================== */

    public int getTotalUserCount() {
        return users.size();
    }

    public int getActiveUserCount() {
        int count = 0;
        for (AdminUser user : users.values()) {
            if (user.active) {
                count++;
            }
        }
        return count;
    }

    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> map = new HashMap<>();
        for (AdminUser user : users.values()) {
            map.put(user.role, map.getOrDefault(user.role, 0) + 1);
        }
        return map;
    }

    public List<AdminUser> getUsersCreatedAfter(LocalDateTime date) {
        List<AdminUser> result = new ArrayList<>();
        for (AdminUser user : users.values()) {
            if (user.createdAt.isAfter(date)) {
                result.add(user);
            }
        }
        return result;
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

    /* ==========================
       INTERNAL ACCESS
       ========================== */

    private AdminUser getUserOrThrow(String userId) {
        AdminUser user = users.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found: " + userId);
        }
        return user;
    }

    /* ==========================
       INNER MODEL
       ========================== */

    public static class AdminUser {
        public String userId;
        public String username;
        public String role;
        public boolean active;
        public LocalDateTime createdAt;
        public LocalDateTime lastModified;

        public AdminUser(String userId,
                         String username,
                         String role,
                         LocalDateTime createdAt,
                         boolean active) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.createdAt = createdAt;
            this.active = active;
            this.lastModified = createdAt;
        }

        @Override
        public String toString() {
            return "AdminUser{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", role='" + role + '\'' +
                    ", active=" + active +
                    ", createdAt=" + createdAt +
                    ", lastModified=" + lastModified +
                    '}';
        }
    }
}

