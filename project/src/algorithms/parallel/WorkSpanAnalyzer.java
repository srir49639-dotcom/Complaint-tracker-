package algorithms.parallel;

public class WorkSpanAnalyzer {

    public static class WorkSpanMetrics {
        public final String algorithmName;
        public final long n;
        public final double workT1;         // Total sequential operations
        public final double spanTinf;       // Longest critical dependency path
        public final double parallelism;    // T1 / Tinf (maximum theoretical speedup)
        public final double estimatedSpeedupP4; // Speedup with P=4 cores
        public final double estimatedSpeedupP8; // Speedup with P=8 cores

        public WorkSpanMetrics(String algorithmName, long n, double workT1, double spanTinf) {
            this.algorithmName = algorithmName;
            this.n = n;
            this.workT1 = workT1;
            this.spanTinf = spanTinf;
            this.parallelism = (spanTinf > 0) ? (workT1 / spanTinf) : 1.0;
            // Brent's Theorem: Tp <= T1 / P + Tinf  => Speedup = T1 / Tp
            this.estimatedSpeedupP4 = workT1 / (workT1 / 4.0 + spanTinf);
            this.estimatedSpeedupP8 = workT1 / (workT1 / 8.0 + spanTinf);
        }
    }

    public static WorkSpanMetrics analyzeParallelPrefixSum(long n) {
        double work = 2.0 * n; // 2 passes
        double span = 2.0 * (Math.log(n) / Math.log(2));
        return new WorkSpanMetrics("Parallel Prefix Sum", n, work, span);
    }

    public static WorkSpanMetrics analyzeParallelReduction(long n) {
        double work = n;
        double span = Math.log(n) / Math.log(2);
        return new WorkSpanMetrics("Parallel Reduction", n, work, span);
    }

    public static WorkSpanMetrics analyzeParallelMergeSort(long n) {
        double work = n * (Math.log(n) / Math.log(2));
        double span = Math.pow(Math.log(n) / Math.log(2), 2);
        return new WorkSpanMetrics("Parallel MergeSort", n, work, span);
    }
}
