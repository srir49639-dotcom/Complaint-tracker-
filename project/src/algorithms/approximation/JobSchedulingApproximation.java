package algorithms.approximation;

import model.Complaint;
import model.Staff;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomPriorityQueue;
import java.util.Arrays;
import java.util.Comparator;

public class JobSchedulingApproximation {

    public static class ScheduleOutput {
        public final CustomHashTable<String, CustomArrayList<Complaint>> staffAssignments;
        public final double makespanHours;
        public final String solverType; // "Exact DP/Branch-and-Bound" or "LPT 4/3-Approximation"
        public final String qualityAssessment; // "Optimal" or "Within 4/3 of Optimal"

        public ScheduleOutput(CustomHashTable<String, CustomArrayList<Complaint>> staffAssignments,
                              double makespanHours, String solverType, String qualityAssessment) {
            this.staffAssignments = staffAssignments;
            this.makespanHours = makespanHours;
            this.solverType = solverType;
            this.qualityAssessment = qualityAssessment;
        }
    }

    private static class StaffLoad implements Comparable<StaffLoad> {
        final Staff staff;
        double totalHours;
        final CustomArrayList<Complaint> assigned;

        StaffLoad(Staff staff) {
            this.staff = staff;
            this.totalHours = 0;
            this.assigned = new CustomArrayList<>();
        }

        @Override
        public int compareTo(StaffLoad o) {
            return Double.compare(this.totalHours, o.totalHours);
        }
    }

    /**
     * Schedules complaints onto staff to minimize maximum completion time (Makespan problem).
     */
    public ScheduleOutput schedule(CustomArrayList<Complaint> complaints, CustomArrayList<Staff> staffList) {
        if (complaints == null || complaints.isEmpty() || staffList == null || staffList.isEmpty()) {
            return new ScheduleOutput(new CustomHashTable<>(), 0.0, "Empty", "N/A");
        }

        int n = complaints.size();
        int m = staffList.size();

        // If dataset is small (N <= 12, M <= 4), use exact Branch-and-Bound / DP search
        if (n <= 12 && m <= 4) {
            return solveExactBranchAndBound(complaints, staffList);
        } else {
            return solveLPTApproximation(complaints, staffList);
        }
    }

    /**
     * Longest Processing Time (LPT) greedy 4/3-approximation algorithm for makespan scheduling.
     */
    private ScheduleOutput solveLPTApproximation(CustomArrayList<Complaint> complaints, CustomArrayList<Staff> staffList) {
        // 1. Sort complaints in descending order of effort hours
        CustomArrayList<Complaint> sorted = new CustomArrayList<>();
        sorted.addAll(complaints);
        sorted.sort((c1, c2) -> Integer.compare(c2.getEstimatedEffortHours(), c1.getEstimatedEffortHours()));

        // 2. Min-Heap of staff loads
        CustomPriorityQueue<StaffLoad> minHeap = new CustomPriorityQueue<>(staffList.size(), null);
        for (int i = 0; i < staffList.size(); i++) {
            minHeap.offer(new StaffLoad(staffList.get(i)));
        }

        // 3. Assign each job to currently least-loaded machine/staff
        for (int i = 0; i < sorted.size(); i++) {
            Complaint c = sorted.get(i);
            StaffLoad least = minHeap.poll();
            least.assigned.add(c);
            least.totalHours += c.getEstimatedEffortHours();
            minHeap.offer(least);
        }

        CustomHashTable<String, CustomArrayList<Complaint>> resultTable = new CustomHashTable<>(staffList.size() * 2);
        double maxMakespan = 0.0;

        CustomArrayList<StaffLoad> allLoads = minHeap.toList();
        for (int i = 0; i < allLoads.size(); i++) {
            StaffLoad sl = allLoads.get(i);
            resultTable.put(sl.staff.getStaffId(), sl.assigned);
            if (sl.totalHours > maxMakespan) {
                maxMakespan = sl.totalHours;
            }
        }

        return new ScheduleOutput(resultTable, maxMakespan, "LPT Approximation", "Guaranteed <= 4/3 * Optimal");
    }

    private ScheduleOutput solveExactBranchAndBound(CustomArrayList<Complaint> complaints, CustomArrayList<Staff> staffList) {
        int n = complaints.size();
        int m = staffList.size();
        int[] jobs = new int[n];
        for (int i = 0; i < n; i++) jobs[i] = complaints.get(i).getEstimatedEffortHours();

        double[] machineLoads = new double[m];
        int[] bestAssignment = new int[n];
        int[] currentAssignment = new int[n];
        double[] bestMakespan = new double[]{Double.MAX_VALUE};

        branchAndBoundHelper(jobs, 0, machineLoads, currentAssignment, bestAssignment, bestMakespan);

        CustomHashTable<String, CustomArrayList<Complaint>> resultTable = new CustomHashTable<>(m * 2);
        for (int j = 0; j < m; j++) {
            resultTable.put(staffList.get(j).getStaffId(), new CustomArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            int staffIdx = bestAssignment[i];
            resultTable.get(staffList.get(staffIdx).getStaffId()).add(complaints.get(i));
        }

        return new ScheduleOutput(resultTable, bestMakespan[0], "Exact Branch-and-Bound", "Optimal Solution");
    }

    private void branchAndBoundHelper(int[] jobs, int jobIndex, double[] machineLoads,
                                      int[] currentAssignment, int[] bestAssignment, double[] bestMakespan) {
        if (jobIndex == jobs.length) {
            double currentMax = 0;
            for (double load : machineLoads) if (load > currentMax) currentMax = load;
            if (currentMax < bestMakespan[0]) {
                bestMakespan[0] = currentMax;
                System.arraycopy(currentAssignment, 0, bestAssignment, 0, jobs.length);
            }
            return;
        }

        for (int m = 0; m < machineLoads.length; m++) {
            if (machineLoads[m] + jobs[jobIndex] < bestMakespan[0]) {
                machineLoads[m] += jobs[jobIndex];
                currentAssignment[jobIndex] = m;

                branchAndBoundHelper(jobs, jobIndex + 1, machineLoads, currentAssignment, bestAssignment, bestMakespan);

                machineLoads[m] -= jobs[jobIndex];
            }
        }
    }

    public String getName() {
        return "Job Scheduling & Makespan Optimizer (CO5)";
    }
}
