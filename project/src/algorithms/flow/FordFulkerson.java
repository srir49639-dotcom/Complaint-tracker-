package algorithms.flow;

import interfaces.FlowAlgorithm;
import java.util.Arrays;

public class FordFulkerson implements FlowAlgorithm {

    private int[][] residualGraph;

    @Override
    public int computeMaxFlow(int[][] capacityMatrix, int source, int sink) {
        if (capacityMatrix == null || capacityMatrix.length == 0) return 0;
        int n = capacityMatrix.length;
        residualGraph = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacityMatrix[i], 0, residualGraph[i], 0, n);
        }

        int[] parent = new int[n];
        int maxFlow = 0;

        while (dfsPath(source, sink, parent, new boolean[n])) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean dfsPath(int u, int sink, int[] parent, boolean[] visited) {
        if (u == sink) return true;
        visited[u] = true;

        for (int v = 0; v < residualGraph.length; v++) {
            if (!visited[v] && residualGraph[u][v] > 0) {
                parent[v] = u;
                if (dfsPath(v, sink, parent, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int[][] getResidualGraph() {
        return residualGraph;
    }

    @Override
    public String getName() {
        return "Ford-Fulkerson Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(E * MaxFlow)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V^2)";
    }
}
