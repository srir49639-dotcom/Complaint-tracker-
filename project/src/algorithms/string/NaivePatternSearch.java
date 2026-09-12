package algorithms.string;

import interfaces.SearchAlgorithm;
import datastructures.CustomArrayList;

public class NaivePatternSearch implements SearchAlgorithm {

    @Override
    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new int[0];
        }

        int n = text.length();
        int m = pattern.length();
        String tLower = text.toLowerCase();
        String pLower = pattern.toLowerCase();
        CustomArrayList<Integer> matches = new CustomArrayList<>();

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && tLower.charAt(i + j) == pLower.charAt(j)) {
                j++;
            }
            if (j == m) {
                matches.add(i);
            }
        }

        int[] result = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            result[i] = matches.get(i);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Naive Pattern Search";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N * M)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}
