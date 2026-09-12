package gui.organization;

import algorithms.flow.BipartiteMatchingAssignment;
import gui.common.AppTheme;
import gui.common.UIUtils;
import model.*;
import repository.ComplaintRepository;
import repository.NotificationRepository;
import services.AssignmentEngineService;
import services.ComplaintService;
import services.PriorityEngineService;
import services.SearchEngineService;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OrgDashboardFrame extends JFrame {

    private final User currentUser;
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private final NotificationRepository notifRepo = NotificationRepository.getInstance();
    private final SearchEngineService searchEngine = SearchEngineService.getInstance();
    private final PriorityEngineService priorityEngine = PriorityEngineService.getInstance();
    private final AssignmentEngineService assignmentEngine = AssignmentEngineService.getInstance();

    private DefaultTableModel tableModel;
    private JTable complaintsTable;
    private CustomArrayList<Complaint> displayedComplaints = new CustomArrayList<>();

    private JLabel totalCardVal, newCardVal, assignedCardVal, progressCardVal, resolvedCardVal, overdueCardVal, escalatedCardVal;
    private JComboBox<String> statusFilterCombo, priorityFilterCombo, categoryFilterCombo;
    private JTextField searchField;

    public OrgDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Organization Portal — Smart Complaint Tracker");
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
        refreshData();
    }

    private void buildUI() {
        // 1. Top Navigation Bar
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(AppTheme.BG_SIDEBAR);
        topNav.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userPanel.setBackground(AppTheme.BG_SIDEBAR);

        JLabel welcome = new JLabel(currentUser.getFullName());
        welcome.setFont(AppTheme.FONT_TITLE);
        welcome.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel roleBadge = new JLabel("  " + currentUser.getRole().getTitle() + "  ");
        roleBadge.setFont(AppTheme.FONT_SMALL);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(new Color(30, 58, 138));
        roleBadge.setForeground(Color.WHITE);

        userPanel.add(welcome);
        userPanel.add(roleBadge);
        topNav.add(userPanel, BorderLayout.WEST);

        JPanel navActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navActions.setBackground(AppTheme.BG_SIDEBAR);

        JButton logoutBtn = AppTheme.createPrimaryButton("Logout");

        navActions.add(logoutBtn);
        topNav.add(navActions, BorderLayout.EAST);
        add(topNav, BorderLayout.NORTH);

        // 2. Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout(12, 12));
        mainContent.setBackground(AppTheme.BG_DARK);
        mainContent.setBorder(new EmptyBorder(12, 24, 16, 24));

        // Metric Cards Panel
        JPanel metricsPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        metricsPanel.setBackground(AppTheme.BG_DARK);

        JPanel c1 = UIUtils.createCard("Total", "0", AppTheme.PRIMARY);
        JPanel c2 = UIUtils.createCard("New", "0", AppTheme.ACCENT);
        JPanel c3 = UIUtils.createCard("Assigned", "0", new Color(59, 130, 246));
        JPanel c4 = UIUtils.createCard("In Progress", "0", AppTheme.WARNING);
        JPanel c5 = UIUtils.createCard("Resolved", "0", AppTheme.SUCCESS);
        JPanel c6 = UIUtils.createCard("Overdue", "0", AppTheme.DANGER);
        JPanel c7 = UIUtils.createCard("Escalated", "0", new Color(236, 72, 153));

        totalCardVal = (JLabel) c1.getComponent(1);
        newCardVal = (JLabel) c2.getComponent(1);
        assignedCardVal = (JLabel) c3.getComponent(1);
        progressCardVal = (JLabel) c4.getComponent(1);
        resolvedCardVal = (JLabel) c5.getComponent(1);
        overdueCardVal = (JLabel) c6.getComponent(1);
        escalatedCardVal = (JLabel) c7.getComponent(1);

        metricsPanel.add(c1); metricsPanel.add(c2); metricsPanel.add(c3);
        metricsPanel.add(c4); metricsPanel.add(c5); metricsPanel.add(c6);
        metricsPanel.add(c7);

        mainContent.add(metricsPanel, BorderLayout.NORTH);

        // Center Panel: Management Action Buttons + Search/Filters + Table
        JPanel centerStack = new JPanel(new BorderLayout(10, 10));
        centerStack.setBackground(AppTheme.BG_DARK);

        // Top Action Bar (Operational Tools)
        JPanel actionToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionToolBar.setBackground(AppTheme.BG_DARK);

        JButton autoAssignBtn = AppTheme.createPrimaryButton("⚡ Auto-Assign Queue");
        JButton prioritizeBtn = AppTheme.createSecondaryButton("🔥 Prioritize Queue");
        JButton segregateBtn = AppTheme.createSecondaryButton("🗂️ Category & Pattern Segregator");
        JButton scheduleBtn = AppTheme.createSecondaryButton("📅 Work Schedule");
        JButton deptsBtn = AppTheme.createSecondaryButton("🏢 Departments");
        JButton staffBtn = AppTheme.createSecondaryButton("👥 Staff");
        JButton graphBtn = AppTheme.createSecondaryButton("🕸️ Network Graph");
        JButton analyticsBtn = AppTheme.createSecondaryButton("📊 Analytics");
        JButton reportsBtn = AppTheme.createSecondaryButton("📄 Reports");
        JButton filesBtn = AppTheme.createSecondaryButton("💾 Files & Backup");

        actionToolBar.add(autoAssignBtn);
        actionToolBar.add(prioritizeBtn);
        actionToolBar.add(segregateBtn);
        actionToolBar.add(scheduleBtn);
        actionToolBar.add(deptsBtn);
        actionToolBar.add(staffBtn);
        actionToolBar.add(graphBtn);
        actionToolBar.add(analyticsBtn);
        actionToolBar.add(reportsBtn);
        actionToolBar.add(filesBtn);

        // Search & Multi-Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.setBackground(AppTheme.BG_DARK);

        searchField = AppTheme.createTextField(16);
        searchField.putClientProperty("JTextField.placeholderText", "Search all complaints...");
        JButton searchBtn = AppTheme.createSecondaryButton("Search");

        String[] statuses = new String[]{"All Statuses", "NEW", "ASSIGNED", "IN_PROGRESS", "WAITING_FOR_CUSTOMER", "ESCALATED", "RESOLVED", "CLOSED", "REOPENED"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setBackground(AppTheme.BG_INPUT);
        statusFilterCombo.setForeground(AppTheme.TEXT_PRIMARY);

        String[] priorities = new String[]{"All Priorities", "CRITICAL", "HIGH", "MEDIUM", "LOW"};
        priorityFilterCombo = new JComboBox<>(priorities);
        priorityFilterCombo.setBackground(AppTheme.BG_INPUT);
        priorityFilterCombo.setForeground(AppTheme.TEXT_PRIMARY);

        String[] cats = new String[]{"All Categories", "Internet / Wi-Fi", "Water & Plumbing", "Electricity / Power", "Hostel Facilities", "Academic / Classroom", "Finance & Fees", "Security & Access", "Transport / Bus", "Library Services", "Food Services / Canteen", "Infrastructure & Maintenance", "Administration"};
        categoryFilterCombo = new JComboBox<>(cats);
        categoryFilterCombo.setBackground(AppTheme.BG_INPUT);
        categoryFilterCombo.setForeground(AppTheme.TEXT_PRIMARY);

        JButton resetFilterBtn = AppTheme.createSecondaryButton("🔄 Reset");

        filterBar.add(searchField);
        filterBar.add(searchBtn);
        filterBar.add(new JLabel(" "));
        filterBar.add(statusFilterCombo);
        filterBar.add(priorityFilterCombo);
        filterBar.add(categoryFilterCombo);
        filterBar.add(resetFilterBtn);

        JPanel controls = new JPanel(new BorderLayout(4, 4));
        controls.setBackground(AppTheme.BG_DARK);
        controls.add(actionToolBar, BorderLayout.NORTH);
        controls.add(filterBar, BorderLayout.SOUTH);
        centerStack.add(controls, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"Tracking ID", "Customer", "Title", "Category", "Department", "Staff", "Priority", "Status", "Escalation", "Deadline", "DSA Strategy"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintsTable = new JTable(tableModel);
        UIUtils.styleTable(complaintsTable);
        complaintsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        complaintsTable.getColumnModel().getColumn(10).setPreferredWidth(170);

        JScrollPane scroll = new JScrollPane(complaintsTable);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        centerStack.add(scroll, BorderLayout.CENTER);

        mainContent.add(centerStack, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Event Handlers
        complaintsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = complaintsTable.getSelectedRow();
                    if (row >= 0 && row < displayedComplaints.size()) {
                        Complaint sel = displayedComplaints.get(row);
                        new OrgComplaintDetailDialog(OrgDashboardFrame.this, sel, currentUser, () -> refreshData()).setVisible(true);
                    }
                }
            }
        });

        searchBtn.addActionListener(e -> applyFilters());
        searchField.addActionListener(e -> applyFilters());
        statusFilterCombo.addActionListener(e -> applyFilters());
        priorityFilterCombo.addActionListener(e -> applyFilters());
        categoryFilterCombo.addActionListener(e -> applyFilters());

        resetFilterBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilterCombo.setSelectedIndex(0);
            priorityFilterCombo.setSelectedIndex(0);
            categoryFilterCombo.setSelectedIndex(0);
            refreshData();
        });

        autoAssignBtn.addActionListener(e -> {
            CustomArrayList<Complaint> unassigned = new CustomArrayList<>();
            CustomArrayList<Complaint> all = complaintRepo.findAll();
            for (int i = 0; i < all.size(); i++) {
                Complaint c = all.get(i);
                if (c.getStatus() == ComplaintStatus.NEW || c.getAssignedStaffId() == null || c.getAssignedStaffId().isEmpty()) {
                    unassigned.add(c);
                }
            }

            if (unassigned.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No unassigned complaints in queue.", "Queue Clean", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            BipartiteMatchingAssignment.AssignmentResult res = assignmentEngine.runBatchAssignment(unassigned);
            for (int i = 0; i < res.matchedPairs.size(); i++) {
                Complaint cmp = res.matchedPairs.get(i).getKey();
                Staff st = res.matchedPairs.get(i).getValue();
                ComplaintService.getInstance().assignComplaint(cmp.getComplaintId(), st.getDepartmentName(), st.getStaffId(), currentUser.getUserId(), currentUser.getFullName(), "Assigned via optimal workload balancer.");
            }

            JOptionPane.showMessageDialog(this,
                    "Optimal assignment generated!\n" + res.totalMatched + " complaints assigned across available staff." +
                            (res.unassignedCount > 0 ? "\n" + res.unassignedCount + " complaints remain unassigned due to staff capacity limits." : ""),
                    "Auto-Assignment Complete", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        });

        prioritizeBtn.addActionListener(e -> {
            CustomArrayList<Complaint> active = new CustomArrayList<>();
            CustomArrayList<Complaint> all = complaintRepo.findAll();
            for (int i = 0; i < all.size(); i++) {
                if (!all.get(i).isResolvedOrClosed()) active.add(all.get(i));
            }
            displayedComplaints = priorityEngine.getTopPrioritizedComplaints(active, active.size());
            populateTable(displayedComplaints);
            JOptionPane.showMessageDialog(this, "Complaints prioritized by severity, SLA risk, and escalation levels.", "Priority Calculated", JOptionPane.INFORMATION_MESSAGE);
        });

        segregateBtn.addActionListener(e -> new PatternSegregationDialog(this, currentUser, () -> refreshData()).setVisible(true));
        scheduleBtn.addActionListener(e -> new SchedulePlanningDialog(this).setVisible(true));
        deptsBtn.addActionListener(e -> new DepartmentManagementDialog(this).setVisible(true));
        staffBtn.addActionListener(e -> new StaffManagementDialog(this).setVisible(true));
        graphBtn.addActionListener(e -> new RelationshipGraphDialog(this).setVisible(true));
        analyticsBtn.addActionListener(e -> new AnalyticsDialog(this).setVisible(true));
        reportsBtn.addActionListener(e -> new ReportGenerationDialog(this).setVisible(true));
        filesBtn.addActionListener(e -> new FileManagementDialog(this).setVisible(true));

        logoutBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of the organization portal?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                new OrgLoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    public void refreshData() {
        complaintRepo.reloadFromFile();
        CustomArrayList<Complaint> all = complaintRepo.findAll();
        all.sort((c1, c2) -> Long.compare(c2.getUpdatedTimestamp(), c1.getUpdatedTimestamp()));
        displayedComplaints = all;
        populateTable(displayedComplaints);

        // Update cards
        int total = all.size();
        int newC = 0, assignedC = 0, inProgC = 0, resC = 0, overdueC = 0, escC = 0;

        for (int i = 0; i < all.size(); i++) {
            Complaint c = all.get(i);
            if (c.getStatus() == ComplaintStatus.NEW) newC++;
            else if (c.getStatus() == ComplaintStatus.ASSIGNED) assignedC++;
            else if (c.getStatus() == ComplaintStatus.IN_PROGRESS || c.getStatus() == ComplaintStatus.WAITING_FOR_CUSTOMER) inProgC++;
            else if (c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED) resC++;

            if (c.isOverdue()) overdueC++;
            if (c.getEscalationLevel().getLevel() > 0) escC++;
        }

        totalCardVal.setText(String.valueOf(total));
        newCardVal.setText(String.valueOf(newC));
        assignedCardVal.setText(String.valueOf(assignedC));
        progressCardVal.setText(String.valueOf(inProgC));
        resolvedCardVal.setText(String.valueOf(resC));
        overdueCardVal.setText(String.valueOf(overdueC));
        escalatedCardVal.setText(String.valueOf(escC));
    }

    private void applyFilters() {
        String q = searchField.getText().trim();
        String selStatus = (String) statusFilterCombo.getSelectedItem();
        String selPrio = (String) priorityFilterCombo.getSelectedItem();
        String selCat = (String) categoryFilterCombo.getSelectedItem();

        CustomArrayList<Complaint> base = complaintRepo.findAll();
        CustomArrayList<Complaint> filtered = new CustomArrayList<>();

        for (int i = 0; i < base.size(); i++) {
            Complaint c = base.get(i);
            boolean statusOk = (selStatus == null || selStatus.startsWith("All") || c.getStatus().name().equalsIgnoreCase(selStatus));
            boolean prioOk = (selPrio == null || selPrio.startsWith("All") || c.getPriority().name().equalsIgnoreCase(selPrio));
            boolean catOk = (selCat == null || selCat.startsWith("All") || c.getCategory().equalsIgnoreCase(selCat));

            if (statusOk && prioOk && catOk) {
                filtered.add(c);
            }
        }

        if (!q.isEmpty()) {
            SearchEngineService.SearchResponse res = searchEngine.search(filtered, q);
            displayedComplaints = res.results;
        } else {
            displayedComplaints = filtered;
        }

        populateTable(displayedComplaints);
    }

    private void populateTable(CustomArrayList<Complaint> list) {
        tableModel.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Complaint c = list.get(i);
            tableModel.addRow(new Object[]{
                    c.getTrackingId(),
                    c.getCustomerName(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "Pending",
                    c.getAssignedStaffName() != null && !c.getAssignedStaffName().isEmpty() ? c.getAssignedStaffName() : "Unassigned",
                    c.getPriority().getDisplayName(),
                    c.getStatus().getDisplayName(),
                    c.getEscalationLevel().getDescription(),
                    DateFormatter.formatDateTime(c.getExpectedResolutionTimestamp()),
                    c.getDsaStrategy()
            });
        }
    }
}
