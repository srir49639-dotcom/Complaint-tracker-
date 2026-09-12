package algorithms.string;

import datastructures.CustomArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SuffixArrayKasai {

    public static class SuffixItem {
        public final int index;
        public final int rank1;
        public final int rank2;

        public SuffixItem(int index, int rank1, int rank2) {
            this.index = index;
            this.rank1 = rank1;
            this.rank2 = rank2;
        }
    }

    private final String text;
    private final int[] suffixArray;
    private final int[] lcpArray;

    public SuffixArrayKasai(String text) {
        this.text = text != null ? text.toLowerCase() : "";
        this.suffixArray = buildSuffixArray(this.text);
        this.lcpArray = buildKasaiLCP(this.text, this.suffixArray);
    }

    private int[] buildSuffixArray(String s) {
        int n = s.length();
        if (n == 0) return new int[0];

        SuffixItem[] suffixes = new SuffixItem[n];
        for (int i = 0; i < n; i++) {
            int nextRank = (i + 1 < n) ? (s.charAt(i + 1) - 'a') : -1;
            suffixes[i] = new SuffixItem(i, s.charAt(i) - 'a', nextRank);
        }

        Arrays.sort(suffixes, (a, b) -> {
            if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
            return Integer.compare(a.rank2, b.rank2);
        });

        int[] ind = new int[n];
        for (int k = 4; k < 2 * n; k *= 2) {
            int rank = 0;
            int prevRank = suffixes[0].rank1;
            suffixes[0] = new SuffixItem(suffixes[0].index, rank, suffixes[0].rank2);
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    prevRank = suffixes[i].rank1;
                    suffixes[i] = new SuffixItem(suffixes[i].index, rank, suffixes[i].rank2);
                } else {
                    prevRank = suffixes[i].rank1;
                    suffixes[i] = new SuffixItem(suffixes[i].index, ++rank, suffixes[i].rank2);
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                int r2 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank1 : -1;
                suffixes[i] = new SuffixItem(suffixes[i].index, suffixes[i].rank1, r2);
            }

            Arrays.sort(suffixes, (a, b) -> {
                if (a.rank1 != b.rank1) return Integer.compare(a.rank1, b.rank1);
                return Integer.compare(a.rank2, b.rank2);
            });
        }

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = suffixes[i].index;
        }
        return sa;
    }

    private int[] buildKasaiLCP(String s, int[] sa) {
        int n = s.length();
        if (n == 0) return new int[0];
        int[] lcp = new int[n];
        int[] invSa = new int[n];

        for (int i = 0; i < n; i++) {
            invSa[sa[i]] = i;
        }

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (invSa[i] == n - 1) {
                k = 0;
                continue;
            }
            int j = sa[invSa[i] + 1];
            while (i + k < n && j + k < n && s.charAt(i + k) == s.charAt(j + k)) {
                k++;
            }
            lcp[invSa[i]] = k;
            if (k > 0) k--;
        }
        return lcp;
    }

    public int[] searchPattern(String pattern) {
        if (pattern == null || pattern.isEmpty() || suffixArray.length == 0) return new int[0];
        String p = pattern.toLowerCase();
        int n = text.length();
        int m = p.length();

        // Binary search for left boundary
        int l = 0, r = n - 1;
        int leftMatch = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            String suffix = text.substring(suffixArray[mid], Math.min(n, suffixArray[mid] + m));
            int cmp = suffix.compareTo(p);
            if (cmp >= 0) {
                if (cmp == 0) leftMatch = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        if (leftMatch == -1) return new int[0];

        // Binary search for right boundary
        l = 0; r = n - 1;
        int rightMatch = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            String suffix = text.substring(suffixArray[mid], Math.min(n, suffixArray[mid] + m));
            int cmp = suffix.compareTo(p);
            if (cmp <= 0) {
                if (cmp == 0) rightMatch = mid;
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }

        if (rightMatch == -1 || rightMatch < leftMatch) return new int[0];

        int count = rightMatch - leftMatch + 1;
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = suffixArray[leftMatch + i];
        }
        Arrays.sort(result);
        return result;
    }

    public int[] getSuffixArray() { return suffixArray; }
    public int[] getLcpArray() { return lcpArray; }

    public String getName() { return "Suffix Array with Kasai LCP"; }
    public String getTimeComplexity() { return "Construction O(N log^2 N), Search O(M log N)"; }
    public String getSpaceComplexity() { return "O(N)"; }
}
