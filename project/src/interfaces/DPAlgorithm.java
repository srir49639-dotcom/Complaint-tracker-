package interfaces;

public interface DPAlgorithm<I, O> {
    /**
     * Executes the Dynamic Programming algorithm on the given input.
     */
    O solve(I input);

    String getName();

    String getTimeComplexity();

    String getSpaceComplexity();
}
