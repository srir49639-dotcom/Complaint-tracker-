package algorithms.randomized;

import interfaces.SortAlgorithm;
import java.util.Comparator;
import java.util.Random;

public class RandomizedQuickSort<T> implements SortAlgorithm<T> {

    private final Random random = new Random();

    @Override
    public void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length <= 1) return;
        quickSort(array, 0, array.length - 1, comparator);
    }

    private void quickSort(T[] a, int low, int high, Comparator<T> comp) {
        if (low < high) {
            int pi = randomizedPartition(a, low, high, comp);
            quickSort(a, low, pi - 1, comp);
            quickSort(a, pi + 1, high, comp);
        }
    }

    private int randomizedPartition(T[] a, int low, int high, Comparator<T> comp) {
        int randomIndex = low + random.nextInt(high - low + 1);
        swap(a, randomIndex, high);
        return partition(a, low, high, comp);
    }

    private int partition(T[] a, int low, int high, Comparator<T> comp) {
        T pivot = a[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(a[j], pivot) <= 0) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, high);
        return i + 1;
    }

    private void swap(T[] a, int i, int j) {
        T temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    @Override
    public String getName() {
        return "Randomized QuickSort";
    }

    @Override
    public String getTimeComplexity() {
        return "Expected O(N log N), Worst O(N^2) with negligible probability";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(log N)";
    }
}
