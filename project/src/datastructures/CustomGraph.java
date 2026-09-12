package datastructures;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class CustomGraph<V> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Edge<V> implements Serializable {
        private static final long serialVersionUID = 1L;
        public final V from;
        public final V to;
        public final double weight;
        public final String relationshipType;

        public Edge(V from, V to, double weight, String relationshipType) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.relationshipType = relationshipType != null ? relationshipType : "RELATED";
        }
    }

    private final CustomHashTable<V, CustomArrayList<Edge<V>>> adjacencyList;
    private final CustomArrayList<V> vertices;
    private int edgeCount;

    public CustomGraph() {
        this.adjacencyList = new CustomHashTable<>(31);
        this.vertices = new CustomArrayList<>();
        this.edgeCount = 0;
    }

    public void addVertex(V vertex) {
        if (vertex == null) return;
        if (!adjacencyList.containsKey(vertex)) {
            adjacencyList.put(vertex, new CustomArrayList<>());
            vertices.add(vertex);
        }
    }

    public void addEdge(V from, V to, double weight, String relationshipType, boolean bidirectional) {
        addVertex(from);
        addVertex(to);

        CustomArrayList<Edge<V>> fromEdges = adjacencyList.get(from);
        fromEdges.add(new Edge<>(from, to, weight, relationshipType));
        edgeCount++;

        if (bidirectional && !Objects.equals(from, to)) {
            CustomArrayList<Edge<V>> toEdges = adjacencyList.get(to);
            toEdges.add(new Edge<>(to, from, weight, relationshipType));
            edgeCount++;
        }
    }

    public CustomArrayList<Edge<V>> getNeighbors(V vertex) {
        CustomArrayList<Edge<V>> edges = adjacencyList.get(vertex);
        return edges != null ? edges : new CustomArrayList<>();
    }

    public CustomArrayList<V> getVertices() {
        return vertices;
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public CustomArrayList<V> bfs(V startVertex) {
        CustomArrayList<V> visitedOrder = new CustomArrayList<>();
        if (startVertex == null || !adjacencyList.containsKey(startVertex)) return visitedOrder;

        CustomHashTable<V, Boolean> visited = new CustomHashTable<>(vertices.size() * 2);
        CustomQueue<V> queue = new CustomQueue<>();

        visited.put(startVertex, true);
        queue.enqueue(startVertex);

        while (!queue.isEmpty()) {
            V current = queue.dequeue();
            visitedOrder.add(current);

            CustomArrayList<Edge<V>> edges = getNeighbors(current);
            for (Edge<V> edge : edges) {
                if (!visited.containsKey(edge.to)) {
                    visited.put(edge.to, true);
                    queue.enqueue(edge.to);
                }
            }
        }
        return visitedOrder;
    }

    public CustomArrayList<V> dfs(V startVertex) {
        CustomArrayList<V> visitedOrder = new CustomArrayList<>();
        if (startVertex == null || !adjacencyList.containsKey(startVertex)) return visitedOrder;

        CustomHashTable<V, Boolean> visited = new CustomHashTable<>(vertices.size() * 2);
        dfsHelper(startVertex, visited, visitedOrder);
        return visitedOrder;
    }

    private void dfsHelper(V current, CustomHashTable<V, Boolean> visited, CustomArrayList<V> visitedOrder) {
        visited.put(current, true);
        visitedOrder.add(current);

        CustomArrayList<Edge<V>> edges = getNeighbors(current);
        for (Edge<V> edge : edges) {
            if (!visited.containsKey(edge.to)) {
                dfsHelper(edge.to, visited, visitedOrder);
            }
        }
    }

    public CustomArrayList<CustomArrayList<V>> getConnectedComponents() {
        CustomArrayList<CustomArrayList<V>> components = new CustomArrayList<>();
        CustomHashTable<V, Boolean> visited = new CustomHashTable<>(vertices.size() * 2);

        for (V vertex : vertices) {
            if (!visited.containsKey(vertex)) {
                CustomArrayList<V> comp = new CustomArrayList<>();
                dfsHelper(vertex, visited, comp);
                components.add(comp);
            }
        }
        return components;
    }
}
