# Algorithmic Complexity Analysis

Detailed theoretical asymptotic bounds for all algorithms implemented in **Smart Complaint Tracker**:

---

## 1. String Searching Algorithms (CO2)

### Knuth-Morris-Pratt (KMP)
- **Time Complexity**:
  - Preprocessing (LPS Table): $O(M)$ where $M = |\text{Pattern}|$
  - Text Search: $O(N)$ where $N = |\text{Text}|$
  - **Total**: $O(N + M)$ deterministic time.
- **Space Complexity**: $O(M)$ auxiliary memory for the failure function array.

### Z-Algorithm
- **Time Complexity**: $O(N + M)$ linear time via Z-box window boundaries $[L, R]$.
- **Space Complexity**: $O(N + M)$ for the concatenation string $P + \$ + T$ and $Z$-array.

### Rabin-Karp Rolling Hash
- **Time Complexity**:
  - Hash Precomputation: $O(M)$
  - Expected Search Time: $O(N + M)$ with base $B=256$, modulus $P=10^9+7$.
  - Worst Case (All Collisions): $O(N \cdot M)$.
- **Space Complexity**: $O(1)$ auxiliary memory.

### Aho-Corasick Multi-Pattern Search
- **Time Complexity**:
  - Trie Construction: $O(\sum_{i=1}^k M_i)$
  - Failure Link BFS: $O(\sum_{i=1}^k M_i)$
  - Search: $O(N + \text{Matches})$
- **Space Complexity**: $O(\sum_{i=1}^k M_i \cdot |\Sigma|)$.

### Suffix Array & Kasai LCP
- **Time Complexity**:
  - Prefix Doubling Construction: $O(N \log^2 N)$
  - Kasai's LCP Construction: $O(N)$
  - Pattern Search: $O(M \log N)$ via binary search over suffix array indices.
- **Space Complexity**: $O(N)$ for suffix and LCP arrays.

---

## 2. Advanced Dynamic Programming (CO3)

### Wagner-Fischer Edit Distance
- **Time Complexity**: $O(M \cdot N)$ where $M, N$ are string lengths.
- **Space Complexity**: $O(M \cdot N)$ 2D matrix (can be reduced to $O(\min(M, N))$ with rolling arrays).

### Bitmask DP (Held-Karp TSP)
- **Time Complexity**: $O(2^N \cdot N^2)$ across $2^N$ submask states and $N$ transition vertices.
- **Space Complexity**: $O(2^N \cdot N)$ state memoization table.

### Interval DP (Staged Task Sequencing)
- **Time Complexity**: $O(N^3)$ across chain lengths $L = 2 \dots N$ and split points $k$.
- **Space Complexity**: $O(N^2)$ upper triangular matrix.

### Tree DP (Organizational Hierarchy Impact)
- **Time Complexity**: $O(V + E) = O(V)$ single DFS traversal over tree structure.
- **Space Complexity**: $O(V)$ for recursive call stack and memoized inclusion/exclusion values.

### Sum Over Subsets (SOS) DP
- **Time Complexity**: $O(N \cdot 2^N)$ across $N$ bit positions, improving from naive submask iteration $O(3^N)$.
- **Space Complexity**: $O(2^N)$ memory for state vector $F$.

---

## 3. Network Flow & Matching (CO4)

### Dinic's Algorithm
- **Time Complexity**:
  - General Networks: $O(V^2 E)$
  - Unit Capacity / Bipartite Networks: $O(E \sqrt{V})$
- **Space Complexity**: $O(V^2)$ residual adjacency matrix and $O(V)$ level graph pointers.

### Edmonds-Karp
- **Time Complexity**: $O(V \cdot E^2)$ with BFS shortest augmenting paths.
- **Space Complexity**: $O(V^2)$ residual matrix.

---

## 4. Approximation Algorithms (CO5)

### Vertex Cover 2-Approximation (Maximal Matching)
- **Time Complexity**: $O(V + E)$ single edge scan.
- **Approximation Ratio**: $2.0 \cdot \text{OPT}$.
- **Proof Summary**: Every maximal matching $M$ of size $k$ requires at least $k$ vertices in any valid vertex cover since no two edges in $M$ share a vertex ($\text{OPT} \ge k$). Taking both endpoints yields $|C| = 2k \le 2 \cdot \text{OPT}$.

### Graham LPT 4/3-Approximation (Job Scheduling Makespan)
- **Time Complexity**: $O(N \log N + N \log M)$ with $N$ complaints and $M$ staff.
- **Approximation Ratio**: $\le \frac{4}{3} - \frac{1}{3M} \cdot \text{OPT}$.

---

## 5. Randomized & Parallel Algorithms (CO6)

### Miller-Rabin Primality Test
- **Time Complexity**: $O(k \cdot \log^3 N)$ with $k$ probabilistic rounds.
- **Error Bound**: Probability of false positive composite $\le (1/4)^k$. For $k=10$, error $< 10^{-6}$.

### Reservoir Sampling (Algorithm R)
- **Time Complexity**: $O(N)$ single pass stream scan.
- **Space Complexity**: $O(K)$ reservoir storage.
- **Selection Probability**: $P(\text{Item } i \text{ in sample}) = \frac{K}{N}$ for all $1 \le i \le N$.

### Parallel Prefix Sum & Reduction (ForkJoin)
- **Work ($T_1$)**: $O(N)$ sequential operations.
- **Span ($T_\infty$)**: $O(\log N)$ critical depth.
- **Parallelism ($T_1 / T_\infty$)**: $O(N / \log N)$.
