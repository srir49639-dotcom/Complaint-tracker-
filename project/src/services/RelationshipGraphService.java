package services;

import algorithms.approximation.VertexCoverApproximation;
import datastructures.CustomArrayList;
import datastructures.CustomDisjointSetUnion;
import datastructures.CustomGraph;
import datastructures.CustomPair;
import model.Complaint;

public class RelationshipGraphService {

    private static RelationshipGraphService instance;
    private final VertexCoverApproximation vertexCover = new VertexCoverApproximation();

    public static synchronized RelationshipGraphService getInstance() {
        if (instance == null) {
            instance = new RelationshipGraphService();
        }
        return instance;
    }

    public static class GraphAnalysisResult {
        public final int totalNodes;
        public final int totalEdges;
        public final int connectedComponentsCount;
        public final CustomArrayList<CustomArrayList<String>> clusters;
        public final CustomArrayList<String> criticalInspectionCheckpoints; // from Vertex Cover 2-approx

        public GraphAnalysisResult(int totalNodes, int totalEdges, int connectedComponentsCount,
                                   CustomArrayList<CustomArrayList<String>> clusters,
                                   CustomArrayList<String> criticalInspectionCheckpoints) {
            this.totalNodes = totalNodes;
            this.totalEdges = totalEdges;
            this.connectedComponentsCount = connectedComponentsCount;
            this.clusters = clusters;
            this.criticalInspectionCheckpoints = criticalInspectionCheckpoints;
        }
    }

    public GraphAnalysisResult buildAndAnalyzeNetwork(CustomArrayList<Complaint> complaints) {
        CustomGraph<String> graph = new CustomGraph<>();
        CustomDisjointSetUnion<String> dsu = new CustomDisjointSetUnion<>();
        CustomArrayList<CustomPair<String, String>> edgePairs = new CustomArrayList<>();

        if (complaints == null || complaints.isEmpty()) {
            return new GraphAnalysisResult(0, 0, 0, new CustomArrayList<>(), new CustomArrayList<>());
        }

        // Add nodes and relationships
        for (int i = 0; i < complaints.size(); i++) {
            Complaint c1 = complaints.get(i);
            String node1 = c1.getTrackingId();
            graph.addVertex(node1);
            dsu.makeSet(node1);

            // Connect if same category and same location
            for (int j = i + 1; j < complaints.size(); j++) {
                Complaint c2 = complaints.get(j);
                String node2 = c2.getTrackingId();
                graph.addVertex(node2);
                dsu.makeSet(node2);

                boolean sameCat = c1.getCategory().equalsIgnoreCase(c2.getCategory());
                boolean sameLoc = c1.getLocation() != null && c2.getLocation() != null &&
                        c1.getLocation().equalsIgnoreCase(c2.getLocation());

                if (sameCat && sameLoc) {
                    graph.addEdge(node1, node2, 1.0, "SAME_LOCATION_ISSUE", true);
                    edgePairs.add(new CustomPair<>(node1, node2));
                    dsu.union(node1, node2);
                } else if (sameCat && (c1.getTitle().toLowerCase().contains(c2.getTitle().toLowerCase()) ||
                        c2.getTitle().toLowerCase().contains(c1.getTitle().toLowerCase()))) {
                    graph.addEdge(node1, node2, 2.0, "SIMILAR_TOPIC", true);
                    edgePairs.add(new CustomPair<>(node1, node2));
                    dsu.union(node1, node2);
                }
            }
        }

        CustomArrayList<CustomArrayList<String>> components = graph.getConnectedComponents();
        VertexCoverApproximation.VertexCoverResult vc = vertexCover.compute2Approximation(edgePairs);

        return new GraphAnalysisResult(graph.getVertexCount(), graph.getEdgeCount(),
                components.size(), components, vc.selectedVertices);
    }
}
