package algorithms.dp;

public class MatrixChainMultDemo {

    public static class MCMResult {
        public final int minScalarMultiplications;
        public final String parenthesization;

        public MCMResult(int minScalarMultiplications, String parenthesization) {
            this.minScalarMultiplications = minScalarMultiplications;
            this.parenthesization = parenthesization;
        }
    }

    public MCMResult compute(int[] dims) {
        if (dims == null || dims.length < 2) return new MCMResult(0, "");
        int n = dims.length - 1;
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
                    int q = m[i][k] + m[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        buildOptimalParens(s, 1, n, sb);
        return new MCMResult(m[1][n], sb.toString());
    }

    private void buildOptimalParens(int[][] s, int i, int j, StringBuilder sb) {
        if (i == j) {
            sb.append("A").append(i);
        } else {
            sb.append("(");
            buildOptimalParens(s, i, s[i][j], sb);
            buildOptimalParens(s, s[i][j] + 1, j, sb);
            sb.append(")");
        }
    }
}
