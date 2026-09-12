package algorithms.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ParallelPrefixSum {

    private static final int THRESHOLD = 1000;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    private static class Node {
        final int low, high;
        long sum;
        long fromLeft;
        Node left, right;

        Node(int low, int high) {
            this.low = low;
            this.high = high;
        }
    }

    private static class BuildTreeTask extends RecursiveTask<Node> {
        final long[] array;
        final int low, high;

        BuildTreeTask(long[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected Node compute() {
            Node node = new Node(low, high);
            if (high - low <= THRESHOLD) {
                long s = 0;
                for (int i = low; i < high; i++) {
                    s += array[i];
                }
                node.sum = s;
                return node;
            }

            int mid = low + (high - low) / 2;
            BuildTreeTask leftTask = new BuildTreeTask(array, low, mid);
            BuildTreeTask rightTask = new BuildTreeTask(array, mid, high);

            leftTask.fork();
            node.right = rightTask.compute();
            node.left = leftTask.join();
            node.sum = node.left.sum + node.right.sum;
            return node;
        }
    }

    private static class DownPassTask extends RecursiveAction {
        final Node node;
        final long fromLeft;
        final long[] array;
        final long[] prefixSum;

        DownPassTask(Node node, long fromLeft, long[] array, long[] prefixSum) {
            this.node = node;
            this.fromLeft = fromLeft;
            this.array = array;
            this.prefixSum = prefixSum;
        }

        @Override
        protected void compute() {
            if (node.left == null) {
                long running = fromLeft;
                for (int i = node.low; i < node.high; i++) {
                    running += array[i];
                    prefixSum[i] = running;
                }
                return;
            }

            DownPassTask leftTask = new DownPassTask(node.left, fromLeft, array, prefixSum);
            DownPassTask rightTask = new DownPassTask(node.right, fromLeft + node.left.sum, array, prefixSum);

            leftTask.fork();
            rightTask.compute();
            leftTask.join();
        }
    }

    /**
     * Computes parallel prefix sum (inclusive scan) using a two-pass parallel tree algorithm.
     * Work W = O(N), Span S = O(log N).
     */
    public long[] computePrefixSum(long[] array) {
        if (array == null || array.length == 0) return new long[0];
        long[] result = new long[array.length];

        if (array.length <= THRESHOLD) {
            long running = 0;
            for (int i = 0; i < array.length; i++) {
                running += array[i];
                result[i] = running;
            }
            return result;
        }

        Node root = POOL.invoke(new BuildTreeTask(array, 0, array.length));
        POOL.invoke(new DownPassTask(root, 0, array, result));
        return result;
    }

    public String getName() {
        return "Parallel Prefix Sum (Parallel Scan)";
    }

    public String getTimeComplexity() {
        return "Work: O(N), Span (Depth): O(log N), Parallelism: O(N / log N)";
    }
}
