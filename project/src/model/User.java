package model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String userId;
    protected String username;
    protected String passwordHash;
    protected String fullName;
    protected String email;
    protected String phone;
    protected UserRole role;

    public User() {}

    public User(String userId, String username, String passwordHash, String fullName, String email, String phone, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    @Override
    public String toString() {
        return fullName + " (" + role.getTitle() + ")";
    }
}
