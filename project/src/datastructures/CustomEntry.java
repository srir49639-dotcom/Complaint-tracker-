package datastructures;

import java.io.Serializable;

public class CustomEntry<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;

    private K key;
    private V value;
    public CustomEntry<K, V> next; // for separate chaining

    public CustomEntry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }
}
