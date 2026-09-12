package algorithms.dp;

import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

public class TreeDPHierarchyAnalyzer {

    public static class TreeNode {
        public final String id;
        public final int workloadWeight;
        public final CustomArrayList<TreeNode> children;

        public TreeNode(String id, int workloadWeight) {
            this.id = id;
            this.workloadWeight = workloadWeight;
            this.children = new CustomArrayList<>();
        }
    }

    public static class TreeDPResult {
        public final int maxDirectImpact;
        public final int maxIndirectImpact;
        public final int optimalWorkloadSelected;

        public TreeDPResult(int maxDirectImpact, int maxIndirectImpact, int optimalWorkloadSelected) {
            this.maxDirectImpact = maxDirectImpact;
            this.maxIndirectImpact = maxIndirectImpact;
            this.optimalWorkloadSelected = optimalWorkloadSelected;
        }
    }

    private final CustomHashTable<String, Integer> dpInclude = new CustomHashTable<>(31);
    private final CustomHashTable<String, Integer> dpExclude = new CustomHashTable<>(31);

    /**
     * Executes Tree DP (Maximum Weight Independent Set on organizational hierarchy tree).
     */
    public TreeDPResult analyzeHierarchy(TreeNode root) {
        if (root == null) return new TreeDPResult(0, 0, 0);

        dpInclude.clear();
        dpExclude.clear();

        computeTreeDP(root);

        int incl = dpInclude.get(root.id);
        int excl = dpExclude.get(root.id);
        int optimal = Math.max(incl, excl);

        return new TreeDPResult(incl, excl, optimal);
    }

    private void computeTreeDP(TreeNode u) {
        int inc = u.workloadWeight;
        int exc = 0;

        for (TreeNode v : u.children) {
            computeTreeDP(v);
            inc += dpExclude.get(v.id);
            exc += Math.max(dpInclude.get(v.id), dpExclude.get(v.id));
        }

        dpInclude.put(u.id, inc);
        dpExclude.put(u.id, exc);
    }

    public String getName() {
        return "Tree Dynamic Programming (Hierarchy Impact Analyzer)";
    }

    public String getTimeComplexity() {
        return "O(V + E) on Trees";
    }

    public String getSpaceComplexity() {
        return "O(V)";
    }
}
