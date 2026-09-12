package services;

import algorithms.approximation.JobSchedulingApproximation;
import model.Complaint;
import model.Staff;
import repository.StaffRepository;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

public class SchedulingService {

    private static SchedulingService instance;
    private final JobSchedulingApproximation scheduler;
    private final StaffRepository staffRepository;

    public SchedulingService() {
        this.scheduler = new JobSchedulingApproximation();
        this.staffRepository = StaffRepository.getInstance();
    }

    public static synchronized SchedulingService getInstance() {
        if (instance == null) {
            instance = new SchedulingService();
        }
        return instance;
    }

    public static class SchedulePlan {
        public final CustomHashTable<String, CustomArrayList<Complaint>> staffAllocations;
        public final double estimatedMakespanHours;
        public final String scheduleQuality;
        public final String statusSummary;
        public final String internalSolverType; // For academic evaluation

        public SchedulePlan(CustomHashTable<String, CustomArrayList<Complaint>> staffAllocations,
                            double estimatedMakespanHours, String scheduleQuality,
                            String statusSummary, String internalSolverType) {
            this.staffAllocations = staffAllocations;
            this.estimatedMakespanHours = estimatedMakespanHours;
            this.scheduleQuality = scheduleQuality;
            this.statusSummary = statusSummary;
            this.internalSolverType = internalSolverType;
        }
    }

    public SchedulePlan generateWorkSchedule(CustomArrayList<Complaint> activeComplaints) {
        if (activeComplaints == null || activeComplaints.isEmpty()) {
            return new SchedulePlan(new CustomHashTable<>(), 0.0, "N/A", "No active complaints to schedule.", "None");
        }

        CustomArrayList<Staff> staffList = staffRepository.findAll();
        if (staffList.isEmpty()) {
            return new SchedulePlan(new CustomHashTable<>(), 0.0, "N/A", "No staff available for scheduling.", "None");
        }

        JobSchedulingApproximation.ScheduleOutput out = scheduler.schedule(activeComplaints, staffList);

        String quality = out.qualityAssessment.contains("Optimal") ? "Optimal" : "Excellent (Guaranteed <= 1.33x Optimal)";
        String summary = "Schedule generated for " + activeComplaints.size() + " complaints across " +
                staffList.size() + " staff members. Estimated max completion: " +
                String.format("%.1f", out.makespanHours) + " hours.";

        return new SchedulePlan(out.staffAssignments, out.makespanHours, quality, summary, out.solverType);
    }
}
