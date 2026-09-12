package datastructures;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;

    public CustomArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = DEFAULT_CAPACITY;
        this.elements = new Object[Math.max(DEFAULT_CAPACITY, initialCapacity)];
        this.size = 0;
    }

    public void add(T item) {
        ensureCapacity(size + 1);
        elements[size++] = item;
    }

    public void add(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T item) {
        checkIndex(index);
        T old = (T) elements[index];
        elements[index] = item;
        return old;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T item = (T) elements[index];
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;
        return item;
    }

    public boolean remove(T item) {
        int index = indexOf(item);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    public int indexOf(T item) {
        if (item == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (item.equals(elements[i])) return i;
            }
        }
        return -1;
    }

    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    public void addAll(CustomArrayList<T> other) {
        if (other == null || other.isEmpty()) return;
        ensureCapacity(size + other.size);
        for (int i = 0; i < other.size; i++) {
            elements[size++] = other.get(i);
        }
    }

    @SuppressWarnings("unchecked")
    public void sort(Comparator<T> comparator) {
        // In-place QuickSort on custom array
        quickSort(0, size - 1, comparator);
    }

    @SuppressWarnings("unchecked")
    private void quickSort(int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pi = partition(low, high, comparator);
            quickSort(low, pi - 1, comparator);
            quickSort(pi + 1, high, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    private int partition(int low, int high, Comparator<T> comparator) {
        T pivot = (T) elements[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare((T) elements[j], pivot) <= 0) {
                i++;
                Object temp = elements[i];
                elements[i] = elements[j];
                elements[j] = temp;
            }
        }
        Object temp = elements[i + 1];
        elements[i + 1] = elements[high];
        elements[high] = temp;
        return i + 1;
    }

    private void ensureCapacity(int minCap) {
        if (minCap > elements.length) {
            int newCap = Math.max(elements.length * 2, minCap);
            elements = Arrays.copyOf(elements, newCap);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[cursor++];
            }
        };
    }
}
