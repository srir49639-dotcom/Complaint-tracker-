package algorithms.flow;

import datastructures.CustomArrayList;
import datastructures.CustomQueue;
import java.util.Arrays;

public class MinCostMaxFlow {

    public static class Edge {
        public final int to;
        public final int rev;
        public int cap;
        public final int cost;

        public Edge(int to, int rev, int cap, int cost) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.cost = cost;
        }
    }

    public static class MCMFResult {
        public final int maxFlow;
        public final int minCost;

        public MCMFResult(int maxFlow, int minCost) {
            this.maxFlow = maxFlow;
            this.minCost = minCost;
        }
    }

    private final int n;
    private final CustomArrayList<CustomArrayList<Edge>> graph;

    public MinCostMaxFlow(int n) {
        this.n = n;
        this.graph = new CustomArrayList<>(n);
        for (int i = 0; i < n; i++) {
            graph.add(new CustomArrayList<>());
        }
    }

    public void addEdge(int from, int to, int cap, int cost) {
        CustomArrayList<Edge> fromList = graph.get(from);
        CustomArrayList<Edge> toList = graph.get(to);

        fromList.add(new Edge(to, toList.size(), cap, cost));
        toList.add(new Edge(from, fromList.size() - 1, 0, -cost));
    }

    public MCMFResult computeMCMF(int s, int t) {
        int maxFlow = 0;
        int minCost = 0;

        int[] dist = new int[n];
        int[] prevNode = new int[n];
        int[] prevEdge = new int[n];
        boolean[] inQueue = new boolean[n];

        while (true) {
            Arrays.fill(dist, Integer.MAX_VALUE);
            Arrays.fill(inQueue, false);
            CustomQueue<Integer> queue = new CustomQueue<>();

            dist[s] = 0;
            queue.enqueue(s);
            inQueue[s] = true;

            while (!queue.isEmpty()) {
                int u = queue.dequeue();
                inQueue[u] = false;

                CustomArrayList<Edge> edges = graph.get(u);
                for (int i = 0; i < edges.size(); i++) {
                    Edge e = edges.get(i);
                    if (e.cap > 0 && dist[u] != Integer.MAX_VALUE && dist[u] + e.cost < dist[e.to]) {
                        dist[e.to] = dist[u] + e.cost;
                        prevNode[e.to] = u;
                        prevEdge[e.to] = i;
                        if (!inQueue[e.to]) {
                            queue.enqueue(e.to);
                            inQueue[e.to] = true;
                        }
                    }
                }
            }

            if (dist[t] == Integer.MAX_VALUE) break;

            int push = Integer.MAX_VALUE;
            for (int v = t; v != s; v = prevNode[v]) {
                int u = prevNode[v];
                int eIdx = prevEdge[v];
                push = Math.min(push, graph.get(u).get(eIdx).cap);
            }

            for (int v = t; v != s; v = prevNode[v]) {
                int u = prevNode[v];
                int eIdx = prevEdge[v];
                Edge e = graph.get(u).get(eIdx);
                e.cap -= push;
                graph.get(v).get(e.rev).cap += push;
            }

            maxFlow += push;
            minCost += push * dist[t];
        }

        return new MCMFResult(maxFlow, minCost);
    }
}
