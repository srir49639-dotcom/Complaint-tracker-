package model;

import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationId;
    private String recipientUserId;
    private String recipientRole; // "CUSTOMER" or "STAFF" or "ADMIN"
    private String complaintId;
    private String title;
    private String message;
    private long timestamp;
    private boolean read;

    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    public Notification(String notificationId, String recipientUserId, String recipientRole,
                        String complaintId, String title, String message, long timestamp, boolean read) {
        this.notificationId = notificationId;
        this.recipientUserId = recipientUserId;
        this.recipientRole = recipientRole;
        this.complaintId = complaintId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
