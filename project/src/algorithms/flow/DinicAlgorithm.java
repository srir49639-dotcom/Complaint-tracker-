package algorithms.flow;

import interfaces.FlowAlgorithm;
import datastructures.CustomQueue;
import java.util.Arrays;

public class DinicAlgorithm implements FlowAlgorithm {

    private int[][] residualGraph;
    private int[] level;
    private int[] ptr;

    @Override
    public int computeMaxFlow(int[][] capacityMatrix, int source, int sink) {
        if (capacityMatrix == null || capacityMatrix.length == 0) return 0;
        int n = capacityMatrix.length;
        residualGraph = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacityMatrix[i], 0, residualGraph[i], 0, n);
        }

        level = new int[n];
        ptr = new int[n];
        int maxFlow = 0;

        while (bfsLevelGraph(source, sink, n)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = sendBlockingFlow(source, sink, Integer.MAX_VALUE, n);
                if (pushed <= 0) break;
                maxFlow += pushed;
            }
        }

        return maxFlow;
    }

    private boolean bfsLevelGraph(int source, int sink, int n) {
        Arrays.fill(level, -1);
        level[source] = 0;
        CustomQueue<Integer> queue = new CustomQueue<>();
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (int v = 0; v < n; v++) {
                if (residualGraph[u][v] > 0 && level[v] < 0) {
                    level[v] = level[u] + 1;
                    queue.enqueue(v);
                }
            }
        }
        return level[sink] >= 0;
    }

    private int sendBlockingFlow(int u, int sink, int flow, int n) {
        if (u == sink) return flow;

        for (int v = ptr[u]; v < n; v++, ptr[u]++) {
            if (level[v] == level[u] + 1 && residualGraph[u][v] > 0) {
                int currentFlow = Math.min(flow, residualGraph[u][v]);
                int pushedFlow = sendBlockingFlow(v, sink, currentFlow, n);

                if (pushedFlow > 0) {
                    residualGraph[u][v] -= pushedFlow;
                    residualGraph[v][u] += pushedFlow;
                    return pushedFlow;
                }
            }
        }
        return 0;
    }

    @Override
    public int[][] getResidualGraph() {
        return residualGraph;
    }

    @Override
    public String getName() {
        return "Dinic's Blocking Flow Algorithm";
    }

    @Override
    public String getTimeComplexity() {
        return "O(V^2 * E), or O(E * sqrt(V)) for unit networks";
    }

    @Override
    public String getSpaceComplexity() {
        return "O(V^2)";
    }
}
