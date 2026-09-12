package model;

import java.io.Serializable;

public class ComplaintResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String responseId;
    private String complaintId;
    private String senderId;
    private String senderName;
    private UserRole senderRole;
    private String message;
    private long timestamp;
    private boolean isInternalNote;

    public ComplaintResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ComplaintResponse(String responseId, String complaintId, String senderId, String senderName,
                             UserRole senderRole, String message, long timestamp, boolean isInternalNote) {
        this.responseId = responseId;
        this.complaintId = complaintId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.timestamp = timestamp;
        this.isInternalNote = isInternalNote;
    }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public UserRole getSenderRole() { return senderRole; }
    public void setSenderRole(UserRole senderRole) { this.senderRole = senderRole; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isInternalNote() { return isInternalNote; }
    public void setInternalNote(boolean internalNote) { isInternalNote = internalNote; }
}
