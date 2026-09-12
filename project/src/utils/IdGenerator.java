package utils;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final AtomicLong COMPLAINT_COUNTER = new AtomicLong(100);
    private static final AtomicLong CUSTOMER_COUNTER = new AtomicLong(1000);
    private static final AtomicLong STAFF_COUNTER = new AtomicLong(500);
    private static final AtomicLong DEPT_COUNTER = new AtomicLong(10);
    private static final AtomicLong NOTIF_COUNTER = new AtomicLong(5000);
    private static final AtomicLong HIST_COUNTER = new AtomicLong(10000);
    private static final AtomicLong RESP_COUNTER = new AtomicLong(20000);
    private static final AtomicLong FEED_COUNTER = new AtomicLong(30000);
    private static final AtomicLong ASSIGN_COUNTER = new AtomicLong(40000);

    public static String generateComplaintId() {
        return "CMP-" + COMPLAINT_COUNTER.incrementAndGet();
    }

    public static String generateTrackingId() {
        return "CMP-2026-" + String.format("%06d", COMPLAINT_COUNTER.get());
    }

    public static String generateCustomerId() {
        return "CUST-" + CUSTOMER_COUNTER.incrementAndGet();
    }

    public static String generateStaffId() {
        return "STF-" + STAFF_COUNTER.incrementAndGet();
    }

    public static String generateDepartmentId() {
        return "DPT-" + DEPT_COUNTER.incrementAndGet();
    }

    public static String generateNotificationId() {
        return "NTF-" + NOTIF_COUNTER.incrementAndGet();
    }

    public static String generateHistoryId() {
        return "HST-" + HIST_COUNTER.incrementAndGet();
    }

    public static String generateResponseId() {
        return "RSP-" + RESP_COUNTER.incrementAndGet();
    }

    public static String generateFeedbackId() {
        return "FDB-" + FEED_COUNTER.incrementAndGet();
    }

    public static String generateAssignmentId() {
        return "ASG-" + ASSIGN_COUNTER.incrementAndGet();
    }
}
