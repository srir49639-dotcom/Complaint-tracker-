package services;

import model.*;
import notifications.NotificationManager;
import repository.*;
import utils.DateFormatter;
import utils.IdGenerator;
import datastructures.CustomArrayList;

public class ComplaintService {

    private static ComplaintService instance;

    private final ComplaintRepository complaintRepo;
    private final HistoryRepository historyRepo;
    private final ResponseRepository responseRepo;
    private final AssignmentRepository assignmentRepo;
    private final FeedbackRepository feedbackRepo;
    private final StaffRepository staffRepo;
    private final DepartmentRepository deptRepo;
    private final NotificationManager notificationManager;
    private final PriorityEngineService priorityEngine;

    public ComplaintService() {
        this.complaintRepo = ComplaintRepository.getInstance();
        this.historyRepo = HistoryRepository.getInstance();
        this.responseRepo = ResponseRepository.getInstance();
        this.assignmentRepo = AssignmentRepository.getInstance();
        this.feedbackRepo = FeedbackRepository.getInstance();
        this.staffRepo = StaffRepository.getInstance();
        this.deptRepo = DepartmentRepository.getInstance();
        this.notificationManager = NotificationManager.getInstance();
        this.priorityEngine = PriorityEngineService.getInstance();
    }

    public static synchronized ComplaintService getInstance() {
        if (instance == null) {
            instance = new ComplaintService();
        }
        return instance;
    }

    public Complaint submitComplaint(String customerId, String customerName, String customerEmail, String customerPhone,
                                     String title, String description, String category, String location,
                                     PriorityLevel priority, String preferredDept, String tags, String supportingInfo) {
        String cid = IdGenerator.generateComplaintId();
        String trackingId = IdGenerator.generateTrackingId();
        long now = System.currentTimeMillis();

        PriorityLevel prio = (priority != null) ? priority : PriorityLevel.MEDIUM;
        long targetResolution = now + (prio.getSlaHours() * 3600L * 1000L);

        Department dept = null;
        if (preferredDept != null && !preferredDept.isEmpty()) {
            dept = deptRepo.findByName(preferredDept);
        }
        if (dept == null && category != null) {
            dept = deptRepo.findByName(category);
        }

        Complaint cmp = new Complaint(
                cid, trackingId, customerId, customerName, customerEmail, customerPhone,
                title, description, category, location, prio, ComplaintStatus.NEW,
                dept != null ? dept.getDepartmentId() : "",
                dept != null ? dept.getName() : "Unassigned",
                "", "", tags, supportingInfo, now, now, targetResolution, 0,
                EscalationLevel.LEVEL_0, 0, 50.0, 4
        );

        cmp.setDynamicPriorityScore(priorityEngine.calculateDynamicScore(cmp));
        complaintRepo.add(cmp);

        // Record history
        recordHistory(cid, "COMPLAINT_SUBMITTED", null, ComplaintStatus.NEW, null, prio, customerId, customerName, "Complaint created by customer.");

        // Send notifications
        notificationManager.notify(customerId, "CUSTOMER", cid, "Complaint Submitted", "Your complaint " + trackingId + " has been registered successfully.");
        notificationManager.notify("ORG_ADMIN", "ADMIN", cid, "New Complaint Arrived", "Complaint " + trackingId + " (" + category + ") submitted by " + customerName + ".");

        return cmp;
    }

    public boolean updateStatus(String complaintId, ComplaintStatus newStatus, String userId, String userName, String comment) {
        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp == null) return false;

        ComplaintStatus oldStatus = cmp.getStatus();
        cmp.setStatus(newStatus);
        cmp.setUpdatedTimestamp(System.currentTimeMillis());

        if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.CLOSED) {
            cmp.setActualResolvedTimestamp(System.currentTimeMillis());
            if (cmp.getAssignedStaffId() != null && !cmp.getAssignedStaffId().isEmpty()) {
                Staff s = staffRepo.findById(cmp.getAssignedStaffId());
                if (s != null && s.getCurrentWorkload() > 0) {
                    s.setCurrentWorkload(s.getCurrentWorkload() - 1);
                    staffRepo.update(s);
                }
            }
        }

        complaintRepo.update(cmp);
        recordHistory(complaintId, "STATUS_CHANGE", oldStatus, newStatus, cmp.getPriority(), cmp.getPriority(), userId, userName, comment);

        notificationManager.notify(cmp.getCustomerId(), "CUSTOMER", complaintId, "Status Updated",
                "Your complaint " + cmp.getTrackingId() + " status is now " + newStatus.getDisplayName() + ".");

        return true;
    }

    public boolean assignComplaint(String complaintId, String deptName, String staffId, String assignedByUserId, String assignedByUserName, String notes) {
        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp == null) return false;

        Staff staff = staffRepo.findById(staffId);
        Department dept = deptRepo.findByName(deptName);

        String prevStaffId = cmp.getAssignedStaffId();

        cmp.setDepartmentName(deptName);
        cmp.setDepartmentId(dept != null ? dept.getDepartmentId() : "");
        cmp.setAssignedStaffId(staff != null ? staff.getStaffId() : "");
        cmp.setAssignedStaffName(staff != null ? staff.getFullName() : "");
        cmp.setStatus(ComplaintStatus.ASSIGNED);
        cmp.setUpdatedTimestamp(System.currentTimeMillis());

        if (staff != null) {
            staff.setCurrentWorkload(staff.getCurrentWorkload() + 1);
            staffRepo.update(staff);
        }

        if (prevStaffId != null && !prevStaffId.isEmpty() && !prevStaffId.equals(staffId)) {
            Staff oldStaff = staffRepo.findById(prevStaffId);
            if (oldStaff != null && oldStaff.getCurrentWorkload() > 0) {
                oldStaff.setCurrentWorkload(oldStaff.getCurrentWorkload() - 1);
                staffRepo.update(oldStaff);
            }
        }

        complaintRepo.update(cmp);

        // Record Assignment
        String asgId = IdGenerator.generateAssignmentId();
        Assignment asg = new Assignment(asgId, complaintId, cmp.getDepartmentId(), cmp.getAssignedStaffId(),
                assignedByUserId, System.currentTimeMillis(), notes, true);
        assignmentRepo.add(asg);

        recordHistory(complaintId, "COMPLAINT_ASSIGNED", cmp.getStatus(), ComplaintStatus.ASSIGNED,
                cmp.getPriority(), cmp.getPriority(), assignedByUserId, assignedByUserName,
                "Assigned to " + (staff != null ? staff.getFullName() : "Department " + deptName) + ". " + notes);

        notificationManager.notify(cmp.getCustomerId(), "CUSTOMER", complaintId, "Complaint Assigned",
                "Your complaint " + cmp.getTrackingId() + " has been assigned to " + (staff != null ? staff.getFullName() : deptName) + ".");

        if (staff != null) {
            notificationManager.notify(staff.getStaffId(), "STAFF", complaintId, "New Assignment",
                    "Complaint " + cmp.getTrackingId() + " has been assigned to you.");
        }

        return true;
    }

    public boolean escalateComplaint(String complaintId, EscalationLevel newLevel, String userId, String userName, String reason) {
        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp == null) return false;

        cmp.setEscalationLevel(newLevel);
        cmp.setStatus(ComplaintStatus.ESCALATED);
        cmp.setDynamicPriorityScore(priorityEngine.calculateDynamicScore(cmp));
        cmp.setUpdatedTimestamp(System.currentTimeMillis());

        complaintRepo.update(cmp);
        recordHistory(complaintId, "ESCALATION_TRIGGERED", cmp.getStatus(), ComplaintStatus.ESCALATED,
                cmp.getPriority(), cmp.getPriority(), userId, userName, "Escalated to " + newLevel.getDescription() + ". Reason: " + reason);

        notificationManager.notify(cmp.getCustomerId(), "CUSTOMER", complaintId, "Complaint Escalated",
                "Complaint " + cmp.getTrackingId() + " has been escalated to " + newLevel.getDescription() + " for priority handling.");
        notificationManager.notify("ORG_ADMIN", "ADMIN", complaintId, "Complaint Escalation Alert",
                "Complaint " + cmp.getTrackingId() + " has reached " + newLevel.getDescription() + ".");

        return true;
    }

    public boolean reopenComplaint(String complaintId, String customerId, String customerName, String reason) {
        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp == null) return false;

        ComplaintStatus old = cmp.getStatus();
        cmp.setStatus(ComplaintStatus.REOPENED);
        cmp.setReopenCount(cmp.getReopenCount() + 1);
        if (cmp.getReopenCount() >= 2 && cmp.getEscalationLevel() == EscalationLevel.LEVEL_0) {
            cmp.setEscalationLevel(EscalationLevel.LEVEL_1);
        }
        cmp.setDynamicPriorityScore(priorityEngine.calculateDynamicScore(cmp));
        cmp.setUpdatedTimestamp(System.currentTimeMillis());

        complaintRepo.update(cmp);
        recordHistory(complaintId, "COMPLAINT_REOPENED", old, ComplaintStatus.REOPENED,
                cmp.getPriority(), cmp.getPriority(), customerId, customerName, "Reopened by customer: " + reason);

        notificationManager.notify("ORG_ADMIN", "ADMIN", complaintId, "Complaint Reopened",
                "Complaint " + cmp.getTrackingId() + " has been reopened by customer " + customerName + ".");

        return true;
    }

    public void addResponse(String complaintId, String senderId, String senderName, UserRole role, String message, boolean isInternalNote) {
        String respId = IdGenerator.generateResponseId();
        ComplaintResponse resp = new ComplaintResponse(respId, complaintId, senderId, senderName, role, message, System.currentTimeMillis(), isInternalNote);
        responseRepo.add(resp);

        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp != null) {
            cmp.setUpdatedTimestamp(System.currentTimeMillis());
            complaintRepo.update(cmp);

            if (!isInternalNote) {
                if (role == UserRole.CUSTOMER) {
                    notificationManager.notify("ORG_ADMIN", "ADMIN", complaintId, "Customer Response Received",
                            "Customer " + senderName + " replied on " + cmp.getTrackingId() + ".");
                } else {
                    notificationManager.notify(cmp.getCustomerId(), "CUSTOMER", complaintId, "Organization Response",
                            "New response from " + senderName + " on your complaint " + cmp.getTrackingId() + ".");
                }
            }
        }
    }

    public void submitFeedback(String complaintId, String customerId, int rating, String comments, boolean satisfied) {
        String fid = IdGenerator.generateFeedbackId();
        Feedback f = new Feedback(fid, complaintId, customerId, rating, comments, satisfied, System.currentTimeMillis());
        feedbackRepo.add(f);

        Complaint cmp = complaintRepo.findById(complaintId);
        if (cmp != null) {
            notificationManager.notify("ORG_ADMIN", "ADMIN", complaintId, "Customer Feedback Received",
                    "Rating " + rating + "/5 submitted for complaint " + cmp.getTrackingId() + ".");
        }
    }

    private void recordHistory(String complaintId, String action, ComplaintStatus prevStatus, ComplaintStatus newStatus,
                               PriorityLevel prevPrio, PriorityLevel newPrio, String userId, String userName, String comment) {
        String hid = IdGenerator.generateHistoryId();
        ComplaintHistory h = new ComplaintHistory(hid, complaintId, action, prevStatus, newStatus, prevPrio, newPrio, userId, userName, System.currentTimeMillis(), comment);
        historyRepo.add(h);
    }
}
