package algorithms.parallel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort {

    private static final int THRESHOLD = 500;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    private static class SortTask<T> extends RecursiveAction {
        final T[] array;
        final T[] helper;
        final int low, high;
        final Comparator<T> comparator;

        SortTask(T[] array, T[] helper, int low, int high, Comparator<T> comparator) {
            this.array = array;
            this.helper = helper;
            this.low = low;
            this.high = high;
            this.comparator = comparator;
        }

        @Override
        protected void compute() {
            if (high - low <= THRESHOLD) {
                Arrays.sort(array, low, high + 1, comparator);
                return;
            }

            int mid = low + (high - low) / 2;
            SortTask<T> left = new SortTask<>(array, helper, low, mid, comparator);
            SortTask<T> right = new SortTask<>(array, helper, mid + 1, high, comparator);

            invokeAll(left, right);
            merge(low, mid, high);
        }

        private void merge(int low, int mid, int high) {
            for (int i = low; i <= high; i++) {
                helper[i] = array[i];
            }

            int i = low;
            int j = mid + 1;
            int k = low;

            while (i <= mid && j <= high) {
                if (comparator.compare(helper[i], helper[j]) <= 0) {
                    array[k++] = helper[i++];
                } else {
                    array[k++] = helper[j++];
                }
            }

            while (i <= mid) {
                array[k++] = helper[i++];
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length <= 1) return;
        T[] helper = (T[]) new Object[array.length];
        POOL.invoke(new SortTask<>(array, helper, 0, array.length - 1, comparator));
    }

    public String getName() {
        return "Parallel MergeSort (ForkJoin)";
    }

    public String getTimeComplexity() {
        return "Work: O(N log N), Span: O(log^2 N) or O(N) merge";
    }
}
