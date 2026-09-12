package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.AuditSample;
import services.AnalyticsService;
import utils.DateFormatter;
import datastructures.CustomArrayList;
import datastructures.CustomPair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AnalyticsDialog extends JDialog {

    private final AnalyticsService analyticsService = AnalyticsService.getInstance();

    public AnalyticsDialog(Frame parent) {
        super(parent, "Executive Analytics & Performance Insights", true);
        setSize(980, 680);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("System Analytics & Operational Metrics");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Aggregated complaint trends, SLA performance metrics, and audit samples.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        AnalyticsService.AnalyticsReport report = analyticsService.generateAnalyticsReport();

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Top KPIs
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiPanel.setBackground(AppTheme.BG_DARK);
        kpiPanel.add(UIUtils.createCard("Resolution Rate", String.format("%.1f%%", report.resolutionRatePercent), AppTheme.SUCCESS));
        kpiPanel.add(UIUtils.createCard("Avg Resolution Time", String.format("%.1f hrs", report.averageResolutionHours), AppTheme.ACCENT));
        kpiPanel.add(UIUtils.createCard("Customer Satisfaction", String.format("%.2f / 5.0", report.averageFeedbackRating), AppTheme.WARNING));
        kpiPanel.add(UIUtils.createCard("Overdue / Breached", String.valueOf(report.overdueComplaints), AppTheme.DANGER));
        center.add(kpiPanel, BorderLayout.NORTH);

        // Tabbed Panel: 1. Category Breakdown, 2. Audit Sampling, 3. Monthly Intake Trend
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        // Tab 1: Category Distribution
        String[] catCols = new String[]{"Service Category", "Complaint Count", "Percentage Share"};
        DefaultTableModel catModel = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        CustomArrayList<CustomPair<String, Integer>> catEntries = report.categoryDistribution.entryList();
        for (int i = 0; i < catEntries.size(); i++) {
            CustomPair<String, Integer> e = catEntries.get(i);
            double pct = report.totalComplaints > 0 ? ((double) e.getValue() / report.totalComplaints) * 100.0 : 0.0;
            catModel.addRow(new Object[]{e.getKey(), e.getValue(), String.format("%.1f%%", pct)});
        }

        JTable catTable = new JTable(catModel);
        UIUtils.styleTable(catTable);
        JScrollPane catScroll = new JScrollPane(catTable);
        catScroll.getViewport().setBackground(AppTheme.BG_CARD);
        catScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabs.addTab("Category Breakdown", catScroll);

        // Tab 2: Audit Samples
        String[] auditCols = new String[]{"Sample ID", "Tracking ID", "Title", "Category", "Department", "Status", "Audit Notes"};
        DefaultTableModel auditModel = new DefaultTableModel(auditCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (int i = 0; i < report.auditSamples.size(); i++) {
            AuditSample s = report.auditSamples.get(i);
            auditModel.addRow(new Object[]{
                    s.getSampleId(), s.getTrackingId(), s.getTitle(),
                    s.getCategory(), s.getDepartment(), s.getStatus(), s.getAuditNotes()
            });
        }

        JTable auditTable = new JTable(auditModel);
        UIUtils.styleTable(auditTable);
        JScrollPane auditScroll = new JScrollPane(auditTable);
        auditScroll.getViewport().setBackground(AppTheme.BG_CARD);
        auditScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabs.addTab("Quality Audit Samples (Randomized)", auditScroll);

        // Tab 3: Monthly Cumulative Trend
        String[] trendCols = new String[]{"Month", "Monthly Intake", "Cumulative Volume"};
        DefaultTableModel trendModel = new DefaultTableModel(trendCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        long[] baseIntake = new long[]{12, 18, 15, 24, 28, 32, 22, 35, 40, 38, 45, 50};
        for (int i = 0; i < 12; i++) {
            trendModel.addRow(new Object[]{
                    months[i],
                    baseIntake[i] + " complaints",
                    report.cumulativeComplaintTrend[i] + " total"
            });
        }

        JTable trendTable = new JTable(trendModel);
        UIUtils.styleTable(trendTable);
        JScrollPane trendScroll = new JScrollPane(trendTable);
        trendScroll.getViewport().setBackground(AppTheme.BG_CARD);
        trendScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabs.addTab("Cumulative Intake Trend", trendScroll);

        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }
}
