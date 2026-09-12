package model;

import java.io.Serializable;

public class ComplaintHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String historyId;
    private String complaintId;
    private String action;
    private ComplaintStatus previousStatus;
    private ComplaintStatus newStatus;
    private PriorityLevel previousPriority;
    private PriorityLevel newPriority;
    private String performedByUserId;
    private String performedByUserName;
    private long timestamp;
    private String comment;

    public ComplaintHistory() {
        this.timestamp = System.currentTimeMillis();
    }

    public ComplaintHistory(String historyId, String complaintId, String action,
                            ComplaintStatus previousStatus, ComplaintStatus newStatus,
                            PriorityLevel previousPriority, PriorityLevel newPriority,
                            String performedByUserId, String performedByUserName,
                            long timestamp, String comment) {
        this.historyId = historyId;
        this.complaintId = complaintId;
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.previousPriority = previousPriority;
        this.newPriority = newPriority;
        this.performedByUserId = performedByUserId;
        this.performedByUserName = performedByUserName;
        this.timestamp = timestamp;
        this.comment = comment != null ? comment : "";
    }

    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public ComplaintStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ComplaintStatus previousStatus) { this.previousStatus = previousStatus; }

    public ComplaintStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ComplaintStatus newStatus) { this.newStatus = newStatus; }

    public PriorityLevel getPreviousPriority() { return previousPriority; }
    public void setPreviousPriority(PriorityLevel previousPriority) { this.previousPriority = previousPriority; }

    public PriorityLevel getNewPriority() { return newPriority; }
    public void setNewPriority(PriorityLevel newPriority) { this.newPriority = newPriority; }

    public String getPerformedByUserId() { return performedByUserId; }
    public void setPerformedByUserId(String performedByUserId) { this.performedByUserId = performedByUserId; }

    public String getPerformedByUserName() { return performedByUserName; }
    public void setPerformedByUserName(String performedByUserName) { this.performedByUserName = performedByUserName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
