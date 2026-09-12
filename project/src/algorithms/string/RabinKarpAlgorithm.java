package algorithms.string;

import interfaces.SearchAlgorithm;
import datastructures.CustomArrayList;

public class RabinKarpAlgorithm implements SearchAlgorithm {
    private static final int PRIME_MOD = 1000000007;
    private static final int BASE = 256;

    @Override
    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new int[0];
        }

        int n = text.length();
        int m = pattern.length();
        String tLower = text.toLowerCase();
        String pLower = pattern.toLowerCase();

        long pHash = 0;
        long tHash = 0;
        long h = 1;

        // h = BASE^(m-1) % PRIME_MOD
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % PRIME_MOD;
        }

        // Calculate initial hash values of pattern and first window of text
        for (int i = 0; i < m; i++) {
            pHash = (BASE * pHash + pLower.charAt(i)) % PRIME_MOD;
            tHash = (BASE * tHash + tLower.charAt(i)) % PRIME_MOD;
        }

        CustomArrayList<Integer> matches = new CustomArrayList<>();

        // Slide the pattern over text
        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Verify characters to avoid hash collisions
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (tLower.charAt(i + j) != pLower.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Compute hash for next window
            if (i < n - m) {
                tHash = (BASE * (tHash - tLower.charAt(i) * h) + tLower.charAt(i + m)) % PRIME_MOD;
                if (tHash < 0) {
                    tHash = (tHash + PRIME_MOD);
                }
            }
        }

        int[] result = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i);
        }
        return result;
    }

    public long computeRollingHash(String s) {
        long hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = (BASE * hash + Character.toLowerCase(s.charAt(i))) % PRIME_MOD;
        }
        return hash;
    }

    @Override
    public String getName() {
        return "Rabin-Karp Rolling Hash";
    }

    @Override
    public String getTimeComplexity() {
        return "Average O(N + M), Worst O(N * M)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}
