package model;

import java.io.Serializable;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private String feedbackId;
    private String complaintId;
    private String customerId;
    private int rating; // 1 to 5
    private String comments;
    private boolean satisfactionFlag; // true = satisfied, false = unsatisfied
    private long submittedTimestamp;

    public Feedback() {
        this.submittedTimestamp = System.currentTimeMillis();
    }

    public Feedback(String feedbackId, String complaintId, String customerId, int rating, String comments, boolean satisfactionFlag, long submittedTimestamp) {
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.customerId = customerId;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comments = comments != null ? comments : "";
        this.satisfactionFlag = satisfactionFlag;
        this.submittedTimestamp = submittedTimestamp;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public boolean isSatisfactionFlag() { return satisfactionFlag; }
    public void setSatisfactionFlag(boolean satisfactionFlag) { this.satisfactionFlag = satisfactionFlag; }

    public long getSubmittedTimestamp() { return submittedTimestamp; }
    public void setSubmittedTimestamp(long submittedTimestamp) { this.submittedTimestamp = submittedTimestamp; }
}
