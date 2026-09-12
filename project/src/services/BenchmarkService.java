package services;

import algorithms.approximation.JobSchedulingApproximation;
import algorithms.approximation.VertexCoverApproximation;
import algorithms.dp.BitmaskDPScheduler;
import algorithms.dp.WagnerFischerEditDistance;
import algorithms.flow.DinicAlgorithm;
import algorithms.flow.EdmondsKarp;
import algorithms.flow.FordFulkerson;
import algorithms.parallel.ParallelPrefixSum;
import algorithms.parallel.ParallelReduction;
import algorithms.randomized.MillerRabinPrimality;
import algorithms.randomized.RandomizedQuickSort;
import algorithms.string.KMPAlgorithm;
import algorithms.string.RabinKarpAlgorithm;
import algorithms.string.SuffixArrayKasai;
import algorithms.string.ZAlgorithm;
import datastructures.CustomArrayList;
import datastructures.CustomPair;
import model.Complaint;
import model.Staff;

public class BenchmarkService {

    private static BenchmarkService instance;

    public static synchronized BenchmarkService getInstance() {
        if (instance == null) {
            instance = new BenchmarkService();
        }
        return instance;
    }

    public static class BenchmarkResult {
        public final String courseOutcome;
        public final String algorithmName;
        public final String inputSizeDescription;
        public final long executionTimeMicros;
        public final String theoreticalComplexity;

        public BenchmarkResult(String courseOutcome, String algorithmName, String inputSizeDescription,
                               long executionTimeMicros, String theoreticalComplexity) {
            this.courseOutcome = courseOutcome;
            this.algorithmName = algorithmName;
            this.inputSizeDescription = inputSizeDescription;
            this.executionTimeMicros = executionTimeMicros;
            this.theoreticalComplexity = theoreticalComplexity;
        }
    }

    public CustomArrayList<BenchmarkResult> runAllBenchmarks() {
        CustomArrayList<BenchmarkResult> results = new CustomArrayList<>();

        // 1. CO2: String Searching Algorithms Benchmark
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("Water leakage reported in hostel block A corridor near electrical board and elevator. ");
        }
        String corpus = sb.toString();
        String pattern = "electrical board";

        // KMP
        KMPAlgorithm kmp = new KMPAlgorithm();
        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) kmp.search(corpus, pattern);
        long kmpMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO2 - String Algorithms", "KMP Algorithm", "Text: ~40KB, Pattern: 16 chars (100 runs)", kmpMicros, "O(N + M)"));

        // Z-Algorithm
        ZAlgorithm z = new ZAlgorithm();
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) z.search(corpus, pattern);
        long zMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO2 - String Algorithms", "Z-Algorithm", "Text: ~40KB, Pattern: 16 chars (100 runs)", zMicros, "O(N + M)"));

        // Rabin-Karp
        RabinKarpAlgorithm rk = new RabinKarpAlgorithm();
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) rk.search(corpus, pattern);
        long rkMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO2 - String Algorithms", "Rabin-Karp Rolling Hash", "Text: ~40KB, Pattern: 16 chars (100 runs)", rkMicros, "Average O(N + M)"));

        // Suffix Array Kasai
        start = System.nanoTime();
        SuffixArrayKasai sa = new SuffixArrayKasai(corpus.substring(0, 1000));
        for (int i = 0; i < 100; i++) sa.searchPattern(pattern);
        long saMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO2 - String Algorithms", "Suffix Array & Kasai LCP", "Text: 1000 chars, Pattern: 16 chars (100 runs)", saMicros, "Search O(M log N)"));

        // 2. CO3: Advanced DP Benchmark
        WagnerFischerEditDistance wf = new WagnerFischerEditDistance();
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) wf.computeDistance("electricity outage in engineering block", "eletricity outag in enginering blok");
        long wfMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO3 - Advanced DP", "Wagner-Fischer Edit Distance", "Two 40-char strings (1000 runs)", wfMicros, "O(M * N)"));

        BitmaskDPScheduler bitmask = new BitmaskDPScheduler();
        double[][] costMat = new double[12][12];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) costMat[i][j] = (i == j) ? 0 : (Math.abs(i - j) * 1.5 + 2.0);
        }
        start = System.nanoTime();
        bitmask.solve(costMat);
        long bmMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO3 - Advanced DP", "Bitmask DP (Held-Karp TSP)", "N=12 Complaint routing nodes", bmMicros, "O(2^N * N^2)"));

        // 3. CO4: Network Flow Benchmark
        int n = 30;
        int[][] capMat = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < Math.min(n, i + 6); j++) capMat[i][j] = 10;
        }

        DinicAlgorithm dinic = new DinicAlgorithm();
        start = System.nanoTime();
        for (int i = 0; i < 50; i++) dinic.computeMaxFlow(capMat, 0, n - 1);
        long dinicMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO4 - Network Flow", "Dinic Blocking Flow", "V=30, E=120 Flow Network (50 runs)", dinicMicros, "O(V^2 * E)"));

        EdmondsKarp ek = new EdmondsKarp();
        start = System.nanoTime();
        for (int i = 0; i < 50; i++) ek.computeMaxFlow(capMat, 0, n - 1);
        long ekMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO4 - Network Flow", "Edmonds-Karp BFS Flow", "V=30, E=120 Flow Network (50 runs)", ekMicros, "O(V * E^2)"));

        FordFulkerson ff = new FordFulkerson();
        start = System.nanoTime();
        for (int i = 0; i < 50; i++) ff.computeMaxFlow(capMat, 0, n - 1);
        long ffMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO4 - Network Flow", "Ford-Fulkerson DFS Flow", "V=30, E=120 Flow Network (50 runs)", ffMicros, "O(E * MaxFlow)"));

        // 4. CO5: NP / Approximation Benchmark
        JobSchedulingApproximation jobSched = new JobSchedulingApproximation();
        CustomArrayList<Complaint> testComplaints = new CustomArrayList<>();
        for (int i = 0; i < 50; i++) {
            Complaint c = new Complaint();
            c.setEstimatedEffortHours((i % 8) + 1);
            testComplaints.add(c);
        }
        CustomArrayList<Staff> testStaff = new CustomArrayList<>();
        for (int i = 0; i < 10; i++) {
            Staff st = new Staff();
            st.setStaffId("S-" + i);
            testStaff.add(st);
        }

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) jobSched.schedule(testComplaints, testStaff);
        long schedMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO5 - Approximation", "Graham LPT 4/3-Approximation", "50 Complaints, 10 Staff (100 runs)", schedMicros, "O(N log N + N log M)"));

        VertexCoverApproximation vc = new VertexCoverApproximation();
        CustomArrayList<CustomPair<String, String>> edges = new CustomArrayList<>();
        for (int i = 0; i < 100; i++) {
            edges.add(new CustomPair<>("Node-" + (i % 20), "Node-" + ((i + 3) % 20)));
        }
        start = System.nanoTime();
        for (int i = 0; i < 200; i++) vc.compute2Approximation(edges);
        long vcMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO5 - Approximation", "Vertex Cover 2-Approximation", "100 Edges, 20 Nodes (200 runs)", vcMicros, "O(V + E)"));

        // 5. CO6: Randomized & Parallel Benchmark
        MillerRabinPrimality mr = new MillerRabinPrimality();
        start = System.nanoTime();
        for (int i = 0; i < 500; i++) mr.isPrime(1000000007L, 10);
        long mrMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO6 - Randomized", "Miller-Rabin Primality Test", "Prime N=10^9+7, 10 rounds (500 runs)", mrMicros, "O(k * log^3 N)"));

        ParallelReduction pRed = new ParallelReduction();
        double[] redArray = new double[10000];
        for (int i = 0; i < 10000; i++) redArray[i] = i * 1.5;
        start = System.nanoTime();
        for (int i = 0; i < 50; i++) pRed.computeMetrics(redArray);
        long redMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO6 - Parallel", "Parallel Reduction (ForkJoin)", "10,000 double array elements (50 runs)", redMicros, "Work: O(N), Span: O(log N)"));

        ParallelPrefixSum pScan = new ParallelPrefixSum();
        long[] scanArray = new long[10000];
        for (int i = 0; i < 10000; i++) scanArray[i] = i + 1;
        start = System.nanoTime();
        for (int i = 0; i < 50; i++) pScan.computePrefixSum(scanArray);
        long scanMicros = (System.nanoTime() - start) / 1000;
        results.add(new BenchmarkResult("CO6 - Parallel", "Parallel Prefix Sum Scan", "10,000 long array elements (50 runs)", scanMicros, "Work: O(N), Span: O(log N)"));

        return results;
    }
}
