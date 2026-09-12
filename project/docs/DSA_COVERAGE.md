# DSA-3 Course Outcome (CO1 – CO6) Coverage Document

## Overview
**Smart Complaint Tracker** is an enterprise-grade Complaint Management System implemented exclusively in pure Java (Java 21). It uses no relational or NoSQL database, persisting all records in custom flat files while running an advanced data structures and algorithms engine behind the scenes to power searching, fuzzy typo correction, duplicate detection, bipartite capacity assignment, makespan work scheduling, and parallel analytics.

---

## 1. CO1: Problem Classification & Algorithmic Strategy Selection
- **Description**: Evaluates query, dataset size, and computational constraints to select appropriate algorithmic strategies dynamically.
- **Java Class**: [`SearchEngineService`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/services/SearchEngineService.java), [`JobSchedulingApproximation`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/approximation/JobSchedulingApproximation.java)
- **Integration**:
  - Automatically dispatches among Naive ($M \le 3$), KMP (general linear), Z-Function (high character repetition), and Rabin-Karp ($M > 15$ or large corpus).
  - Selects exact Branch-and-Bound / DP for small job scheduling sets ($N \le 12$) vs Graham's LPT 4/3-Approximation for larger loads.
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**

---

## 2. CO2: Advanced String Algorithms
- **Description**: Deterministic linear-time and multi-pattern string searching, dictionary lookup, and suffix indexing.
- **Java Classes**:
  - [`KMPAlgorithm`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/string/KMPAlgorithm.java): Prefix-function / LPS array for deterministic $O(N + M)$ searching.
  - [`ZAlgorithm`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/string/ZAlgorithm.java): Linear time $O(N + M)$ pattern matching via $Z$-box boundaries.
  - [`RabinKarpAlgorithm`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/string/RabinKarpAlgorithm.java): Polynomial rolling hash fingerprinting.
  - [`AhoCorasickAlgorithm`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/string/AhoCorasickAlgorithm.java): Multi-keyword dictionary trie with failure and output links.
  - [`SuffixArrayKasai`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/string/SuffixArrayKasai.java): Suffix Array construction & Kasai's Longest Common Prefix (LCP) algorithm for binary search pattern lookups in $O(M \log N)$.
  - [`CustomTrie`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/datastructures/CustomTrie.java): Prefix tree for live keyword autocompletion.
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**

---

## 3. CO3: Advanced Dynamic Programming
- **Description**: State-space memoization, edit distance metrication, bitmask routing, tree hierarchy aggregation, and sum over subsets.
- **Java Classes**:
  - [`WagnerFischerEditDistance`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/WagnerFischerEditDistance.java): Levenshtein distance for fuzzy search typo auto-correction and duplicate detection.
  - [`BitmaskDPScheduler`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/BitmaskDPScheduler.java): Held-Karp $O(2^N \cdot N^2)$ Bitmask DP for optimal routing and ordering of complaint tasks.
  - [`IntervalDPScheduler`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/IntervalDPScheduler.java): $O(N^3)$ Matrix Chain style interval DP for staged pipeline transitions.
  - [`TreeDPHierarchyAnalyzer`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/TreeDPHierarchyAnalyzer.java): Maximum independent supervisory impact on the organizational tree hierarchy.
  - [`SOSDPCategoryAnalyzer`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/SOSDPCategoryAnalyzer.java): Sum Over Subsets DP in $O(N \cdot 2^N)$ for category combination queries.
  - Educational Demos: [`NeedlemanWunschDemo`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/NeedlemanWunschDemo.java), [`SmithWatermanDemo`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/SmithWatermanDemo.java), [`MatrixChainMultDemo`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/MatrixChainMultDemo.java), [`OptimalBSTDemo`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/dp/OptimalBSTDemo.java).
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**

---

## 4. CO4: Network Flow & Matching
- **Description**: Maximal capacity augmentation and bipartite matching under workload constraints.
- **Java Classes**:
  - [`FordFulkerson`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/flow/FordFulkerson.java): DFS augmenting path flow $O(E \cdot \text{MaxFlow})$.
  - [`EdmondsKarp`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/flow/EdmondsKarp.java): BFS shortest augmenting path flow $O(V \cdot E^2)$.
  - [`DinicAlgorithm`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/flow/DinicAlgorithm.java): Level graph + blocking flow in $O(V^2 E)$ ($O(E \sqrt{V})$ for unit networks).
  - [`BipartiteMatchingAssignment`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/flow/BipartiteMatchingAssignment.java): Flow network matching complaints to department staff under individual capacity limits.
  - [`MinCostMaxFlow`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/flow/MinCostMaxFlow.java): Successive shortest path algorithm with SPFA.
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**

---

## 5. CO5: NP-Completeness & Approximation Algorithms
- **Description**: Provable polynomial-time approximation bounds for NP-Hard scheduling and graph coverage problems.
- **Java Classes**:
  - [`VertexCoverApproximation`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/approximation/VertexCoverApproximation.java): Maximal matching 2-Approximation for minimal staff inspection points over connected complaint locations.
  - [`JobSchedulingApproximation`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/approximation/JobSchedulingApproximation.java): Graham's Longest Processing Time (LPT) 4/3-Approximation and exact Branch-and-Bound solver for makespan minimization.
  - [`TSPApproximation`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/approximation/TSPApproximation.java): 2-Approximation for Metric TSP via MST doubling & triangle inequality shortcutting.
  - [`SubsetSumKnapsackSolver`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/approximation/SubsetSumKnapsackSolver.java): 0/1 Knapsack dynamic programming and FPTAS.
  - [`NPTheoryReference`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/np/NPTheoryReference.java): Academic encyclopedia of Cook-Levin, 3-SAT, Clique, Independent Set, PTAS, FPTAS, and Kernelization.
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**

---

## 6. CO6: Randomized & Parallel Algorithms
- **Description**: Probabilistic algorithms and multi-core work-span parallel computation.
- **Java Classes**:
  - [`RandomizedQuickSort`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/randomized/RandomizedQuickSort.java): Uniform randomized pivot selection.
  - [`MillerRabinPrimality`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/randomized/MillerRabinPrimality.java): Probabilistic primality test with $(1/4)^k$ error bound for security validation.
  - [`RandomizedPolynomialHash`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/randomized/RandomizedPolynomialHash.java): Universal randomized Rabin fingerprinting.
  - [`ReservoirSampler`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/randomized/ReservoirSampler.java): Algorithm R for single-pass uniform sampling for complaint quality auditing.
  - [`ParallelPrefixSum`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/parallel/ParallelPrefixSum.java): Two-pass parallel scan tree for cumulative intake trend analytics.
  - [`ParallelReduction`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/parallel/ParallelReduction.java): ForkJoin parallel reduction for average, min, max resolution statistics.
  - [`ParallelMergeSort`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/parallel/ParallelMergeSort.java): Parallel divide-and-conquer sorting.
  - [`WorkSpanAnalyzer`](file:///c:/Users/sriram/OneDrive/Desktop/dsa%20proj/src/algorithms/parallel/WorkSpanAnalyzer.java): Work $T_1$, Span $T_\infty$, Parallelism $T_1/T_\infty$, and Brent's theorem speedup models.
- **Status**: **IMPLEMENTED, INTEGRATED, TESTED, DOCUMENTED**
