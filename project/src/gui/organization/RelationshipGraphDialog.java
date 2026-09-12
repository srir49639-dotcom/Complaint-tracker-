package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Complaint;
import repository.ComplaintRepository;
import services.RelationshipGraphService;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RelationshipGraphDialog extends JDialog {

    private final RelationshipGraphService graphService = RelationshipGraphService.getInstance();
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();

    public RelationshipGraphDialog(Frame parent) {
        super(parent, "Complaint Network & Correlation Graph", true);
        setSize(920, 620);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Complaint Correlation & Network Analysis");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Discovers inter-connected complaint clusters and priority inspection locations.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        CustomArrayList<Complaint> all = complaintRepo.findAll();
        RelationshipGraphService.GraphAnalysisResult res = graphService.buildAndAnalyzeNetwork(all);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 20, 12, 20));

        // KPI Banner
        JPanel kpi = new JPanel(new GridLayout(1, 4, 10, 0));
        kpi.setBackground(AppTheme.BG_DARK);
        kpi.add(UIUtils.createCard("Tracked Nodes", String.valueOf(res.totalNodes), AppTheme.PRIMARY));
        kpi.add(UIUtils.createCard("Correlations / Edges", String.valueOf(res.totalEdges), AppTheme.ACCENT));
        kpi.add(UIUtils.createCard("Connected Clusters", String.valueOf(res.connectedComponentsCount), AppTheme.WARNING));
        kpi.add(UIUtils.createCard("Critical Checkpoints", String.valueOf(res.criticalInspectionCheckpoints.size()), AppTheme.DANGER));
        center.add(kpi, BorderLayout.NORTH);

        // Tabbed view: 1. Related Complaint Clusters, 2. Critical Inspection Checkpoints
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        // Tab 1: Clusters Table
        String[] clusterCols = new String[]{"Cluster #", "Cluster Size", "Associated Complaints"};
        DefaultTableModel clusterModel = new DefaultTableModel(clusterCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (int i = 0; i < res.clusters.size(); i++) {
            CustomArrayList<String> comp = res.clusters.get(i);
            if (comp.size() > 1) { // show multi-complaint groups
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < comp.size(); j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(comp.get(j));
                }
                clusterModel.addRow(new Object[]{"Cluster " + (i + 1), comp.size() + " complaints", sb.toString()});
            }
        }

        JTable clusterTable = new JTable(clusterModel);
        UIUtils.styleTable(clusterTable);
        clusterTable.getColumnModel().getColumn(2).setPreferredWidth(450);
        JScrollPane scroll1 = new JScrollPane(clusterTable);
        scroll1.getViewport().setBackground(AppTheme.BG_CARD);
        scroll1.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabs.addTab("Connected Complaint Clusters (" + clusterModel.getRowCount() + ")", scroll1);

        // Tab 2: Critical Checkpoints
        String[] cpCols = new String[]{"Checkpoint #", "Tracking ID / Node", "Status in Network"};
        DefaultTableModel cpModel = new DefaultTableModel(cpCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (int i = 0; i < res.criticalInspectionCheckpoints.size(); i++) {
            cpModel.addRow(new Object[]{
                    "CP-" + (i + 1),
                    res.criticalInspectionCheckpoints.get(i),
                    "Primary incident point covering connected issue reports"
            });
        }

        JTable cpTable = new JTable(cpModel);
        UIUtils.styleTable(cpTable);
        JScrollPane scroll2 = new JScrollPane(cpTable);
        scroll2.getViewport().setBackground(AppTheme.BG_CARD);
        scroll2.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabs.addTab("Priority Inspection Checkpoints (" + res.criticalInspectionCheckpoints.size() + ")", scroll2);

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
