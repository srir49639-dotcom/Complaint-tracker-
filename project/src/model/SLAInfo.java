package model;

import java.io.Serializable;

public class SLAInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum SLAStatus {
        ON_TRACK("On Track"),
        AT_RISK("At Risk"),
        BREACHED("Overdue / Breached");

        private final String label;
        SLAStatus(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private String complaintId;
    private PriorityLevel priority;
    private long creationTime;
    private long targetResolutionTime;
    private SLAStatus status;
    private double hoursRemaining;

    public SLAInfo(String complaintId, PriorityLevel priority, long creationTime, long targetResolutionTime) {
        this.complaintId = complaintId;
        this.priority = priority;
        this.creationTime = creationTime;
        this.targetResolutionTime = targetResolutionTime;
        recalculate();
    }

    public void recalculate() {
        long now = System.currentTimeMillis();
        long diffMs = targetResolutionTime - now;
        this.hoursRemaining = (double) diffMs / (1000.0 * 3600.0);

        if (diffMs < 0) {
            this.status = SLAStatus.BREACHED;
        } else if (diffMs < (6L * 3600L * 1000L)) { // Less than 6 hours left
            this.status = SLAStatus.AT_RISK;
        } else {
            this.status = SLAStatus.ON_TRACK;
        }
    }

    public String getComplaintId() { return complaintId; }
    public PriorityLevel getPriority() { return priority; }
    public long getCreationTime() { return creationTime; }
    public long getTargetResolutionTime() { return targetResolutionTime; }
    public SLAStatus getStatus() { return status; }
    public double getHoursRemaining() { return hoursRemaining; }
}
