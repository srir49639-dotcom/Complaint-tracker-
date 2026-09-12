package algorithms.dp;

public class SOSDPCategoryAnalyzer {

    /**
     * Executes Sum Over Subsets (SOS) DP across category/tag bitmasks.
     * Given an array A where A[mask] is the weight/count of complaints with exact tag mask,
     * returns F where F[mask] = sum of A[submask] for all submasks of mask in O(N * 2^N) instead of O(3^N).
     * @param A base array of size 2^numTags
     * @param numTags number of tag categories (e.g. 10..15)
     * @return cumulative subset sums array F
     */
    public int[] computeSumOverSubsets(int[] A, int numTags) {
        int totalStates = 1 << numTags;
        int[] F = new int[totalStates];

        // Initialize F with A
        for (int mask = 0; mask < totalStates; mask++) {
            F[mask] = (mask < A.length) ? A[mask] : 0;
        }

        // SOS DP iterations
        for (int i = 0; i < numTags; i++) {
            for (int mask = 0; mask < totalStates; mask++) {
                if ((mask & (1 << i)) != 0) {
                    F[mask] += F[mask ^ (1 << i)];
                }
            }
        }

        return F;
    }

    public String getName() {
        return "Sum Over Subsets (SOS) Dynamic Programming";
    }

    public String getTimeComplexity() {
        return "O(N * 2^N)";
    }

    public String getSpaceComplexity() {
        return "O(2^N)";
    }
}
