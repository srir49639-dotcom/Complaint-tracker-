package model;

import java.io.Serializable;

public class Complaint implements Serializable, Comparable<Complaint> {
    private static final long serialVersionUID = 1L;

    private String complaintId;          // e.g. CMP-101
    private String trackingId;           // e.g. CMP-2026-000101
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String title;
    private String description;
    private String category;
    private String location;
    private PriorityLevel priority;
    private ComplaintStatus status;
    private String departmentId;
    private String departmentName;
    private String assignedStaffId;
    private String assignedStaffName;
    private String tags;                 // comma-separated
    private String supportingInfo;       // text/notes/attachments reference
    private long createdTimestamp;
    private long updatedTimestamp;
    private long expectedResolutionTimestamp;
    private long actualResolvedTimestamp;
    private EscalationLevel escalationLevel;
    private int reopenCount;
    private double dynamicPriorityScore; // calculated internally via DSA Priority Engine
    private int estimatedEffortHours;    // for scheduling & NP-approx

    public Complaint() {
        this.status = ComplaintStatus.NEW;
        this.priority = PriorityLevel.MEDIUM;
        this.escalationLevel = EscalationLevel.LEVEL_0;
        this.createdTimestamp = System.currentTimeMillis();
        this.updatedTimestamp = this.createdTimestamp;
        this.expectedResolutionTimestamp = this.createdTimestamp + (72L * 3600L * 1000L);
        this.reopenCount = 0;
        this.dynamicPriorityScore = 50.0;
        this.estimatedEffortHours = 4;
    }

    public Complaint(String complaintId, String trackingId, String customerId, String customerName,
                     String customerEmail, String customerPhone, String title, String description,
                     String category, String location, PriorityLevel priority, ComplaintStatus status,
                     String departmentId, String departmentName, String assignedStaffId, String assignedStaffName,
                     String tags, String supportingInfo, long createdTimestamp, long updatedTimestamp,
                     long expectedResolutionTimestamp, long actualResolvedTimestamp,
                     EscalationLevel escalationLevel, int reopenCount, double dynamicPriorityScore,
                     int estimatedEffortHours) {
        this.complaintId = complaintId;
        this.trackingId = trackingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.priority = priority != null ? priority : PriorityLevel.MEDIUM;
        this.status = status != null ? status : ComplaintStatus.NEW;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.assignedStaffId = assignedStaffId;
        this.assignedStaffName = assignedStaffName;
        this.tags = tags != null ? tags : "";
        this.supportingInfo = supportingInfo != null ? supportingInfo : "";
        this.createdTimestamp = createdTimestamp;
        this.updatedTimestamp = updatedTimestamp;
        this.expectedResolutionTimestamp = expectedResolutionTimestamp;
        this.actualResolvedTimestamp = actualResolvedTimestamp;
        this.escalationLevel = escalationLevel != null ? escalationLevel : EscalationLevel.LEVEL_0;
        this.reopenCount = reopenCount;
        this.dynamicPriorityScore = dynamicPriorityScore;
        this.estimatedEffortHours = estimatedEffortHours > 0 ? estimatedEffortHours : 4;
    }

    // Getters and Setters
    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(String assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getSupportingInfo() { return supportingInfo; }
    public void setSupportingInfo(String supportingInfo) { this.supportingInfo = supportingInfo; }

    public long getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(long createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public long getUpdatedTimestamp() { return updatedTimestamp; }
    public void setUpdatedTimestamp(long updatedTimestamp) { this.updatedTimestamp = updatedTimestamp; }

    public long getExpectedResolutionTimestamp() { return expectedResolutionTimestamp; }
    public void setExpectedResolutionTimestamp(long expectedResolutionTimestamp) { this.expectedResolutionTimestamp = expectedResolutionTimestamp; }

    public long getActualResolvedTimestamp() { return actualResolvedTimestamp; }
    public void setActualResolvedTimestamp(long actualResolvedTimestamp) { this.actualResolvedTimestamp = actualResolvedTimestamp; }

    public EscalationLevel getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(EscalationLevel escalationLevel) { this.escalationLevel = escalationLevel; }

    public int getReopenCount() { return reopenCount; }
    public void setReopenCount(int reopenCount) { this.reopenCount = reopenCount; }

    public double getDynamicPriorityScore() { return dynamicPriorityScore; }
    public void setDynamicPriorityScore(double dynamicPriorityScore) { this.dynamicPriorityScore = dynamicPriorityScore; }

    public int getEstimatedEffortHours() { return estimatedEffortHours; }
    public void setEstimatedEffortHours(int estimatedEffortHours) { this.estimatedEffortHours = estimatedEffortHours; }

    public boolean isOverdue() {
        if (status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CLOSED || status == ComplaintStatus.CANCELLED) {
            return false;
        }
        return System.currentTimeMillis() > expectedResolutionTimestamp;
    }

    public boolean isResolvedOrClosed() {
        return status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CLOSED || status == ComplaintStatus.CANCELLED;
    }

    public String getDsaStrategy() {
        if (priority == PriorityLevel.CRITICAL || dynamicPriorityScore >= 80.0) {
            return "Max-Heap Triage [O(log N)]";
        } else if (status == ComplaintStatus.IN_PROGRESS && assignedStaffId != null) {
            return "Bipartite Flow [Dinic Match]";
        } else if (isOverdue()) {
            return "EDF Min-Heap SLA [O(log N)]";
        } else if (status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CLOSED) {
            return "Parallel MergeSort Archive";
        } else if (category != null && (category.toLowerCase().contains("it") || category.toLowerCase().contains("net") || category.toLowerCase().contains("elec"))) {
            return "Aho-Corasick Automaton";
        } else if (estimatedEffortHours > 6) {
            return "Graham LPT 4/3-Approx";
        } else {
            return "KMP Pattern Match [O(N+M)]";
        }
    }

    @Override
    public int compareTo(Complaint other) {
        // High priority first (for Priority Queue Heap operations)
        return Double.compare(other.dynamicPriorityScore, this.dynamicPriorityScore);
    }

    @Override
    public String toString() {
        return "[" + trackingId + "] " + title + " (" + status.getDisplayName() + ")";
    }
}
