package model;

public enum UserRole {
    CUSTOMER("Customer"),
    STAFF("Staff Member"),
    ORGANIZATION_ADMIN("Organization Admin");

    private final String title;

    UserRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static UserRole fromString(String role) {
        if (role == null) return CUSTOMER;
        for (UserRole r : values()) {
            if (r.name().equalsIgnoreCase(role.trim()) || r.title.equalsIgnoreCase(role.trim())) {
                return r;
            }
        }
        return CUSTOMER;
    }
}
