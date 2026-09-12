package algorithms.dp;

public class SmithWatermanDemo {

    public static class LocalAlignmentResult {
        public final int maxScore;
        public final int startI;
        public final int startJ;

        public LocalAlignmentResult(int maxScore, int startI, int startJ) {
            this.maxScore = maxScore;
            this.startI = startI;
            this.startJ = startJ;
        }
    }

    public LocalAlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        int maxScore = 0;
        int bestI = 0, bestJ = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int match = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int delete = dp[i - 1][j] + gapPenalty;
                int insert = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(0, Math.max(match, Math.max(delete, insert)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        return new LocalAlignmentResult(maxScore, bestI, bestJ);
    }
}
