package algorithms.randomized;

import datastructures.CustomArrayList;
import java.util.Random;

public class ReservoirSampler<T> {

    private final Random random = new Random();

    /**
     * Samples exactly k items uniformly at random from an input list of N items using Reservoir Sampling (Algorithm R).
     * Every item in the population has equal selection probability of k / N.
     */
    public CustomArrayList<T> sample(CustomArrayList<T> population, int k) {
        CustomArrayList<T> reservoir = new CustomArrayList<>(k);
        if (population == null || population.isEmpty() || k <= 0) {
            return reservoir;
        }

        int n = population.size();
        int sampleSize = Math.min(k, n);

        // Fill reservoir with first k elements
        for (int i = 0; i < sampleSize; i++) {
            reservoir.add(population.get(i));
        }

        // Process remaining elements from index k to n - 1
        for (int i = sampleSize; i < n; i++) {
            // Pick a random index between 0 and i inclusive
            int j = random.nextInt(i + 1);
            if (j < sampleSize) {
                reservoir.set(j, population.get(i));
            }
        }

        return reservoir;
    }

    public String getName() {
        return "Reservoir Sampling (Algorithm R)";
    }

    public String getTimeComplexity() {
        return "O(N) single pass";
    }

    public String getSpaceComplexity() {
        return "O(K) auxiliary space";
    }
}
