package services;

import model.*;
import repository.*;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportExportService {

    private static ReportExportService instance;
    private final ComplaintRepository complaintRepo;
    private final DepartmentRepository deptRepo;
    private final StaffRepository staffRepo;
    private final FeedbackRepository feedbackRepo;

    public ReportExportService() {
        this.complaintRepo = ComplaintRepository.getInstance();
        this.deptRepo = DepartmentRepository.getInstance();
        this.staffRepo = StaffRepository.getInstance();
        this.feedbackRepo = FeedbackRepository.getInstance();
    }

    public static synchronized ReportExportService getInstance() {
        if (instance == null) {
            instance = new ReportExportService();
        }
        return instance;
    }

    public File exportComplaintsToCSV(File destinationFile) throws IOException {
        CustomArrayList<Complaint> list = complaintRepo.findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            writer.write("Complaint ID,Tracking ID,Title,Category,Department,Status,Priority,Escalation,Created Date,Updated Date,Staff");
            writer.newLine();

            for (int i = 0; i < list.size(); i++) {
                Complaint c = list.get(i);
                String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        c.getComplaintId(), c.getTrackingId(),
                        c.getTitle().replace("\"", "\"\""),
                        c.getCategory(), c.getDepartmentName(),
                        c.getStatus().getDisplayName(),
                        c.getPriority().getDisplayName(),
                        c.getEscalationLevel().getDescription(),
                        DateFormatter.formatDateTime(c.getCreatedTimestamp()),
                        DateFormatter.formatDateTime(c.getUpdatedTimestamp()),
                        c.getAssignedStaffName() != null ? c.getAssignedStaffName() : "Unassigned"
                );
                writer.write(line);
                writer.newLine();
            }
        }
        return destinationFile;
    }

    public File exportSummaryReportTXT(File destinationFile) throws IOException {
        CustomArrayList<Complaint> complaints = complaintRepo.findAll();
        CustomArrayList<Department> depts = deptRepo.findAll();
        CustomArrayList<Staff> staffList = staffRepo.findAll();
        CustomArrayList<Feedback> feedbacks = feedbackRepo.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile))) {
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                      SMART COMPLAINT TRACKER — AUDIT REPORT                    ");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Generated Date: " + DateFormatter.formatDateTime(System.currentTimeMillis()));
            writer.newLine();
            writer.write("Total Registered Complaints: " + complaints.size());
            writer.newLine();
            writer.write("Active Departments: " + depts.size());
            writer.newLine();
            writer.write("Active Staff Members: " + staffList.size());
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.write("1. DEPARTMENT WORKLOAD SUMMARY:");
            writer.newLine();

            for (int i = 0; i < depts.size(); i++) {
                Department d = depts.get(i);
                CustomArrayList<Complaint> dComplaints = complaintRepo.findByDepartmentName(d.getName());
                writer.write(String.format("   * %-20s | Active: %-3d | Capacity: %-3d | Head: %s",
                        d.getName(), dComplaints.size(), d.getCapacity(), d.getHeadName()));
                writer.newLine();
            }

            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.write("2. STAFF ALLOCATION & CAPACITY:");
            writer.newLine();

            for (int i = 0; i < staffList.size(); i++) {
                Staff s = staffList.get(i);
                writer.write(String.format("   * %-20s | Dept: %-15s | Workload: %d/%d | Status: %s",
                        s.getFullName(), s.getDepartmentName(), s.getCurrentWorkload(), s.getMaxWorkload(), s.getStatus()));
                writer.newLine();
            }

            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.write("3. CUSTOMER SATISFACTION SUMMARY:");
            writer.newLine();
            writer.write("   * Total Reviews Received: " + feedbacks.size());
            writer.newLine();

            double totalRating = 0;
            for (int i = 0; i < feedbacks.size(); i++) totalRating += feedbacks.get(i).getRating();
            double avgRating = feedbacks.size() > 0 ? totalRating / feedbacks.size() : 0.0;

            writer.write(String.format("   * Average Satisfaction Rating: %.2f / 5.00", avgRating));
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
        }
        return destinationFile;
    }
}
