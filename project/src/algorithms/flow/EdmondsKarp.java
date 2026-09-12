package algorithms.flow;

import interfaces.FlowAlgorithm;
import datastructures.CustomQueue;

public class EdmondsKarp implements FlowAlgorithm {

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

        while (bfs(source, sink, parent)) {
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

    private boolean bfs(int source, int sink, int[] parent) {
        int n = residualGraph.length;
        boolean[] visited = new boolean[n];
        CustomQueue<Integer> queue = new CustomQueue<>();

        queue.enqueue(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.dequeue();

            for (int v = 0; v < n; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) return true;
                    queue.enqueue(v);
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
        return "Edmonds-Karp Network Flow (BFS Augmenting Paths)";
    }

    @Override
    public String getTimeComplexity() {
        return "O(V * E^2)";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V^2)";
    }
}
