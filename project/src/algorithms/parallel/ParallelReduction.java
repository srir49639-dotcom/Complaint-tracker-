package algorithms.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelReduction {

    private static final int THRESHOLD = 500;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public static class Metrics {
        public final long count;
        public final double sum;
        public final double min;
        public final double max;
        public final double average;

        public Metrics(long count, double sum, double min, double max, double average) {
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.average = average;
        }

        public static Metrics combine(Metrics a, Metrics b) {
            long totalCount = a.count + b.count;
            double totalSum = a.sum + b.sum;
            double minVal = Math.min(a.min, b.min);
            double maxVal = Math.max(a.max, b.max);
            double avg = (totalCount > 0) ? (totalSum / totalCount) : 0.0;
            return new Metrics(totalCount, totalSum, minVal, maxVal, avg);
        }
    }

    private static class ReductionTask extends RecursiveTask<Metrics> {
        final double[] data;
        final int low, high;

        ReductionTask(double[] data, int low, int high) {
            this.data = data;
            this.low = low;
            this.high = high;
        }

        @Override
        protected Metrics compute() {
            if (high - low <= THRESHOLD) {
                double sum = 0;
                double min = Double.MAX_VALUE;
                double max = -Double.MAX_VALUE;
                for (int i = low; i < high; i++) {
                    double v = data[i];
                    sum += v;
                    if (v < min) min = v;
                    if (v > max) max = v;
                }
                long cnt = high - low;
                return new Metrics(cnt, sum, cnt > 0 ? min : 0, cnt > 0 ? max : 0, cnt > 0 ? (sum / cnt) : 0);
            }

            int mid = low + (high - low) / 2;
            ReductionTask leftTask = new ReductionTask(data, low, mid);
            ReductionTask rightTask = new ReductionTask(data, mid, high);

            leftTask.fork();
            Metrics rightMetrics = rightTask.compute();
            Metrics leftMetrics = leftTask.join();

            return Metrics.combine(leftMetrics, rightMetrics);
        }
    }

    /**
     * Executes parallel reduction over data array to calculate aggregate metrics in parallel.
     */
    public Metrics computeMetrics(double[] data) {
        if (data == null || data.length == 0) {
            return new Metrics(0, 0, 0, 0, 0);
        }
        return POOL.invoke(new ReductionTask(data, 0, data.length));
    }

    public String getName() {
        return "Parallel Reduction (ForkJoin)";
    }

    public String getTimeComplexity() {
        return "Work: O(N), Span (Depth): O(log N)";
    }
}
