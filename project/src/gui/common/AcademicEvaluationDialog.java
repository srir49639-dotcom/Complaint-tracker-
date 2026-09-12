package gui.common;

import services.BenchmarkService;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AcademicEvaluationDialog extends JDialog {

    private final BenchmarkService benchmarkService = BenchmarkService.getInstance();

    public AcademicEvaluationDialog(Frame parent) {
        super(parent, "DSA-3 Academic Course Outcomes (CO1 - CO6) Coverage Inspector", true);
        setSize(950, 680);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("DSA-3 Course Outcomes & Algorithmic Integration");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Academic Evaluation View — Maps all Course Outcomes (CO1 - CO6) to custom Java implementations.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Tabbed Panel: 1. CO Mapping Table, 2. Live Benchmarks
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        tabs.addTab("Course Outcome Mapping (CO1 - CO6)", createMappingPanel());
        tabs.addTab("Live Empirical Benchmarks", createBenchmarkPanel());

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_DARK);
        JButton closeBtn = AppTheme.createPrimaryButton("Close Inspector");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createMappingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(AppTheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = new String[]{"Course Outcome", "Core Topic", "Java Classes", "Custom Data Structure", "Complexity", "Application Integration"};
        String[][] data = new String[][]{
                {"CO1", "Problem Classification & Strategy Selection", "SearchEngineService, SchedulingStrategy", "Strategy Pattern / HashTable", "O(1) Dispatch", "Dynamic selection of search & scheduling strategy based on problem size"},
                {"CO2", "Advanced String Searching", "KMPAlgorithm, ZAlgorithm, RabinKarpAlgorithm, AhoCorasick, SuffixArrayKasai", "Trie, Suffix Array, LPS Array", "O(N + M) / O(M log N)", "Complaint search, multi-keyword categorization, pattern detection"},
                {"CO3", "Advanced Dynamic Programming", "WagnerFischerEditDistance, BitmaskDPScheduler, IntervalDPScheduler, TreeDPHierarchyAnalyzer, SOSDPCategoryAnalyzer", "2D/3D DP Tables, Tree Nodes, Bitmasks", "O(M*N), O(2^N*N^2), O(N^3)", "Fuzzy complaint search & typo correction, complaint routing, hierarchy impact analysis"},
                {"CO4", "Network Flow & Matching", "DinicAlgorithm, EdmondsKarp, FordFulkerson, BipartiteMatchingAssignment, MinCostMaxFlow", "Adjacency Residual Graphs, Level Graph", "O(V^2 * E), O(V * E^2)", "Automated capacity matching between complaints and staff under workload limits"},
                {"CO5", "NP / Approximation Algorithms", "JobSchedulingApproximation, VertexCoverApproximation, TSPApproximation, SubsetSumKnapsackSolver", "Max-Heap, Min-Heap, Priority Queue", "O(V + E), O(N log N)", "Makespan work scheduling (Exact for small N vs LPT 4/3-Approx for large N), minimum inspection points"},
                {"CO6", "Randomized + Parallel Computing", "RandomizedQuickSort, MillerRabinPrimality, ReservoirSampler, ParallelPrefixSum, ParallelReduction, ParallelMergeSort", "ForkJoin Recursive Tasks, Sample Arrays", "Work: O(N), Span: O(log N)", "Parallel analytics reduction, cumulative trend scans, uniform audit complaint sampling"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(260);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBenchmarkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(AppTheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppTheme.BG_DARK);

        JLabel lbl = new JLabel("Live Microsecond Benchmark Profiler (Empirical Execution Times)");
        lbl.setFont(AppTheme.FONT_SUBTITLE);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);

        JButton runBtn = AppTheme.createPrimaryButton("⚡ Run Live Benchmarks");
        topBar.add(lbl, BorderLayout.WEST);
        topBar.add(runBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = new String[]{"Course Outcome", "Algorithm Tested", "Test Dataset Description", "Time (Microseconds)", "Theoretical Complexity"};
        DefaultTableModel benchModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable benchTable = new JTable(benchModel);
        UIUtils.styleTable(benchTable);
        JScrollPane scroll = new JScrollPane(benchTable);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);

        runBtn.addActionListener(e -> {
            runBtn.setEnabled(false);
            runBtn.setText("Running...");
            SwingUtilities.invokeLater(() -> {
                benchModel.setRowCount(0);
                CustomArrayList<BenchmarkService.BenchmarkResult> results = benchmarkService.runAllBenchmarks();
                for (int i = 0; i < results.size(); i++) {
                    BenchmarkService.BenchmarkResult br = results.get(i);
                    benchModel.addRow(new Object[]{
                            br.courseOutcome,
                            br.algorithmName,
                            br.inputSizeDescription,
                            br.executionTimeMicros + " µs",
                            br.theoreticalComplexity
                    });
                }
                runBtn.setEnabled(true);
                runBtn.setText("⚡ Re-Run Live Benchmarks");
            });
        });

        return panel;
    }
}
