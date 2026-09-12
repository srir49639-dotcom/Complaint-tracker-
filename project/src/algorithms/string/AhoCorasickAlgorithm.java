package algorithms.string;

import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomQueue;

public class AhoCorasickAlgorithm {

    public static class MatchResult {
        public final int startIndex;
        public final int endIndex;
        public final String matchedKeyword;

        public MatchResult(int startIndex, int endIndex, String matchedKeyword) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.matchedKeyword = matchedKeyword;
        }

        @Override
        public String toString() {
            return matchedKeyword + "@[" + startIndex + "," + endIndex + "]";
        }
    }

    private static class Node {
        final CustomHashTable<Character, Node> children = new CustomHashTable<>(16);
        Node fail;
        CustomArrayList<String> output = new CustomArrayList<>();

        Node() {
            this.fail = null;
        }
    }

    private final Node root;
    private final CustomArrayList<String> dictionary;

    public AhoCorasickAlgorithm(CustomArrayList<String> keywords) {
        this.root = new Node();
        this.dictionary = keywords;
        buildTrie(keywords);
        buildFailureLinks();
    }

    private void buildTrie(CustomArrayList<String> keywords) {
        if (keywords == null) return;
        for (String word : keywords) {
            if (word == null || word.trim().isEmpty()) continue;
            String clean = word.trim().toLowerCase();
            Node curr = root;
            for (int i = 0; i < clean.length(); i++) {
                char c = clean.charAt(i);
                Node child = curr.children.get(c);
                if (child == null) {
                    child = new Node();
                    curr.children.put(c, child);
                }
                curr = child;
            }
            curr.output.add(clean);
        }
    }

    private void buildFailureLinks() {
        CustomQueue<Node> queue = new CustomQueue<>();

        // Level 1 nodes fail to root
        CustomArrayList<Character> rootKeys = root.children.keySet();
        for (Character c : rootKeys) {
            Node child = root.children.get(c);
            child.fail = root;
            queue.enqueue(child);
        }

        // BFS for deeper nodes
        while (!queue.isEmpty()) {
            Node current = queue.dequeue();
            CustomArrayList<Character> keys = current.children.keySet();

            for (Character c : keys) {
                Node child = current.children.get(c);
                Node f = current.fail;

                while (f != null && f.children.get(c) == null) {
                    f = f.fail;
                }

                child.fail = (f == null) ? root : f.children.get(c);
                if (child.fail == null) child.fail = root;

                // Merge dictionary outputs
                if (child.fail != null) {
                    child.output.addAll(child.fail.output);
                }

                queue.enqueue(child);
            }
        }
    }

    public CustomArrayList<MatchResult> searchAll(String text) {
        CustomArrayList<MatchResult> matches = new CustomArrayList<>();
        if (text == null || text.isEmpty()) return matches;

        String cleanText = text.toLowerCase();
        Node curr = root;

        for (int i = 0; i < cleanText.length(); i++) {
            char c = cleanText.charAt(i);

            while (curr != root && curr.children.get(c) == null) {
                curr = (curr.fail != null) ? curr.fail : root;
            }

            Node next = curr.children.get(c);
            curr = (next != null) ? next : root;

            for (String kw : curr.output) {
                matches.add(new MatchResult(i - kw.length() + 1, i, kw));
            }
        }

        return matches;
    }

    public String getName() {
        return "Aho-Corasick Multi-Pattern Matcher";
    }

    public String getTimeComplexity() {
        return "O(TextLength + Sum(PatternLengths) + MatchesCount)";
    }

    public String getSpaceComplexity() {
        return "O(Sum(PatternLengths) * AlphabetSize)";
    }
}
