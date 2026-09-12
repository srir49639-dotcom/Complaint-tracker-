package algorithms.approximation;

import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomPair;

public class VertexCoverApproximation {

    public static class VertexCoverResult {
        public final CustomArrayList<String> selectedVertices;
        public final int coverSize;
        public final String approximationRatio;

        public VertexCoverResult(CustomArrayList<String> selectedVertices, int coverSize, String approximationRatio) {
            this.selectedVertices = selectedVertices;
            this.coverSize = coverSize;
            this.approximationRatio = approximationRatio;
        }
    }

    /**
     * Computes a 2-approximation for Vertex Cover on an undirected graph.
     * Finds minimal inspection points / staff checkpoints covering all reported complaint connections.
     * @param edges list of edges (u, v)
     * @return 2-approx vertex cover result
     */
    public VertexCoverResult compute2Approximation(CustomArrayList<CustomPair<String, String>> edges) {
        if (edges == null || edges.isEmpty()) {
            return new VertexCoverResult(new CustomArrayList<>(), 0, "2.0-Approximation");
        }

        CustomHashTable<String, Boolean> visitedVertices = new CustomHashTable<>(edges.size() * 2);
        CustomArrayList<String> cover = new CustomArrayList<>();

        for (int i = 0; i < edges.size(); i++) {
            CustomPair<String, String> edge = edges.get(i);
            String u = edge.getKey();
            String v = edge.getValue();

            // If neither endpoint is in the cover, add BOTH endpoints to the cover (maximal matching)
            if (!visitedVertices.containsKey(u) && !visitedVertices.containsKey(v)) {
                visitedVertices.put(u, true);
                visitedVertices.put(v, true);
                cover.add(u);
                cover.add(v);
            }
        }

        return new VertexCoverResult(cover, cover.size(), "2.0-Approximation (Maximal Matching)");
    }

    public String getName() {
        return "Vertex Cover 2-Approximation";
    }

    public String getTimeComplexity() {
        return "O(V + E)";
    }

    public String getSpaceComplexity() {
        return "O(V)";
    }
}
