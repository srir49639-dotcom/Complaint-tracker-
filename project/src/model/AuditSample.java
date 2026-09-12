package model;

import java.io.Serializable;

public class AuditSample implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sampleId;
    private String complaintId;
    private String trackingId;
    private String title;
    private String category;
    private String department;
    private String status;
    private long selectedTimestamp;
    private String auditNotes;

    public AuditSample(String sampleId, Complaint complaint, String auditNotes) {
        this.sampleId = sampleId;
        if (complaint != null) {
            this.complaintId = complaint.getComplaintId();
            this.trackingId = complaint.getTrackingId();
            this.title = complaint.getTitle();
            this.category = complaint.getCategory();
            this.department = complaint.getDepartmentName();
            this.status = complaint.getStatus().getDisplayName();
        }
        this.selectedTimestamp = System.currentTimeMillis();
        this.auditNotes = auditNotes != null ? auditNotes : "";
    }

    public String getSampleId() { return sampleId; }
    public String getComplaintId() { return complaintId; }
    public String getTrackingId() { return trackingId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public long getSelectedTimestamp() { return selectedTimestamp; }
    public String getAuditNotes() { return auditNotes; }
    public void setAuditNotes(String auditNotes) { this.auditNotes = auditNotes; }
}
