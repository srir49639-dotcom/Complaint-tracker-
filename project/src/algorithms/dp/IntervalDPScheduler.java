package algorithms.dp;

import datastructures.CustomArrayList;

public class IntervalDPScheduler {

    /**
     * Solves optimal order of merging or processing stages of interrelated complaint resolution tasks.
     * Analogous to Matrix Chain Multiplication / optimal interval partitioning.
     * @param stageWeights costs of stage boundaries
     * @return minimum total merge/transition cost
     */
    public long computeOptimalIntervalCost(int[] stageWeights) {
        if (stageWeights == null || stageWeights.length < 2) return 0;

        int n = stageWeights.length - 1; // number of stages
        long[][] dp = new long[n + 1][n + 1];

        // L is chain length
        for (int L = 2; L <= n; L++) {
            for (int i = 1; i <= n - L + 1; i++) {
                int j = i + L - 1;
                dp[i][j] = Long.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (long) stageWeights[i - 1] * stageWeights[k] * stageWeights[j];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                    }
                }
            }
        }
        return dp[1][n];
    }

    public String getName() {
        return "Interval Dynamic Programming (Stage Optimization)";
    }

    public String getTimeComplexity() {
        return "O(N^3)";
    }

    public String getSpaceComplexity() {
        return "O(N^2)";
    }
}
