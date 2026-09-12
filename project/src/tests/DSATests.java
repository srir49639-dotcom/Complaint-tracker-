package tests;

import datastructures.*;

public class DSATests {

    public static void runAll() {
        System.out.println("\n========== RUNNING CUSTOM DATA STRUCTURE TESTS ==========");
        testCustomArrayList();
        testCustomLinkedList();
        testCustomStack();
        testCustomQueue();
        testCustomPriorityQueue();
        testCustomHashTable();
        testCustomTrie();
        testCustomGraph();
        testCustomDisjointSetUnion();
        System.out.println(">>> ALL 9 CUSTOM DATA STRUCTURE TESTS PASSED! [100% SUCCESS]");
    }

    private static void testCustomArrayList() {
        CustomArrayList<String> list = new CustomArrayList<>();
        assert list.isEmpty();
        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma");
        assert list.size() == 3;
        assert list.get(1).equals("Beta");
        list.remove(1);
        assert list.size() == 2;
        assert list.get(1).equals("Gamma");
        System.out.println("  [PASS] CustomArrayList<T>");
    }

    private static void testCustomLinkedList() {
        CustomLinkedList<Integer> ll = new CustomLinkedList<>();
        ll.addFirst(10);
        ll.addLast(20);
        ll.addFirst(5);
        assert ll.size() == 3;
        assert ll.removeFirst() == 5;
        assert ll.removeLast() == 20;
        assert ll.getFirst() == 10;
        System.out.println("  [PASS] CustomLinkedList<T>");
    }

    private static void testCustomStack() {
        CustomStack<String> stack = new CustomStack<>();
        stack.push("A");
        stack.push("B");
        assert stack.peek().equals("B");
        assert stack.pop().equals("B");
        assert stack.pop().equals("A");
        assert stack.isEmpty();
        System.out.println("  [PASS] CustomStack<T>");
    }

    private static void testCustomQueue() {
        CustomQueue<String> q = new CustomQueue<>();
        q.enqueue("First");
        q.enqueue("Second");
        assert q.dequeue().equals("First");
        assert q.peek().equals("Second");
        assert q.dequeue().equals("Second");
        assert q.isEmpty();
        System.out.println("  [PASS] CustomQueue<T>");
    }

    private static void testCustomPriorityQueue() {
        CustomPriorityQueue<Integer> minHeap = new CustomPriorityQueue<>();
        minHeap.offer(50);
        minHeap.offer(10);
        minHeap.offer(30);
        minHeap.offer(5);
        assert minHeap.poll() == 5;
        assert minHeap.poll() == 10;
        assert minHeap.poll() == 30;
        assert minHeap.poll() == 50;
        System.out.println("  [PASS] CustomPriorityQueue<T> (Binary Heap)");
    }

    private static void testCustomHashTable() {
        CustomHashTable<String, Integer> map = new CustomHashTable<>(5);
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);
        map.put("Four", 4);
        assert map.get("One") == 1;
        assert map.get("Three") == 3;
        assert map.size() == 4;
        map.remove("Two");
        assert !map.containsKey("Two");
        System.out.println("  [PASS] CustomHashTable<K,V> (Separate Chaining)");
    }

    private static void testCustomTrie() {
        CustomTrie trie = new CustomTrie();
        trie.insert("electricity");
        trie.insert("electrical");
        trie.insert("elevator");
        assert trie.search("electricity");
        assert !trie.search("elect");
        assert trie.startsWith("elec");
        CustomArrayList<String> suggestions = trie.autocomplete("elec", 5);
        assert suggestions.size() == 2;
        System.out.println("  [PASS] CustomTrie (Prefix Tree)");
    }

    private static void testCustomGraph() {
        CustomGraph<String> g = new CustomGraph<>();
        g.addEdge("A", "B", 1.0, "LINK", true);
        g.addEdge("B", "C", 1.0, "LINK", true);
        g.addEdge("D", "E", 1.0, "LINK", true);
        assert g.getVertexCount() == 5;
        CustomArrayList<String> bfs = g.bfs("A");
        assert bfs.size() == 3;
        CustomArrayList<CustomArrayList<String>> comps = g.getConnectedComponents();
        assert comps.size() == 2;
        System.out.println("  [PASS] CustomGraph<V> (Adjacency List & BFS/DFS/Components)");
    }

    private static void testCustomDisjointSetUnion() {
        CustomDisjointSetUnion<String> dsu = new CustomDisjointSetUnion<>();
        dsu.makeSet("X");
        dsu.makeSet("Y");
        dsu.makeSet("Z");
        assert !dsu.connected("X", "Y");
        dsu.union("X", "Y");
        assert dsu.connected("X", "Y");
        assert !dsu.connected("X", "Z");
        dsu.union("Y", "Z");
        assert dsu.connected("X", "Z");
        System.out.println("  [PASS] CustomDisjointSetUnion<T> (Path Compression & Union-By-Rank)");
    }
}
