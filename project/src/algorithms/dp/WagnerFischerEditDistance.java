package algorithms.dp;

import datastructures.CustomArrayList;

public class WagnerFischerEditDistance {

    /**
     * Calculates the Levenshtein edit distance between s1 and s2 using Wagner-Fischer DP.
     */
    public int computeDistance(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null || s1.isEmpty()) return s2 != null ? s2.length() : 0;
        if (s2 == null || s2.isEmpty()) return s1.length();

        String a = s1.toLowerCase();
        String b = s2.toLowerCase();
        int m = a.length();
        int n = b.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        dp[i - 1][j] + 1,       // Deletion
                        Math.min(
                                dp[i][j - 1] + 1,   // Insertion
                                dp[i - 1][j - 1] + cost // Substitution
                        )
                );
            }
        }
        return dp[m][n];
    }

    /**
     * Calculates normalized similarity score between 0.0 (completely distinct) and 1.0 (identical).
     */
    public double computeSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        int dist = computeDistance(s1, s2);
        return 1.0 - ((double) dist / maxLen);
    }

    /**
     * Finds the closest dictionary match within a maximum distance threshold.
     */
    public String findClosestMatch(String input, CustomArrayList<String> dictionary, int maxDistance) {
        if (input == null || dictionary == null || dictionary.isEmpty()) return null;
        String bestMatch = null;
        int minDistance = maxDistance + 1;

        for (String word : dictionary) {
            int dist = computeDistance(input, word);
            if (dist < minDistance) {
                minDistance = dist;
                bestMatch = word;
            }
        }
        return (minDistance <= maxDistance) ? bestMatch : null;
    }

    public String getName() {
        return "Wagner-Fischer Dynamic Programming (Edit Distance)";
    }

    public String getTimeComplexity() {
        return "O(M * N)";
    }

    public String getSpaceComplexity() {
        return "O(M * N)";
    }
}
