package algorithms.approximation;

import datastructures.CustomArrayList;
import java.util.Arrays;

public class TSPApproximation {

    public static class TSPResult {
        public final CustomArrayList<Integer> tour;
        public final double totalDistance;
        public final String approxFactor;

        public TSPResult(CustomArrayList<Integer> tour, double totalDistance, String approxFactor) {
            this.tour = tour;
            this.totalDistance = totalDistance;
            this.approxFactor = approxFactor;
        }
    }

    /**
     * 2-Approximation for Metric TSP via Minimum Spanning Tree (MST) doubling and Eulerian tour shortcutting.
     * @param distMatrix symmetric distance matrix satisfying triangle inequality
     */
    public TSPResult solveMetricTSP2Approx(double[][] distMatrix) {
        int n = distMatrix.length;
        if (n == 0) return new TSPResult(new CustomArrayList<>(), 0.0, "2.0-Approx");
        if (n == 1) {
            CustomArrayList<Integer> t = new CustomArrayList<>();
            t.add(0);
            return new TSPResult(t, 0.0, "Exact");
        }

        // 1. Prim's MST
        int[] parent = new int[n];
        double[] key = new double[n];
        boolean[] inMST = new boolean[n];
        Arrays.fill(key, Double.MAX_VALUE);
        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < n - 1; count++) {
            int u = -1;
            double minK = Double.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && key[v] < minK) {
                    minK = key[v];
                    u = v;
                }
            }

            inMST[u] = true;

            for (int v = 0; v < n; v++) {
                if (distMatrix[u][v] > 0 && !inMST[v] && distMatrix[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = distMatrix[u][v];
                }
            }
        }

        // 2. Build adjacency for MST
        CustomArrayList<CustomArrayList<Integer>> mstAdj = new CustomArrayList<>(n);
        for (int i = 0; i < n; i++) mstAdj.add(new CustomArrayList<>());

        for (int i = 1; i < n; i++) {
            mstAdj.get(parent[i]).add(i);
            mstAdj.get(i).add(parent[i]);
        }

        // 3. Preorder DFS traversal for shortcutting
        boolean[] visited = new boolean[n];
        CustomArrayList<Integer> tour = new CustomArrayList<>();
        dfsTour(0, mstAdj, visited, tour);
        tour.add(0); // Return to start

        // Calculate tour distance
        double totalDist = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDist += distMatrix[tour.get(i)][tour.get(i + 1)];
        }

        return new TSPResult(tour, totalDist, "2.0-Approximation (MST Shortcut)");
    }

    private void dfsTour(int u, CustomArrayList<CustomArrayList<Integer>> adj, boolean[] visited, CustomArrayList<Integer> tour) {
        visited[u] = true;
        tour.add(u);

        CustomArrayList<Integer> neighbors = adj.get(u);
        for (int i = 0; i < neighbors.size(); i++) {
            int v = neighbors.get(i);
            if (!visited[v]) {
                dfsTour(v, adj, visited, tour);
            }
        }
    }

    public String getName() {
        return "Metric TSP 2-Approximation (MST Shortcut)";
    }
}
