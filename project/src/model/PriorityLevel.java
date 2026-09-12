package model;

public enum PriorityLevel {
    LOW("Low", 1, 168), // 7 days (168h) SLA
    MEDIUM("Medium", 2, 72), // 3 days (72h) SLA
    HIGH("High", 3, 24), // 24 hours SLA
    CRITICAL("Critical", 4, 4); // 4 hours SLA

    private final String displayName;
    private final int severityRank;
    private final int slaHours;

    PriorityLevel(String displayName, int severityRank, int slaHours) {
        this.displayName = displayName;
        this.severityRank = severityRank;
        this.slaHours = slaHours;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSeverityRank() {
        return severityRank;
    }

    public int getSlaHours() {
        return slaHours;
    }

    public static PriorityLevel fromString(String text) {
        if (text == null) return MEDIUM;
        for (PriorityLevel p : values()) {
            if (p.name().equalsIgnoreCase(text.trim()) || p.displayName.equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return MEDIUM;
    }
}
