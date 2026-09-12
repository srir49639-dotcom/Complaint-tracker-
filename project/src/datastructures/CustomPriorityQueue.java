package datastructures;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class CustomPriorityQueue<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private Object[] queue;
    private int size;
    private final Comparator<? super T> comparator;

    public CustomPriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public CustomPriorityQueue(Comparator<? super T> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    public CustomPriorityQueue(int initialCapacity, Comparator<? super T> comparator) {
        this.queue = new Object[Math.max(1, initialCapacity)];
        this.size = 0;
        this.comparator = comparator;
    }

    public void offer(T e) {
        if (e == null) throw new NullPointerException();
        int i = size;
        if (i >= queue.length) {
            grow(i + 1);
        }
        size = i + 1;
        if (i == 0) {
            queue[0] = e;
        } else {
            siftUp(i, e);
        }
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (size == 0) return null;
        int s = --size;
        T result = (T) queue[0];
        T x = (T) queue[s];
        queue[s] = null;
        if (s != 0) {
            siftDown(0, x);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (size == 0) ? null : (T) queue[0];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            queue[i] = null;
        }
        size = 0;
    }

    public CustomArrayList<T> toList() {
        CustomArrayList<T> list = new CustomArrayList<>(size);
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T item = (T) queue[i];
            list.add(item);
        }
        return list;
    }

    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        int newCapacity = oldCapacity + ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
        if (newCapacity < minCapacity) newCapacity = minCapacity;
        Object[] newQueue = new Object[newCapacity];
        System.arraycopy(queue, 0, newQueue, 0, size);
        queue = newQueue;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int k, T x) {
        if (comparator != null) {
            siftUpUsingComparator(k, x);
        } else {
            siftUpComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, T x) {
        Comparable<? super T> key = (Comparable<? super T>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((T) e) >= 0) break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, T x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (T) e) >= 0) break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int k, T x) {
        if (comparator != null) {
            siftDownUsingComparator(k, x);
        } else {
            siftDownComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, T x) {
        Comparable<? super T> key = (Comparable<? super T>) x;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size && ((Comparable<? super T>) c).compareTo((T) queue[right]) > 0) {
                child = right;
                c = queue[child];
            }
            if (key.compareTo((T) c) <= 0) break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, T x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size && comparator.compare((T) c, (T) queue[right]) > 0) {
                child = right;
                c = queue[child];
            }
            if (comparator.compare(x, (T) c) <= 0) break;
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }
}
