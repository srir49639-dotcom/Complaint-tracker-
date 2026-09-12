package model;

import java.io.Serializable;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String assignmentId;
    private String complaintId;
    private String departmentId;
    private String staffId;
    private String assignedByUserId;
    private long assignedTimestamp;
    private String notes;
    private boolean active;

    public Assignment() {
        this.assignedTimestamp = System.currentTimeMillis();
        this.active = true;
    }

    public Assignment(String assignmentId, String complaintId, String departmentId, String staffId,
                      String assignedByUserId, long assignedTimestamp, String notes, boolean active) {
        this.assignmentId = assignmentId;
        this.complaintId = complaintId;
        this.departmentId = departmentId;
        this.staffId = staffId;
        this.assignedByUserId = assignedByUserId;
        this.assignedTimestamp = assignedTimestamp;
        this.notes = notes != null ? notes : "";
        this.active = active;
    }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getAssignedByUserId() { return assignedByUserId; }
    public void setAssignedByUserId(String assignedByUserId) { this.assignedByUserId = assignedByUserId; }

    public long getAssignedTimestamp() { return assignedTimestamp; }
    public void setAssignedTimestamp(long assignedTimestamp) { this.assignedTimestamp = assignedTimestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
