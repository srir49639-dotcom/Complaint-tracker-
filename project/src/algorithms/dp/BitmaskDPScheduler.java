package algorithms.dp;

import datastructures.CustomArrayList;
import java.util.Arrays;

public class BitmaskDPScheduler {

    public static class ScheduleResult {
        public final CustomArrayList<Integer> optimalOrder;
        public final double totalCost;

        public ScheduleResult(CustomArrayList<Integer> optimalOrder, double totalCost) {
            this.optimalOrder = optimalOrder;
            this.totalCost = totalCost;
        }
    }

    /**
     * Solves minimum cost sequence to resolve N complaints using Bitmask DP (Held-Karp).
     * @param costMatrix transition cost/time between resolving complaint i then j
     */
    public ScheduleResult solve(double[][] costMatrix) {
        if (costMatrix == null || costMatrix.length == 0) {
            return new ScheduleResult(new CustomArrayList<>(), 0.0);
        }

        int n = costMatrix.length;
        if (n == 1) {
            CustomArrayList<Integer> order = new CustomArrayList<>();
            order.add(0);
            return new ScheduleResult(order, 0.0);
        }

        int totalStates = 1 << n;
        double[][] dp = new double[totalStates][n];
        int[][] parent = new int[totalStates][n];

        for (int mask = 0; mask < totalStates; mask++) {
            Arrays.fill(dp[mask], Double.POSITIVE_INFINITY);
            Arrays.fill(parent[mask], -1);
        }

        // Base cases: starting at each complaint
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 0.0;
        }

        // DP transitions
        for (int mask = 1; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || Double.isInfinite(dp[mask][u])) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue; // v not yet visited

                    int nextMask = mask | (1 << v);
                    double nextCost = dp[mask][u] + costMatrix[u][v];

                    if (nextCost < dp[nextMask][v]) {
                        dp[nextMask][v] = nextCost;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        // Find minimum cost ending at all visited state (1 << n) - 1
        int finalMask = totalStates - 1;
        int bestLast = -1;
        double minTotal = Double.POSITIVE_INFINITY;

        for (int i = 0; i < n; i++) {
            if (dp[finalMask][i] < minTotal) {
                minTotal = dp[finalMask][i];
                bestLast = i;
            }
        }

        // Reconstruct path
        CustomArrayList<Integer> path = new CustomArrayList<>();
        if (bestLast != -1) {
            int currMask = finalMask;
            int currNode = bestLast;

            int[] rev = new int[n];
            int idx = n - 1;

            while (currNode != -1 && currMask > 0) {
                rev[idx--] = currNode;
                int p = parent[currMask][currNode];
                currMask = currMask ^ (1 << currNode);
                currNode = p;
            }

            for (int i = 0; i < n; i++) {
                path.add(rev[i]);
            }
        }

        return new ScheduleResult(path, minTotal);
    }

    public String getName() {
        return "Bitmask Dynamic Programming (Held-Karp TSP)";
    }

    public String getTimeComplexity() {
        return "O(2^N * N^2)";
    }

    public String getSpaceComplexity() {
        return "O(2^N * N)";
    }
}
