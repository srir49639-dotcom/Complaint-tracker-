package algorithms.np;

import datastructures.CustomArrayList;

public class NPTheoryReference {

    public static class TopicSummary {
        public final String topicName;
        public final String complexityClass;
        public final String reductionChain;
        public final String description;

        public TopicSummary(String topicName, String complexityClass, String reductionChain, String description) {
            this.topicName = topicName;
            this.complexityClass = complexityClass;
            this.reductionChain = reductionChain;
            this.description = description;
        }
    }

    public static CustomArrayList<TopicSummary> getNPConceptsList() {
        CustomArrayList<TopicSummary> list = new CustomArrayList<>();

        list.add(new TopicSummary(
                "Cook-Levin Theorem & SAT",
                "NP-Complete",
                "General NP -> SAT",
                "Proves Boolean Satisfiability (SAT) is NP-Complete by encoding any non-deterministic Turing machine computation in polynomial time."
        ));

        list.add(new TopicSummary(
                "3-SAT",
                "NP-Complete",
                "SAT <=p 3-SAT",
                "Restricts clauses to exactly 3 literals. Core stepping stone for subsequent graph and set problem reductions."
        ));

        list.add(new TopicSummary(
                "Independent Set",
                "NP-Complete",
                "3-SAT <=p Independent Set",
                "Finding a set of vertices in graph G such that no two are adjacent. Dual of Vertex Cover and Clique."
        ));

        list.add(new TopicSummary(
                "Vertex Cover",
                "NP-Complete",
                "Independent Set <=p Vertex Cover",
                "Finding minimum set of vertices incident to every edge. S is an Independent Set iff V \\ S is a Vertex Cover. Admits 2-approximation."
        ));

        list.add(new TopicSummary(
                "Clique",
                "NP-Complete",
                "Independent Set <=p Clique",
                "Finding maximum subset of mutually adjacent vertices. Equivalent to Independent Set in the complement graph G'."
        ));

        list.add(new TopicSummary(
                "Hamiltonian Cycle & Path",
                "NP-Complete",
                "3-SAT <=p Directed HC <=p Undirected HC",
                "Finding a simple closed cycle visiting every vertex exactly once. Special case of TSP with unit/infinity edge costs."
        ));

        list.add(new TopicSummary(
                "Traveling Salesperson Problem (TSP)",
                "NP-Hard",
                "Hamiltonian Cycle <=p TSP",
                "Finding minimum cost tour visiting all cities. Metric TSP admits 1.5-approx (Christofides) and 2-approx (MST doubling)."
        ));

        list.add(new TopicSummary(
                "Subset Sum & Knapsack",
                "NP-Complete / NP-Hard",
                "3-SAT <=p 3D Matching <=p Subset Sum <=p Knapsack",
                "Pseudo-polynomial time O(N * W) via DP. Knapsack admits Fully Polynomial Time Approximation Scheme (FPTAS)."
        ));

        list.add(new TopicSummary(
                "Approximation Hierarchy & PTAS / FPTAS",
                "Theoretical Foundation",
                "P subset of FPTAS subset of PTAS subset of APX",
                "PTAS achieves (1 + epsilon) in time n^(O(1/eps)); FPTAS achieves it in poly(n, 1/eps). Vertex Cover in APX; TSP without triangle inequality not in APX."
        ));

        list.add(new TopicSummary(
                "Parameterized Complexity & Kernelization",
                "FPT",
                "Pre-processing polynomial reduction to kernel size f(k)",
                "k-Vertex Cover is fixed-parameter tractable (FPT) solvable in O(2^k * k + n) using Nemhauser-Trotter kernelization."
        ));

        return list;
    }
}
