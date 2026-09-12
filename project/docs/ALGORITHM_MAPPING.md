# SMART COMPLAINT DSA TRACKER - DSA-3 ALGORITHM MAPPING & COMPLEXITY REFERENCE

## Overview
This document provides a comprehensive mapping of all theoretical Data Structures and Algorithms (DSA-3 Academic Curriculum) implemented within the **Smart Complaint DSA Tracker** application.

---

## 1. Custom Handcrafted Data Structures

| Data Structure | Implementation Class | Time Complexity (Ops) | Space Complexity | Practical Purpose in Project |
|---|---|---|---|---|
| **Custom Dynamic Array** | `datastructures.CustomArrayList<T>` | Get: $O(1)$, Add: $O(1)$ amortized, Insert/Delete: $O(N)$ | $O(N)$ | Primary in-memory container with internal in-place QuickSort |
| **Doubly Linked List** | `datastructures.ComplaintLinkedList<T>` | AddFirst/Last: $O(1)$, Remove: $O(1)$ with ref | $O(N)$ | Sequential complaint tracking and FIFO/LIFO manipulation |
| **LIFO Stack** | `datastructures.ComplaintStack<T>` | Push: $O(1)$, Pop: $O(1)$, Peek: $O(1)$ | $O(N)$ | Complaint modification audit trail, history timeline & rollback |
| **FIFO Queue** | `datastructures.ComplaintQueue<T>` | Enqueue: $O(1)$, Dequeue: $O(1)$, Peek: $O(1)$ | $O(N)$ | First-come-first-serve complaint triage and BFS traversal |
| **Min Binary Heap** | `datastructures.MinHeap<T>` | Insert: $O(\log N)$, ExtractMin: $O(\log N)$, Peek: $O(1)$ | $O(N)$ | Earliest Deadline First (EDF) dispatch scheduling |
| **Max Binary Heap** | `datastructures.MaxHeap<T>` | Insert: $O(\log N)$, ExtractMax: $O(\log N)$, Peek: $O(1)$ | $O(N)$ | Real-time dynamic severity priority ranking |
| **Separate Chaining Hash Table** | `datastructures.CustomHashTable<K,V>` | Put: $O(1)$ avg, Get: $O(1)$ avg, Remove: $O(1)$ avg | $O(N)$ | $O(1)$ Complaint ID lookup, category/department indexing |
| **Prefix Tree (Trie)** | `datastructures.Trie` | Insert: $O(L)$, Search: $O(L)$, Prefix: $O(L)$ | $O(\Sigma \cdot L \cdot N)$ | Multi-field prefix indexing, fast keyword lookup & autocomplete |
| **Adjacency List Graph** | `datastructures.Graph` | AddNode: $O(1)$, AddEdge: $O(1)$, BFS: $O(V + E)$ | $O(V + E)$ | Complaint co-location, technician assignment & dependency network |

---

## 2. Module 2: String Processing Algorithms

| Algorithm | Class | Time Complexity | Space Complexity | Real-World Application |
|---|---|---|---|---|
| **Naive Pattern Search** | `algorithms.string.NaivePatternSearch` | $O((N - M + 1) \cdot M)$ | $O(1)$ | Baseline unoptimized text search for benchmark comparisons |
| **Knuth-Morris-Pratt (KMP)** | `algorithms.string.KMPAlgorithm` | $O(N + M)$ | $O(M)$ | Fast ticket description searching using Longest Prefix Suffix (LPS) array |
| **Z-Function (Z-Algorithm)** | `algorithms.string.ZFunctionAlgorithm` | $O(N + M)$ | $O(N + M)$ | Linear exact matching by preprocessing $S = \text{pattern} + \$ + \text{text}$ |
| **Rabin-Karp Rolling Hash** | `algorithms.string.RabinKarpAlgorithm` | $O(N + M)$ avg, $O(N \cdot M)$ worst | $O(1)$ | Polynomial rolling hash with prime modulus and collision verification |
| **Aho-Corasick Automaton** | `algorithms.string.AhoCorasickAlgorithm` | $O(N + M + Z)$ | $O(M)$ | Simultaneous multi-keyword matching for auto-categorization & duplicate detection |
| **Suffix Array** | `algorithms.string.SuffixArray` | $O(N \log^2 N)$ construction, $O(M \log N)$ query | $O(N)$ | Full-text indexing of complaint database with binary search |
| **Kasai's LCP Array** | `algorithms.string.KasaiAlgorithm` | $O(N)$ construction | $O(N)$ | Linear Longest Common Prefix calculation for Longest Common Substring duplicate matching |

---

## 3. Module 3: Dynamic Programming

| Algorithm | Class | Recurrence / Formula | Complexity | Application |
|---|---|---|---|---|
| **Wagner-Fischer Edit Distance** | `algorithms.dp.EditDistance` | $DP[i][j] = \min(DP[i-1][j]+1, DP[i][j-1]+1, DP[i-1][j-1]+\text{cost})$ | $O(N \cdot M)$ Time<br>$O(N \cdot M)$ Space | Fuzzy complaint searching and typo auto-correction |
| **Damerau-Levenshtein** | `algorithms.dp.EditDistance` | Wagner-Fischer + adjacent character transposition | $O(N \cdot M)$ Time<br>$O(N \cdot M)$ Space | Transposition-aware query suggestions |
| **Needleman-Wunsch** | `algorithms.dp.NeedlemanWunsch` | $M[i][j] = \max(M[i-1][j-1]+S, M[i-1][j]+G, M[i][j-1]+G)$ | $O(N \cdot M)$ Time<br>$O(N \cdot M)$ Space | Global sequence alignment for complaint descriptions |
| **Smith-Waterman** | `algorithms.dp.SmithWaterman` | $H[i][j] = \max(0, H[i-1][j-1]+S, H[i-1][j]+G, H[i][j-1]+G)$ | $O(N \cdot M)$ Time<br>$O(N \cdot M)$ Space | Local sequence alignment for identifying cloned complaint clauses |
| **Matrix Chain Multiplication** | `algorithms.dp.MatrixChainMultiplication` | $m[i][j] = \min_{i \le k < j}(m[i][k] + m[k+1][j] + p_{i-1}p_k p_j)$ | $O(N^3)$ Time<br>$O(N^2)$ Space | Optimal query execution pipeline parenthesization |
| **Optimal BST** | `algorithms.dp.OptimalBST` | $e[i][j] = \min_{i \le r \le j}(e[i][r-1] + e[r+1][j] + w(i,j))$ | $O(N^3)$ Time<br>$O(N^2)$ Space | Search frequency optimization for complaint keywords |
| **Held-Karp TSP Bitmask DP** | `algorithms.dp.TSPBitmaskDP` | $dp(mask, u) = \min_{v \notin mask}(dist[u][v] + dp(mask \cup \{v\}, v))$ | $O(2^N \cdot N^2)$ Time<br>$O(2^N \cdot N)$ Space | Exact optimal dispatch routing for technician inspections |
| **Hamiltonian Path Bitmask** | `algorithms.dp.HamiltonianPath` | $dp[mask][u] = \bigvee_{v \in adj(u)} dp[mask \setminus \{u\}][v]$ | $O(2^N \cdot N^2)$ Time<br>$O(2^N \cdot N)$ Space | Validating hierarchical escalation paths |
| **Tree DP & Rerooting** | `algorithms.dp.TreeDP` | 2-pass DFS subtree size and distance accumulation | $O(N)$ Time<br>$O(N)$ Space | Department hierarchy diameter and optimal central coordinator |
| **Sum Over Subsets (SOS) DP** | `algorithms.dp.SOSDP` | $F[mask][i] = F[mask][i-1] + F[mask \oplus 2^i][i-1]$ | $O(N \cdot 2^N)$ Time<br>$O(2^N)$ Space | Multi-tag complaint profile aggregation |

---

## 4. Module 4: Network Flow & Matching

| Algorithm | Class | Complexity | Theoretical Concept | Practical Integration |
|---|---|---|---|---|
| **Ford-Fulkerson** | `algorithms.flow.FordFulkerson` | $O(E \cdot |f^*|)$ | Augmenting paths via DFS, residual graph capacity updates | Complaint flow throughput modeling |
| **Edmonds-Karp** | `algorithms.flow.EdmondsKarp` | $O(V \cdot E^2)$ | BFS shortest augmenting paths (guaranteed polynomial time) | Complaint allocation capacity |
| **Dinic's Algorithm** | `algorithms.flow.DinicAlgorithm` | $O(V^2 \cdot E)$ | Level graph construction + blocking flow DFS | High-throughput technician workload distribution |
| **Max-Flow Min-Cut** | `algorithms.flow.EdmondsKarp` | $O(V \cdot E^2)$ | Weak duality $f \le C(S,T)$ & Strong duality $\max f = \min C(S,T)$ | Identifying department communication bottlenecks |
| **Hopcroft-Karp Bipartite Matching** | `algorithms.flow.BipartiteMatching` | $O(E \cdot \sqrt{V})$ | Alternating BFS level graph + maximal vertex-disjoint augmenting paths | Maximum 1-to-1 Complaint-to-Staff assignment |
| **Min-Cost Max-Flow (MCMF)** | `algorithms.flow.MinCostMaxFlow` | $O(F \cdot E \cdot V)$ | Shortest Path Faster Algorithm (SPFA) successive shortest paths | Cost and skill-minimized complaint assignment |

---

## 5. Module 5: NP-Completeness & Approximation

| Algorithm / Problem | Class | Complexity | Theoretical Concept | Practical Role |
|---|---|---|---|---|
| **Exact SAT / 3-SAT** | `algorithms.np.ExactSAT` / `Exact3SAT` | $O(2^N \cdot M)$ | DPLL backtracking, unit propagation & pure literal elimination | Validating operational resolution constraint rules |
| **Maximum Clique** | `algorithms.np.MaxClique` | $O(2^N)$ | Branch and bound pruning | Detecting tightly-coupled co-occurring complaint clusters |
| **Independent Set** | `algorithms.np.IndependentSet` | $O(2^N)$ | Complement graph reduction: $\text{IS}(G) \cong \text{Clique}(\overline{G})$ | Finding non-interfering parallel maintenance tasks |
| **Exact Minimum Vertex Cover** | `algorithms.np.ExactVertexCover` | $O(2^N)$ | Reduction theorem: $S$ is VC of $G \iff V \setminus S$ is IS of $G$ | Optimal monitoring hub selection |
| **Subset Sum** | `algorithms.np.SubsetSum` | $O(2^N)$ | Exhaustive backtracking with pruning | Matching exact repair budget and time allocations |
| **0/1 Knapsack** | `algorithms.np.Knapsack01` | $O(N \cdot W)$ | Pseudo-polynomial dynamic programming | Maximizing resolved complaint value under fixed technician hours |
| **Vertex Cover 2-Approximation** | `algorithms.approximation.VertexCoverApproximation` | $O(V + E)$ | Maximal Matching greedy selection ($\rho \le 2.0$) | Fast polynomial-time hub approximation |

---

## 6. Module 6: Randomized & Parallel Algorithms

| Algorithm | Class | Work / Span | Mathematical Foundation | Practical Purpose |
|---|---|---|---|---|
| **Randomized QuickSort** | `algorithms.randomized.RandomizedQuickSort` | Expected: $O(N \log N)$<br>Worst: $O(N^2)$ (prob $\to 0$) | Las Vegas algorithm; uniform random pivot selection avoids adversarial inputs | Guaranteed fast in-place complaint sorting |
| **Miller-Rabin Primality Test** | `algorithms.randomized.MillerRabinPrimalityTest` | Time: $O(k \log^3 N)$<br>Error: $\le (1/4)^k$ | Monte Carlo randomized test using modular exponentiation; detects Carmichael numbers | Cryptographic token generation & prime hashing |
| **Universal Randomized Hashing** | `algorithms.randomized.RandomizedHashing` | Lookup: $O(1)$ expected | $h_{a,b}(x) = ((ax + b) \bmod p) \bmod m$ with prime $p = 2^{31}-1$; collision prob $\le 1/m$ | Universal hash family preventing hash collision attacks |
| **Reservoir Sampling** | `algorithms.randomized.ReservoirSampling` | Time: $O(N)$ single pass<br>Space: $O(k)$ | Algorithm R: item $i$ selected with probability $k/i$; uniform $k/N$ overall probability | Streaming complaint audit sampling without full dataset in memory |
| **Parallel Tree Reduction** | `algorithms.parallel.ParallelReduction` | Work: $O(N)$<br>Span: $O(\log N)$ | ForkJoin framework divide-and-conquer binary tree reduction | Multi-core parallel calculation of system-wide complaint metrics |
| **Parallel Prefix Sum** | `algorithms.parallel.ParallelPrefixSum` | Work: $O(N)$<br>Span: $O(\log N)$ | Blelloch two-pass scan (Up-Sweep reduce + Down-Sweep prefix distribution) | High-speed parallel cumulative timeline computations |
| **Parallel Merge Sort** | `algorithms.parallel.ParallelSort` | Work: $O(N \log N)$<br>Span: $O(\log^2 N)$ | Recursive parallel split and merge using ForkJoin tasks | Multi-core sorting for large complaint datasets |
| **Work-Span & Brent's Theorem** | `algorithms.parallel.WorkSpanAnalysis` | Bound: $T_p \le \frac{T_1}{p} + T_\infty$ | Brent's Scheduling Principle: speedup $S_p = \frac{T_1}{T_p}$ bounded by $\min(p, \frac{T_1}{T_\infty})$ | Theoretical analysis and validation of multi-core parallelism |
