package algorithms.flow;

import model.Complaint;
import model.Staff;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomPair;

public class BipartiteMatchingAssignment {

    public static class AssignmentResult {
        public final CustomArrayList<CustomPair<Complaint, Staff>> matchedPairs;
        public final int totalMatched;
        public final int unassignedCount;

        public AssignmentResult(CustomArrayList<CustomPair<Complaint, Staff>> matchedPairs, int totalMatched, int unassignedCount) {
            this.matchedPairs = matchedPairs;
            this.totalMatched = totalMatched;
            this.unassignedCount = unassignedCount;
        }
    }

    private final DinicAlgorithm flowSolver = new DinicAlgorithm();

    /**
     * Matches unassigned complaints to eligible available staff members under capacity constraints.
     */
    public AssignmentResult matchComplaintsToStaff(CustomArrayList<Complaint> complaints, CustomArrayList<Staff> staffList) {
        if (complaints == null || staffList == null || complaints.isEmpty() || staffList.isEmpty()) {
            return new AssignmentResult(new CustomArrayList<>(), 0, complaints != null ? complaints.size() : 0);
        }

        int numComplaints = complaints.size();
        int numStaff = staffList.size();

        // Node indexing:
        // 0: Source (s)
        // 1 to numComplaints: Complaints
        // numComplaints + 1 to numComplaints + numStaff: Staff
        // numComplaints + numStaff + 1: Sink (t)
        int totalNodes = numComplaints + numStaff + 2;
        int source = 0;
        int sink = totalNodes - 1;

        int[][] capacity = new int[totalNodes][totalNodes];

        // Edge 1: Source to each complaint with capacity 1
        for (int i = 0; i < numComplaints; i++) {
            capacity[source][1 + i] = 1;
        }

        // Edge 2: Complaint to Staff if department / specialization matches and staff is available
        for (int i = 0; i < numComplaints; i++) {
            Complaint cmp = complaints.get(i);
            for (int j = 0; j < numStaff; j++) {
                Staff st = staffList.get(j);

                boolean deptMatch = cmp.getDepartmentName() == null || cmp.getDepartmentName().isEmpty() ||
                        cmp.getDepartmentName().equalsIgnoreCase(st.getDepartmentName()) ||
                        cmp.getCategory().equalsIgnoreCase(st.getDepartmentName());

                boolean canAccept = st.canAcceptComplaint();

                if (deptMatch && canAccept) {
                    capacity[1 + i][numComplaints + 1 + j] = 1;
                }
            }
        }

        // Edge 3: Staff to Sink with capacity = remaining available capacity (maxWorkload - currentWorkload)
        for (int j = 0; j < numStaff; j++) {
            Staff st = staffList.get(j);
            int availableCapacity = Math.max(0, st.getMaxWorkload() - st.getCurrentWorkload());
            capacity[numComplaints + 1 + j][sink] = availableCapacity;
        }

        int maxFlow = flowSolver.computeMaxFlow(capacity, source, sink);
        int[][] residual = flowSolver.getResidualGraph();

        CustomArrayList<CustomPair<Complaint, Staff>> pairs = new CustomArrayList<>();

        // Reconstruct matching from saturated edges
        for (int i = 0; i < numComplaints; i++) {
            for (int j = 0; j < numStaff; j++) {
                int cNode = 1 + i;
                int sNode = numComplaints + 1 + j;
                // If original capacity was 1 and residual forward is 0 (i.e. flow pushed = 1)
                if (capacity[cNode][sNode] == 1 && residual[cNode][sNode] == 0) {
                    pairs.add(new CustomPair<>(complaints.get(i), staffList.get(j)));
                    break;
                }
            }
        }

        int unassigned = numComplaints - pairs.size();
        return new AssignmentResult(pairs, pairs.size(), unassigned);
    }

    public String getName() {
        return "Network Flow Bipartite Capacity Assignment";
    }
}
