package algorithms.string;

import datastructures.CustomArrayList;
import datastructures.CustomTrie;

public class TriePatternSearch {

    private final CustomTrie trie;

    public TriePatternSearch() {
        this.trie = new CustomTrie();
    }

    public void indexKeywords(CustomArrayList<String> words) {
        if (words == null) return;
        for (String w : words) {
            trie.insert(w);
        }
    }

    public void insert(String word) {
        trie.insert(word);
    }

    public boolean containsPrefix(String prefix) {
        return trie.startsWith(prefix);
    }

    public boolean containsExact(String word) {
        return trie.search(word);
    }

    public CustomArrayList<String> getSuggestions(String prefix, int maxLimit) {
        return trie.autocomplete(prefix, maxLimit);
    }

    public String getName() {
        return "Trie Prefix Pattern Search";
    }

    public String getTimeComplexity() {
        return "O(PrefixLength + OutputSize)";
    }

    public String getSpaceComplexity() {
        return "O(TotalKeysLength * AlphabetSize)";
    }
}
