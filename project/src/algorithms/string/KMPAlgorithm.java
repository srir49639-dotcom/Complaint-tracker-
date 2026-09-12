package algorithms.string;

import interfaces.SearchAlgorithm;
import datastructures.CustomArrayList;

public class KMPAlgorithm implements SearchAlgorithm {

    @Override
    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new int[0];
        }

        int[] lps = computeLPSArray(pattern);
        CustomArrayList<Integer> matchIndices = new CustomArrayList<>();

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < text.length()) {
            if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(pattern.charAt(j))) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                matchIndices.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length() && Character.toLowerCase(text.charAt(i)) != Character.toLowerCase(pattern.charAt(j))) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        int[] result = new int[matchIndices.size()];
        for (int k = 0; k < matchIndices.size(); k++) {
            result[k] = matchIndices.get(k);
        }
        return result;
    }

    private int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (Character.toLowerCase(pattern.charAt(i)) == Character.toLowerCase(pattern.charAt(len))) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    @Override
    public String getName() {
        return "Knuth-Morris-Pratt (KMP)";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N + M)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(M)";
    }
}
