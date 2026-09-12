package algorithms.dp;

public class NeedlemanWunschDemo {

    public static class AlignmentResult {
        public final String alignedSeq1;
        public final String alignedSeq2;
        public final int score;

        public AlignmentResult(String alignedSeq1, String alignedSeq2, int score) {
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.score = score;
        }
    }

    public AlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= n; j++) dp[0][j] = j * gapPenalty;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int match = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int delete = dp[i - 1][j] + gapPenalty;
                int insert = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(match, Math.max(delete, insert));
            }
        }

        // Traceback
        StringBuilder a1 = new StringBuilder();
        StringBuilder a2 = new StringBuilder();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                a1.append(seq1.charAt(i - 1));
                a2.append(seq2.charAt(j - 1));
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                a1.append(seq1.charAt(i - 1));
                a2.append('-');
                i--;
            } else {
                a1.append('-');
                a2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(a1.reverse().toString(), a2.reverse().toString(), dp[m][n]);
    }
}
