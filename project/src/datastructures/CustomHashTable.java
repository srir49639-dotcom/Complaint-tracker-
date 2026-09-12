package datastructures;

import java.io.Serializable;
import java.util.Objects;

public class CustomHashTable<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 17;
    private static final float LOAD_FACTOR = 0.75f;

    private CustomEntry<K, V>[] table;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public CustomHashTable() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CustomHashTable(int initialCapacity) {
        this.capacity = Math.max(7, initialCapacity);
        this.table = new CustomEntry[this.capacity];
        this.size = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return Math.abs(h % capacity);
    }

    public void put(K key, V value) {
        if ((float) size / capacity >= LOAD_FACTOR) {
            rehash();
        }

        int index = hash(key);
        CustomEntry<K, V> head = table[index];

        while (head != null) {
            if (Objects.equals(head.getKey(), key)) {
                head.setValue(value);
                return;
            }
            head = head.next;
        }

        CustomEntry<K, V> newEntry = new CustomEntry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        CustomEntry<K, V> head = table[index];

        while (head != null) {
            if (Objects.equals(head.getKey(), key)) {
                return head.getValue();
            }
            head = head.next;
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V val = get(key);
        return val != null ? val : defaultValue;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = hash(key);
        CustomEntry<K, V> head = table[index];
        CustomEntry<K, V> prev = null;

        while (head != null) {
            if (Objects.equals(head.getKey(), key)) {
                if (prev != null) {
                    prev.next = head.next;
                } else {
                    table[index] = head.next;
                }
                size--;
                return head.getValue();
            }
            prev = head;
            head = head.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        size = 0;
    }

    public CustomArrayList<K> keySet() {
        CustomArrayList<K> keys = new CustomArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            CustomEntry<K, V> entry = table[i];
            while (entry != null) {
                keys.add(entry.getKey());
                entry = entry.next;
            }
        }
        return keys;
    }

    public CustomArrayList<V> values() {
        CustomArrayList<V> vals = new CustomArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            CustomEntry<K, V> entry = table[i];
            while (entry != null) {
                vals.add(entry.getValue());
                entry = entry.next;
            }
        }
        return vals;
    }

    public CustomArrayList<CustomPair<K, V>> entryList() {
        CustomArrayList<CustomPair<K, V>> entries = new CustomArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            CustomEntry<K, V> entry = table[i];
            while (entry != null) {
                entries.add(new CustomPair<>(entry.getKey(), entry.getValue()));
                entry = entry.next;
            }
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        int newCap = capacity * 2 + 1;
        CustomEntry<K, V>[] oldTable = table;
        table = new CustomEntry[newCap];
        capacity = newCap;
        size = 0;

        for (CustomEntry<K, V> headNode : oldTable) {
            while (headNode != null) {
                put(headNode.getKey(), headNode.getValue());
                headNode = headNode.next;
            }
        }
    }
}
