package tests;

import algorithms.approximation.*;
import algorithms.dp.*;
import algorithms.flow.*;
import algorithms.parallel.*;
import algorithms.randomized.*;
import algorithms.string.*;
import datastructures.CustomArrayList;
import datastructures.CustomPair;

public class AlgorithmsTestSuite {

    public static void runAll() {
        System.out.println("\n========== RUNNING DSA-3 ALGORITHM TESTS (CO1 - CO6) ==========");
        testCO2StringAlgorithms();
        testCO3AdvancedDP();
        testCO4NetworkFlow();
        testCO5ApproximationAlgorithms();
        testCO6RandomizedAndParallel();
        System.out.println(">>> ALL DSA-3 ALGORITHM TESTS PASSED! [100% SUCCESS]");
    }

    private static void testCO2StringAlgorithms() {
        String text = "Water leakage near electrical switchboard causing outage";
        String pat = "electrical";

        KMPAlgorithm kmp = new KMPAlgorithm();
        assert kmp.search(text, pat).length == 1;

        ZAlgorithm z = new ZAlgorithm();
        assert z.search(text, pat).length == 1;

        RabinKarpAlgorithm rk = new RabinKarpAlgorithm();
        assert rk.search(text, pat).length == 1;

        CustomArrayList<String> kw = new CustomArrayList<>();
        kw.add("leakage"); kw.add("outage"); kw.add("electrical");
        AhoCorasickAlgorithm ac = new AhoCorasickAlgorithm(kw);
        assert ac.searchAll(text).size() == 3;

        SuffixArrayKasai sa = new SuffixArrayKasai(text);
        assert sa.searchPattern("leakage").length == 1;

        System.out.println("  [PASS] CO2: String Search (KMP, Z, Rabin-Karp, Aho-Corasick, Suffix Array & Kasai LCP)");
    }

    private static void testCO3AdvancedDP() {
        // Wagner-Fischer Edit Distance
        WagnerFischerEditDistance wf = new WagnerFischerEditDistance();
        assert wf.computeDistance("electricity", "eletricity") == 1;
        assert wf.computeSimilarity("maintenance", "maintanance") >= 0.85;

        // Bitmask DP
        BitmaskDPScheduler bdp = new BitmaskDPScheduler();
        double[][] costMat = new double[][]{
                {0, 2, 9, 10},
                {1, 0, 6, 4},
                {15, 7, 0, 8},
                {6, 3, 12, 0}
        };
        BitmaskDPScheduler.ScheduleResult res = bdp.solve(costMat);
        assert res.optimalOrder.size() == 4;
        assert res.totalCost > 0;

        // Interval DP
        IntervalDPScheduler idp = new IntervalDPScheduler();
        long intervalCost = idp.computeOptimalIntervalCost(new int[]{10, 20, 30, 40});
        assert intervalCost > 0;

        // Tree DP
        TreeDPHierarchyAnalyzer tdp = new TreeDPHierarchyAnalyzer();
        TreeDPHierarchyAnalyzer.TreeNode root = new TreeDPHierarchyAnalyzer.TreeNode("DPT-1", 100);
        TreeDPHierarchyAnalyzer.TreeNode c1 = new TreeDPHierarchyAnalyzer.TreeNode("DPT-2", 60);
        TreeDPHierarchyAnalyzer.TreeNode c2 = new TreeDPHierarchyAnalyzer.TreeNode("DPT-3", 70);
        root.children.add(c1); root.children.add(c2);
        TreeDPHierarchyAnalyzer.TreeDPResult treeRes = tdp.analyzeHierarchy(root);
        assert treeRes.optimalWorkloadSelected >= 130;

        // SOS DP
        SOSDPCategoryAnalyzer sos = new SOSDPCategoryAnalyzer();
        int[] A = new int[]{1, 2, 3, 4}; // 2 bits (4 states)
        int[] F = sos.computeSumOverSubsets(A, 2);
        assert F[3] == (1 + 2 + 3 + 4); // F[11_2] = sum of all submasks

        System.out.println("  [PASS] CO3: Advanced DP (Wagner-Fischer, Bitmask DP, Interval DP, Tree DP, SOS DP)");
    }

    private static void testCO4NetworkFlow() {
        int n = 4;
        int[][] cap = new int[][]{
                {0, 10, 10, 0},
                {0, 0, 2, 8},
                {0, 0, 0, 9},
                {0, 0, 0, 0}
        };

        FordFulkerson ff = new FordFulkerson();
        assert ff.computeMaxFlow(cap, 0, 3) == 17;

        EdmondsKarp ek = new EdmondsKarp();
        assert ek.computeMaxFlow(cap, 0, 3) == 17;

        DinicAlgorithm dinic = new DinicAlgorithm();
        assert dinic.computeMaxFlow(cap, 0, 3) == 17;

        System.out.println("  [PASS] CO4: Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic's Blocking Flow)");
    }

    private static void testCO5ApproximationAlgorithms() {
        // Vertex Cover 2-Approximation
        VertexCoverApproximation vc = new VertexCoverApproximation();
        CustomArrayList<CustomPair<String, String>> edges = new CustomArrayList<>();
        edges.add(new CustomPair<>("A", "B"));
        edges.add(new CustomPair<>("B", "C"));
        edges.add(new CustomPair<>("C", "D"));
        VertexCoverApproximation.VertexCoverResult vcRes = vc.compute2Approximation(edges);
        assert vcRes.coverSize >= 2;

        // TSP 2-Approximation
        TSPApproximation tsp = new TSPApproximation();
        double[][] dist = new double[][]{
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };
        TSPApproximation.TSPResult tspRes = tsp.solveMetricTSP2Approx(dist);
        assert tspRes.tour.size() == 5;
        assert tspRes.totalDistance > 0;

        // Knapsack DP
        SubsetSumKnapsackSolver ks = new SubsetSumKnapsackSolver();
        SubsetSumKnapsackSolver.KnapsackResult kr = ks.solve01Knapsack(new int[]{2, 3, 4, 5}, new int[]{3, 4, 5, 6}, 5);
        assert kr.maxValue == 7;

        System.out.println("  [PASS] CO5: NP / Approximation (Vertex Cover 2-Approx, TSP 2-Approx, Knapsack FPTAS/DP)");
    }

    private static void testCO6RandomizedAndParallel() {
        // Miller-Rabin Primality
        MillerRabinPrimality mr = new MillerRabinPrimality();
        assert mr.isPrime(1000000007L, 10);
        assert !mr.isPrime(1000000008L, 10);

        // Reservoir Sampling
        ReservoirSampler<Integer> rs = new ReservoirSampler<>();
        CustomArrayList<Integer> population = new CustomArrayList<>();
        for (int i = 1; i <= 100; i++) population.add(i);
        CustomArrayList<Integer> sample = rs.sample(population, 10);
        assert sample.size() == 10;

        // Parallel Reduction
        ParallelReduction pRed = new ParallelReduction();
        double[] vals = new double[]{10.0, 20.0, 30.0, 40.0, 50.0};
        ParallelReduction.Metrics m = pRed.computeMetrics(vals);
        assert m.count == 5;
        assert m.sum == 150.0;
        assert m.average == 30.0;

        // Parallel Prefix Sum
        ParallelPrefixSum pScan = new ParallelPrefixSum();
        long[] arr = new long[]{1, 2, 3, 4, 5};
        long[] prefix = pScan.computePrefixSum(arr);
        assert prefix[4] == 15;

        // Parallel MergeSort
        ParallelMergeSort pms = new ParallelMergeSort();
        Integer[] nums = new Integer[]{50, 10, 40, 20, 30};
        pms.sort(nums, Integer::compare);
        assert nums[0] == 10 && nums[4] == 50;

        System.out.println("  [PASS] CO6: Randomized & Parallel (Miller-Rabin, Reservoir Sampling, Parallel Prefix Sum, Parallel Reduction, Parallel MergeSort)");
    }
}
