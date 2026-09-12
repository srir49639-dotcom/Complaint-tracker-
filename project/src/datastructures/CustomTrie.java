package datastructures;

import java.io.Serializable;

public class CustomTrie implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class TrieNode implements Serializable {
        private static final long serialVersionUID = 1L;
        public final CustomHashTable<Character, TrieNode> children = new CustomHashTable<>(16);
        public boolean isEndOfWord;
        public String fullWord;
        public int frequency;

        public TrieNode() {
            this.isEndOfWord = false;
            this.fullWord = null;
            this.frequency = 0;
        }
    }

    private final TrieNode root;
    private int wordCount;

    public CustomTrie() {
        this.root = new TrieNode();
        this.wordCount = 0;
    }

    public void insert(String word) {
        if (word == null || word.trim().isEmpty()) return;
        String clean = word.trim().toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            TrieNode child = current.children.get(c);
            if (child == null) {
                child = new TrieNode();
                current.children.put(c, child);
            }
            current = child;
        }

        if (!current.isEndOfWord) {
            current.isEndOfWord = true;
            wordCount++;
        }
        current.fullWord = clean;
        current.frequency++;
    }

    public boolean search(String word) {
        if (word == null) return false;
        TrieNode node = getNode(word.trim().toLowerCase());
        return node != null && node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        if (prefix == null) return false;
        return getNode(prefix.trim().toLowerCase()) != null;
    }

    public CustomArrayList<String> autocomplete(String prefix, int limit) {
        CustomArrayList<String> results = new CustomArrayList<>();
        if (prefix == null) return results;

        String clean = prefix.trim().toLowerCase();
        TrieNode startNode = getNode(clean);
        if (startNode == null) return results;

        collectWords(startNode, results, limit);
        return results;
    }

    private void collectWords(TrieNode node, CustomArrayList<String> results, int limit) {
        if (node == null || results.size() >= limit) return;
        if (node.isEndOfWord && node.fullWord != null) {
            results.add(node.fullWord);
        }

        CustomArrayList<Character> keys = node.children.keySet();
        for (Character c : keys) {
            if (results.size() >= limit) break;
            collectWords(node.children.get(c), results, limit);
        }
    }

    private TrieNode getNode(String str) {
        TrieNode current = root;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            current = current.children.get(c);
            if (current == null) return null;
        }
        return current;
    }

    public int getWordCount() {
        return wordCount;
    }
}
