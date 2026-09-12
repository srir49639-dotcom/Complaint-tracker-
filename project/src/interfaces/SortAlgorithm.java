package interfaces;

import java.util.Comparator;

public interface SortAlgorithm<T> {
    /**
     * Sorts the array in place using the given comparator.
     */
    void sort(T[] array, Comparator<T> comparator);

    String getName();

    String getTimeComplexity();

    String getSpaceComplexity();
}
