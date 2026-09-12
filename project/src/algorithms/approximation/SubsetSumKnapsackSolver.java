package algorithms.approximation;

import datastructures.CustomArrayList;

public class SubsetSumKnapsackSolver {

    public static class KnapsackResult {
        public final int maxValue;
        public final CustomArrayList<Integer> selectedItemIndices;

        public KnapsackResult(int maxValue, CustomArrayList<Integer> selectedItemIndices) {
            this.maxValue = maxValue;
            this.selectedItemIndices = selectedItemIndices;
        }
    }

    /**
     * Solves 0/1 Knapsack problem for complaint budget/effort allocation using Dynamic Programming.
     */
    public KnapsackResult solve01Knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(values[i - 1] + dp[i - 1][w - weights[i - 1]], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Traceback selected items
        CustomArrayList<Integer> items = new CustomArrayList<>();
        int res = dp[n][capacity];
        int w = capacity;
        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][w]) {
                items.add(i - 1);
                res -= values[i - 1];
                w -= weights[i - 1];
            }
        }

        return new KnapsackResult(dp[n][capacity], items);
    }

    /**
     * Solves Subset Sum decision problem using Dynamic Programming.
     */
    public boolean hasSubsetSum(int[] set, int targetSum) {
        int n = set.length;
        boolean[][] dp = new boolean[n + 1][targetSum + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= targetSum; j++) {
                if (j < set[i - 1]) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - set[i - 1]];
                }
            }
        }

        return dp[n][targetSum];
    }
}
