package algorithms.string;

import interfaces.SearchAlgorithm;
import datastructures.CustomArrayList;

public class ZAlgorithm implements SearchAlgorithm {

    @Override
    public int[] search(String text, String pattern) {
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new int[0];
        }

        String concat = pattern.toLowerCase() + "$" + text.toLowerCase();
        int l = concat.length();
        int[] z = new int[l];
        int left = 0, right = 0;

        for (int i = 1; i < l; i++) {
            if (i > right) {
                left = right = i;
                while (right < l && concat.charAt(right - left) == concat.charAt(right)) {
                    right++;
                }
                z[i] = right - left;
                right--;
            } else {
                int k = i - left;
                if (z[k] < right - i + 1) {
                    z[i] = z[k];
                } else {
                    left = i;
                    while (right < l && concat.charAt(right - left) == concat.charAt(right)) {
                        right++;
                    }
                    z[i] = right - left;
                    right--;
                }
            }
        }

        CustomArrayList<Integer> matches = new CustomArrayList<>();
        int pLen = pattern.length();
        for (int i = 0; i < l; i++) {
            if (z[i] == pLen) {
                matches.add(i - pLen - 1);
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
        return "Z-Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(N + M)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(N + M)";
    }
}
