package Models;

import java.util.Date;

/**
 * User model for authentication and authorization.
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private Date createdAt;
    private Date lastLogin;

    public enum Role {
        ADMIN("Administrator"),
        MANAGER("Manager"),
        EMPLOYEE("Employee"),
        RECEPTIONIST("Receptionist");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public User() {
        this.active = true;
        this.createdAt = new Date();
    }

    public User(String username, String passwordHash, String fullName, String email, Role role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Permission checks
    public boolean canManageUsers() {
        return role == Role.ADMIN;
    }

    public boolean canViewReports() {
        return role == Role.ADMIN || role == Role.MANAGER;
    }

    public boolean canManageEmployees() {
        return role == Role.ADMIN || role == Role.MANAGER;
    }

    public boolean canManageOrders() {
        return role != null; // All roles can manage orders
    }

    public boolean canManageInventory() {
        return role == Role.ADMIN || role == Role.MANAGER;
    }

    public boolean canManageSuppliers() {
        return role == Role.ADMIN || role == Role.MANAGER;
    }

    @Override
    public String toString() {
        return fullName + " (" + role.getDisplayName() + ")";
    }
}