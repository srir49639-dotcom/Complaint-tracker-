package services;

import algorithms.flow.BipartiteMatchingAssignment;
import model.Complaint;
import model.Department;
import model.Staff;
import repository.DepartmentRepository;
import repository.StaffRepository;
import datastructures.CustomArrayList;
import datastructures.CustomPair;

public class AssignmentEngineService {

    private static AssignmentEngineService instance;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;
    private final BipartiteMatchingAssignment flowAssignment;

    public AssignmentEngineService() {
        this.departmentRepository = DepartmentRepository.getInstance();
        this.staffRepository = StaffRepository.getInstance();
        this.flowAssignment = new BipartiteMatchingAssignment();
    }

    public static synchronized AssignmentEngineService getInstance() {
        if (instance == null) {
            instance = new AssignmentEngineService();
        }
        return instance;
    }

    public static class Recommendation {
        public final Department recommendedDepartment;
        public final Staff recommendedStaff;
        public final String reasoning;
        public final boolean highConfidence;

        public Recommendation(Department recommendedDepartment, Staff recommendedStaff, String reasoning, boolean highConfidence) {
            this.recommendedDepartment = recommendedDepartment;
            this.recommendedStaff = recommendedStaff;
            this.reasoning = reasoning;
            this.highConfidence = highConfidence;
        }
    }

    /**
     * Recommends the best department and staff member for a complaint based on
     * category matching, specialization, workload balance, and availability.
     */
    public Recommendation recommendAssignment(Complaint complaint) {
        if (complaint == null) return null;

        Department targetDept = null;
        CustomArrayList<Department> allDepts = departmentRepository.findAll();

        // 1. Match department by category or name
        for (int i = 0; i < allDepts.size(); i++) {
            Department d = allDepts.get(i);
            if (d.getName().equalsIgnoreCase(complaint.getCategory()) ||
                    (complaint.getDepartmentName() != null && d.getName().equalsIgnoreCase(complaint.getDepartmentName()))) {
                targetDept = d;
                break;
            }
        }

        if (targetDept == null && allDepts.size() > 0) {
            targetDept = allDepts.get(0);
        }

        // 2. Find eligible staff members
        CustomArrayList<Staff> deptStaff = (targetDept != null) ?
                staffRepository.findByDepartmentName(targetDept.getName()) :
                staffRepository.findAll();

        Staff bestStaff = null;
        int minWorkload = Integer.MAX_VALUE;
        double maxEfficiency = -1.0;

        for (int i = 0; i < deptStaff.size(); i++) {
            Staff s = deptStaff.get(i);
            if (s.canAcceptComplaint()) {
                // Prioritize lower workload then higher efficiency
                if (s.getCurrentWorkload() < minWorkload ||
                        (s.getCurrentWorkload() == minWorkload && s.getEfficiencyRating() > maxEfficiency)) {
                    minWorkload = s.getCurrentWorkload();
                    maxEfficiency = s.getEfficiencyRating();
                    bestStaff = s;
                }
            }
        }

        String reason = (bestStaff != null) ?
                "Matches category '" + (targetDept != null ? targetDept.getName() : "General") +
                        "' and has lowest current workload (" + bestStaff.getCurrentWorkload() + "/" + bestStaff.getMaxWorkload() + ")." :
                "Department capacity available; awaiting available staff allocation.";

        return new Recommendation(targetDept, bestStaff, reason, bestStaff != null);
    }

    /**
     * Executes bulk bipartite flow capacity assignment for unassigned complaints.
     */
    public BipartiteMatchingAssignment.AssignmentResult runBatchAssignment(CustomArrayList<Complaint> unassigned) {
        CustomArrayList<Staff> allStaff = staffRepository.findAll();
        return flowAssignment.matchComplaintsToStaff(unassigned, allStaff);
    }
}
