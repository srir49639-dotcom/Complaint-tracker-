package datastructures;

import java.io.Serializable;
import java.util.NoSuchElementException;

public class CustomQueue<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomLinkedList<T> list;

    public CustomQueue() {
        this.list = new CustomLinkedList<>();
    }

    public void enqueue(T item) {
        list.addLast(item);
    }

    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return list.removeFirst();
    }

    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }
}
