package services;

import model.Complaint;
import model.ComplaintStatus;
import model.EscalationLevel;
import model.PriorityLevel;
import datastructures.CustomArrayList;
import datastructures.CustomPriorityQueue;

public class PriorityEngineService {

    private static PriorityEngineService instance;

    public static synchronized PriorityEngineService getInstance() {
        if (instance == null) {
            instance = new PriorityEngineService();
        }
        return instance;
    }

    /**
     * Dynamically calculates complaint priority score based on severity rank, affected count,
     * age, SLA status, reopen count, and escalation level.
     */
    public double calculateDynamicScore(Complaint complaint) {
        if (complaint == null) return 0.0;

        double score = complaint.getPriority().getSeverityRank() * 25.0; // base 25, 50, 75, 100

        // Age factor: +1.5 points per elapsed 12 hours
        long elapsedMs = System.currentTimeMillis() - complaint.getCreatedTimestamp();
        double elapsedHours = (double) elapsedMs / (3600.0 * 1000.0);
        score += Math.min(30.0, (elapsedHours / 12.0) * 1.5);

        // Escalation boost: +15 points per escalation level
        score += complaint.getEscalationLevel().getLevel() * 15.0;

        // Reopen boost: +10 points per reopen
        score += complaint.getReopenCount() * 10.0;

        // Overdue penalty boost: +20 points if past SLA deadline
        if (complaint.isOverdue()) {
            score += 20.0;
        }

        return score;
    }

    /**
     * Sorts complaints by priority using a Custom Priority Queue (Binary Max-Heap).
     */
    public CustomArrayList<Complaint> getTopPrioritizedComplaints(CustomArrayList<Complaint> complaints, int limit) {
        if (complaints == null || complaints.isEmpty()) return new CustomArrayList<>();

        // Custom Max-Heap using Complaint's compareTo (higher dynamicPriorityScore first)
        CustomPriorityQueue<Complaint> maxHeap = new CustomPriorityQueue<>(complaints.size(), (c1, c2) -> Double.compare(c2.getDynamicPriorityScore(), c1.getDynamicPriorityScore()));

        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            // Refresh dynamic score
            c.setDynamicPriorityScore(calculateDynamicScore(c));
            maxHeap.offer(c);
        }

        CustomArrayList<Complaint> result = new CustomArrayList<>();
        int count = Math.min(limit > 0 ? limit : complaints.size(), maxHeap.size());
        for (int i = 0; i < count; i++) {
            Complaint top = maxHeap.poll();
            if (top != null) {
                result.add(top);
            }
        }
        return result;
    }
}
