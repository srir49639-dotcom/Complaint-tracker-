package interfaces;

public interface FlowAlgorithm {
    /**
     * Computes maximum network flow from source (s) to sink (t).
     * @param capacityMatrix Adjacency capacity matrix
     * @param source source node index
     * @param sink sink node index
     * @return maximum flow value
     */
    int computeMaxFlow(int[][] capacityMatrix, int source, int sink);

    int[][] getResidualGraph();

    String getName();

    String getTimeComplexity();

    String getSpaceComplexity();
}
