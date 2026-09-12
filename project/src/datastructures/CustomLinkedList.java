package datastructures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomLinkedList<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private static class Node<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data, tail, null);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data, null, head);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    public void add(T data) {
        addLast(data);
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException("List is empty");
        T val = head.data;
        head = head.next;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
        size--;
        return val;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException("List is empty");
        T val = tail.data;
        tail = tail.prev;
        if (tail != null) {
            tail.next = null;
        } else {
            head = null;
        }
        size--;
        return val;
    }

    public T getFirst() {
        if (head == null) throw new NoSuchElementException("List is empty");
        return head.data;
    }

    public T getLast() {
        if (tail == null) throw new NoSuchElementException("List is empty");
        return tail.data;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> curr;
        if (index < size / 2) {
            curr = head;
            for (int i = 0; i < index; i++) curr = curr.next;
        } else {
            curr = tail;
            for (int i = size - 1; i > index; i--) curr = curr.prev;
        }
        return curr.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
