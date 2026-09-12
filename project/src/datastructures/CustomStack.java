package datastructures;

import java.io.Serializable;
import java.util.EmptyStackException;

public class CustomStack<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomArrayList<T> list;

    public CustomStack() {
        this.list = new CustomArrayList<>();
    }

    public void push(T item) {
        list.add(item);
    }

    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return list.remove(list.size() - 1);
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return list.get(list.size() - 1);
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
