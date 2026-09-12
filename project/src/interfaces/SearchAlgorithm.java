package interfaces;

public interface SearchAlgorithm {
    /**
     * Searches for occurrences of the pattern in the given text.
     * @param text the target string
     * @param pattern the search pattern
     * @return array of 0-based starting match indices, or empty array if none found
     */
    int[] search(String text, String pattern);

    String getName();

    String getTimeComplexity();

    String getSpaceComplexity();
}
