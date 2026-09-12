package model;

public enum ComplaintStatus {
    NEW("New"),
    UNDER_REVIEW("Under Review"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    WAITING_FOR_CUSTOMER("Waiting for Customer"),
    ESCALATED("Escalated"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    REOPENED("Reopened"),
    CANCELLED("Cancelled");

    private final String displayName;

    ComplaintStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ComplaintStatus fromString(String text) {
        if (text == null) return NEW;
        for (ComplaintStatus s : values()) {
            if (s.name().equalsIgnoreCase(text.trim()) || s.displayName.equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return NEW;
    }
}
