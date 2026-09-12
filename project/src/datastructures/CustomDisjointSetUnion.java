package datastructures;

import java.io.Serializable;

public class CustomDisjointSetUnion<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomHashTable<T, T> parent;
    private final CustomHashTable<T, Integer> rank;
    private int setCount;

    public CustomDisjointSetUnion() {
        this.parent = new CustomHashTable<>(31);
        this.rank = new CustomHashTable<>(31);
        this.setCount = 0;
    }

    public void makeSet(T item) {
        if (!parent.containsKey(item)) {
            parent.put(item, item);
            rank.put(item, 0);
            setCount++;
        }
    }

    public T find(T item) {
        if (!parent.containsKey(item)) {
            makeSet(item);
            return item;
        }

        T p = parent.get(item);
        if (!p.equals(item)) {
            T root = find(p);
            parent.put(item, root); // Path compression
            return root;
        }
        return p;
    }

    public boolean union(T item1, T item2) {
        T root1 = find(item1);
        T root2 = find(item2);

        if (root1.equals(root2)) return false;

        int rank1 = rank.getOrDefault(root1, 0);
        int rank2 = rank.getOrDefault(root2, 0);

        if (rank1 < rank2) {
            parent.put(root1, root2);
        } else if (rank1 > rank2) {
            parent.put(root2, root1);
        } else {
            parent.put(root2, root1);
            rank.put(root1, rank1 + 1);
        }

        setCount--;
        return true;
    }

    public boolean connected(T item1, T item2) {
        return find(item1).equals(find(item2));
    }

    public int getSetCount() {
        return setCount;
    }
}
