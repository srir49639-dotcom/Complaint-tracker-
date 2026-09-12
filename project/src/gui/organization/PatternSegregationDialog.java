package gui.organization;

import datastructures.CustomArrayList;
import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Complaint;
import model.PriorityLevel;
import model.User;
import repository.ComplaintRepository;
import services.PatternSegregationService;
import utils.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatternSegregationDialog extends JDialog {

    private final User currentUser;
    private final Runnable onDataChanged;
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private final PatternSegregationService segregationService = PatternSegregationService.getInstance();

    private DefaultListModel<String> categoryListModel;
    private JList<String> categoryJList;
    private CustomArrayList<PatternSegregationService.CategoryStat> categoryStats;

    private DefaultListModel<String> clusterListModel;
    private JList<String> clusterJList;
    private CustomArrayList<PatternSegregationService.PatternCluster> patternClusters;

    private JTextField customPatternField;
    private JComboBox<String> algoSelector;
    private JLabel matchCountLabel;

    private JLabel selectionTitleLabel;
    private JLabel kpiTotalLabel, kpiOpenLabel, kpiProgressLabel, kpiResolvedLabel, kpiCriticalLabel;
    private DefaultTableModel tableModel;
    private JTable complaintTable;
    private CustomArrayList<Complaint> currentlyDisplayedComplaints = new CustomArrayList<>();

    public PatternSegregationDialog(Frame parent, User currentUser, Runnable onDataChanged) {
        super(parent, "Complaint Category & Pattern Segregation Center", true);
        this.currentUser = currentUser;
        this.onDataChanged = onDataChanged;

        setSize(1180, 720);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
        loadData();
    }

    private void buildUI() {
        // 1. Top Header
        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("🗂️ Same-Type Complaint Segregator & Category Analytics");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Segregates identical issue types, clusters recurring problem patterns, and analyzes category distribution.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // 2. Main Content Split View
        JPanel mainContent = new JPanel(new BorderLayout(12, 12));
        mainContent.setBackground(AppTheme.BG_DARK);
        mainContent.setBorder(new EmptyBorder(10, 16, 12, 16));

        // LEFT SIDEBAR: Segregation Controls & Category/Cluster Selectors
        JPanel leftSidebar = new JPanel(new BorderLayout(8, 8));
        leftSidebar.setPreferredSize(new Dimension(360, 500));
        leftSidebar.setBackground(AppTheme.BG_SIDEBAR);
        leftSidebar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(AppTheme.BG_SIDEBAR);
        tabbedPane.setForeground(AppTheme.TEXT_PRIMARY);
        tabbedPane.setFont(AppTheme.FONT_SUBTITLE);

        // TAB 1: By Category
        categoryListModel = new DefaultListModel<>();
        categoryJList = new JList<>(categoryListModel);
        categoryJList.setBackground(AppTheme.BG_INPUT);
        categoryJList.setForeground(AppTheme.TEXT_PRIMARY);
        categoryJList.setFont(AppTheme.FONT_BODY);
        categoryJList.setSelectionBackground(AppTheme.PRIMARY);
        categoryJList.setSelectionForeground(Color.WHITE);
        categoryJList.setFixedCellHeight(36);

        JScrollPane catScroll = new JScrollPane(categoryJList);
        catScroll.getViewport().setBackground(AppTheme.BG_INPUT);
        catScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabbedPane.addTab("📁 By Category", catScroll);

        // TAB 2: Detected Issue Clusters
        clusterListModel = new DefaultListModel<>();
        clusterJList = new JList<>(clusterListModel);
        clusterJList.setBackground(AppTheme.BG_INPUT);
        clusterJList.setForeground(AppTheme.TEXT_PRIMARY);
        clusterJList.setFont(AppTheme.FONT_BODY);
        clusterJList.setSelectionBackground(AppTheme.PRIMARY);
        clusterJList.setSelectionForeground(Color.WHITE);
        clusterJList.setFixedCellHeight(36);

        JScrollPane clusterScroll = new JScrollPane(clusterJList);
        clusterScroll.getViewport().setBackground(AppTheme.BG_INPUT);
        clusterScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tabbedPane.addTab("🧩 Issue Clusters", clusterScroll);

        // TAB 3: Live Custom Pattern Search
        JPanel customSearchPanel = new JPanel(new BorderLayout(10, 10));
        customSearchPanel.setBackground(AppTheme.BG_SIDEBAR);
        customSearchPanel.setBorder(new EmptyBorder(12, 8, 12, 8));

        JPanel searchControls = new JPanel(new GridLayout(6, 1, 6, 6));
        searchControls.setBackground(AppTheme.BG_SIDEBAR);

        JLabel patternPrompt = new JLabel("Enter Keyword / Search Pattern:");
        patternPrompt.setFont(AppTheme.FONT_BODY);
        patternPrompt.setForeground(AppTheme.TEXT_PRIMARY);

        customPatternField = AppTheme.createTextField(14);
        customPatternField.putClientProperty("JTextField.placeholderText", "e.g. leak, wifi, socket, refund, projector");

        JLabel algoPrompt = new JLabel("Search Precision Mode:");
        algoPrompt.setFont(AppTheme.FONT_BODY);
        algoPrompt.setForeground(AppTheme.TEXT_PRIMARY);

        String[] algos = new String[]{
                "Smart Multi-Keyword Match",
                "Exact Substring Match",
                "Extended Phrase Match",
                "Fast Linear Scan"
        };
        algoSelector = new JComboBox<>(algos);
        algoSelector.setBackground(AppTheme.BG_INPUT);
        algoSelector.setForeground(AppTheme.TEXT_PRIMARY);

        JButton runPatternBtn = AppTheme.createPrimaryButton("⚡ Segregate by Pattern");
        matchCountLabel = new JLabel("Enter pattern above to segregate", SwingConstants.CENTER);
        matchCountLabel.setFont(AppTheme.FONT_SMALL);
        matchCountLabel.setForeground(AppTheme.TEXT_SECONDARY);

        searchControls.add(patternPrompt);
        searchControls.add(customPatternField);
        searchControls.add(algoPrompt);
        searchControls.add(algoSelector);
        searchControls.add(runPatternBtn);
        searchControls.add(matchCountLabel);

        customSearchPanel.add(searchControls, BorderLayout.NORTH);

        JTextArea algoInfo = new JTextArea();
        algoInfo.setEditable(false);
        algoInfo.setLineWrap(true);
        algoInfo.setWrapStyleWord(true);
        algoInfo.setBackground(AppTheme.BG_CARD);
        algoInfo.setForeground(AppTheme.TEXT_MUTED);
        algoInfo.setFont(AppTheme.FONT_SMALL);
        algoInfo.setBorder(new EmptyBorder(8, 8, 8, 8));
        algoInfo.setText("💡 Pattern Segregation:\nAutomatically discovers and isolates complaints sharing common keywords, phrases, or infrastructure components across all campus departments.");

        customSearchPanel.add(algoInfo, BorderLayout.CENTER);
        tabbedPane.addTab("🔍 Custom Pattern", customSearchPanel);

        leftSidebar.add(tabbedPane, BorderLayout.CENTER);
        mainContent.add(leftSidebar, BorderLayout.WEST);

        // RIGHT PANEL: Segregated Complaints View & Analytical KPI Chips
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(AppTheme.BG_DARK);

        // Top Status Header for Selected Group
        JPanel selectionHeader = new JPanel(new BorderLayout(10, 10));
        selectionHeader.setBackground(AppTheme.BG_CARD);
        selectionHeader.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        selectionTitleLabel = new JLabel("All Categories / All Complaints");
        selectionTitleLabel.setFont(AppTheme.FONT_TITLE);
        selectionTitleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel kpiRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        kpiRow.setBackground(AppTheme.BG_CARD);

        kpiTotalLabel = createChip("Total: 0", AppTheme.PRIMARY);
        kpiOpenLabel = createChip("New/Open: 0", AppTheme.ACCENT);
        kpiProgressLabel = createChip("In Progress: 0", AppTheme.WARNING);
        kpiResolvedLabel = createChip("Resolved: 0", AppTheme.SUCCESS);
        kpiCriticalLabel = createChip("Critical/High: 0", AppTheme.DANGER);

        kpiRow.add(kpiTotalLabel);
        kpiRow.add(kpiOpenLabel);
        kpiRow.add(kpiProgressLabel);
        kpiRow.add(kpiResolvedLabel);
        kpiRow.add(kpiCriticalLabel);

        selectionHeader.add(selectionTitleLabel, BorderLayout.NORTH);
        selectionHeader.add(kpiRow, BorderLayout.SOUTH);
        rightPanel.add(selectionHeader, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"Tracking ID", "Customer", "Category", "Title", "Location", "Priority", "Status", "Created At"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintTable = new JTable(tableModel);
        UIUtils.styleTable(complaintTable);
        complaintTable.getColumnModel().getColumn(3).setPreferredWidth(220);

        JScrollPane tableScroll = new JScrollPane(complaintTable);
        tableScroll.getViewport().setBackground(AppTheme.BG_CARD);
        tableScroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        mainContent.add(rightPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // 3. Footer Bar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton viewDetailBtn = AppTheme.createSecondaryButton("🔍 View Complaint Details");
        JButton refreshBtn = AppTheme.createSecondaryButton("🔄 Refresh Data");
        JButton closeBtn = AppTheme.createPrimaryButton("Close");

        footer.add(viewDetailBtn);
        footer.add(refreshBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // Event Handlers
        categoryJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = categoryJList.getSelectedIndex();
                if (idx >= 0 && idx < categoryStats.size()) {
                    displayCategory(categoryStats.get(idx));
                }
            }
        });

        clusterJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = clusterJList.getSelectedIndex();
                if (idx >= 0 && idx < patternClusters.size()) {
                    displayCluster(patternClusters.get(idx));
                }
            }
        });

        runPatternBtn.addActionListener(e -> executeCustomPatternSearch());
        customPatternField.addActionListener(e -> executeCustomPatternSearch());

        complaintTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedComplaintDetail();
                }
            }
        });

        viewDetailBtn.addActionListener(e -> openSelectedComplaintDetail());

        refreshBtn.addActionListener(e -> {
            loadData();
            if (onDataChanged != null) onDataChanged.run();
        });

        closeBtn.addActionListener(e -> dispose());
    }

    private JLabel createChip(String text, Color bg) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    private void loadData() {
        CustomArrayList<Complaint> all = complaintRepo.findAll();

        // 1. Load Categories
        categoryStats = segregationService.getCategoryBreakdown(all);
        categoryListModel.clear();
        categoryListModel.addElement("⭐ [All Categories Combined] (" + all.size() + " total)");
        for (int i = 0; i < categoryStats.size(); i++) {
            PatternSegregationService.CategoryStat stat = categoryStats.get(i);
            categoryListModel.addElement(String.format("📁 %s [%d] (%.1f%%)", stat.categoryName, stat.count, stat.percentage));
        }

        // 2. Load Predefined Pattern Clusters (Aho-Corasick)
        patternClusters = segregationService.getPredefinedPatternClusters(all);
        clusterListModel.clear();
        for (int i = 0; i < patternClusters.size(); i++) {
            PatternSegregationService.PatternCluster cl = patternClusters.get(i);
            clusterListModel.addElement(String.format("%s %s [%d complaints]", cl.icon, cl.clusterName, cl.matchingComplaints.size()));
        }

        // Default: display all complaints
        displayAll(all);
    }

    private void displayAll(CustomArrayList<Complaint> all) {
        selectionTitleLabel.setText("All Registered Complaints (" + all.size() + " Total)");
        updateKPIs(all);
        populateTable(all);
    }

    private void displayCategory(PatternSegregationService.CategoryStat stat) {
        int idx = categoryJList.getSelectedIndex();
        if (idx == 0) {
            displayAll(complaintRepo.findAll());
            return;
        }

        CustomArrayList<Complaint> all = complaintRepo.findAll();
        CustomArrayList<Complaint> filtered = segregationService.getComplaintsByCategory(all, stat.categoryName);

        selectionTitleLabel.setText("📁 Category: " + stat.categoryName + " (" + filtered.size() + " complaints • " + String.format("%.1f%%", stat.percentage) + ")");
        updateKPIs(filtered);
        populateTable(filtered);
    }

    private void displayCluster(PatternSegregationService.PatternCluster cluster) {
        selectionTitleLabel.setText(cluster.icon + " Issue Pattern: " + cluster.clusterName + " (" + cluster.matchingComplaints.size() + " matching complaints)");
        updateKPIs(cluster.matchingComplaints);
        populateTable(cluster.matchingComplaints);
    }

    private void executeCustomPatternSearch() {
        String pattern = customPatternField.getText().trim();
        if (pattern.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword or pattern.", "Empty Pattern", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mode = (String) algoSelector.getSelectedItem();
        CustomArrayList<Complaint> all = complaintRepo.findAll();
        CustomArrayList<Complaint> matches = segregationService.segregateByCustomPattern(all, pattern, mode);

        matchCountLabel.setText("Search completed — " + matches.size() + " matching complaints found.");
        selectionTitleLabel.setText("🔍 Matching Issue Pattern: \"" + pattern + "\" (" + matches.size() + " complaints found)");

        updateKPIs(matches);
        populateTable(matches);
    }

    private void updateKPIs(CustomArrayList<Complaint> list) {
        int total = list.size();
        int open = 0, inProg = 0, resolved = 0, crit = 0;

        for (int i = 0; i < list.size(); i++) {
            Complaint c = list.get(i);
            switch (c.getStatus()) {
                case NEW:
                case ASSIGNED:
                case REOPENED:
                    open++;
                    break;
                case IN_PROGRESS:
                case WAITING_FOR_CUSTOMER:
                case ESCALATED:
                    inProg++;
                    break;
                case RESOLVED:
                case CLOSED:
                    resolved++;
                    break;
            }

            if (c.getPriority() == PriorityLevel.CRITICAL || c.getPriority() == PriorityLevel.HIGH) {
                crit++;
            }
        }

        kpiTotalLabel.setText("  Total: " + total + "  ");
        kpiOpenLabel.setText("  New/Open: " + open + "  ");
        kpiProgressLabel.setText("  In Progress: " + inProg + "  ");
        kpiResolvedLabel.setText("  Resolved: " + resolved + "  ");
        kpiCriticalLabel.setText("  Critical/High: " + crit + "  ");
    }

    private void populateTable(CustomArrayList<Complaint> list) {
        currentlyDisplayedComplaints = list;
        tableModel.setRowCount(0);

        for (int i = 0; i < list.size(); i++) {
            Complaint c = list.get(i);
            tableModel.addRow(new Object[]{
                    c.getTrackingId(),
                    c.getCustomerName(),
                    c.getCategory(),
                    c.getTitle(),
                    c.getLocation() != null ? c.getLocation() : "N/A",
                    c.getPriority().name(),
                    c.getStatus().getDisplayName(),
                    DateFormatter.formatDateTime(c.getCreatedTimestamp())
            });
        }
    }

    private void openSelectedComplaintDetail() {
        int row = complaintTable.getSelectedRow();
        if (row >= 0 && row < currentlyDisplayedComplaints.size()) {
            Complaint selected = currentlyDisplayedComplaints.get(row);
            new OrgComplaintDetailDialog(this, selected, currentUser, () -> {
                loadData();
                if (onDataChanged != null) onDataChanged.run();
            }).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a complaint row from the table first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
