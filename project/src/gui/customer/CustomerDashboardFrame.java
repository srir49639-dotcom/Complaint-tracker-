package gui.customer;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Complaint;
import model.ComplaintStatus;
import model.Customer;
import repository.ComplaintRepository;
import repository.NotificationRepository;
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

public class CustomerDashboardFrame extends JFrame {

    private final Customer currentCustomer;
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private final NotificationRepository notifRepo = NotificationRepository.getInstance();
    private final SearchEngineService searchEngine = SearchEngineService.getInstance();

    private DefaultTableModel tableModel;
    private JTable complaintsTable;
    private CustomArrayList<Complaint> displayedComplaints = new CustomArrayList<>();
    private JLabel typoSuggestionLabel;
    private JLabel totalCardVal, openCardVal, progressCardVal, resolvedCardVal, closedCardVal;

    public CustomerDashboardFrame(Customer customer) {
        this.currentCustomer = customer;
        setTitle("Customer Portal — Smart Complaint Tracker");
        setSize(1100, 720);
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

        JLabel welcome = new JLabel("Welcome, " + currentCustomer.getFullName());
        welcome.setFont(AppTheme.FONT_TITLE);
        welcome.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel custId = new JLabel("[" + currentCustomer.getCustomerId() + "]");
        custId.setFont(AppTheme.FONT_BODY);
        custId.setForeground(AppTheme.TEXT_MUTED);

        userPanel.add(welcome);
        userPanel.add(custId);
        topNav.add(userPanel, BorderLayout.WEST);

        JPanel navActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navActions.setBackground(AppTheme.BG_SIDEBAR);

        JButton notifBtn = AppTheme.createSecondaryButton("🔔 Notifications (" + notifRepo.getUnreadCount(currentCustomer.getCustomerId()) + ")");
        JButton profileBtn = AppTheme.createSecondaryButton("👤 Profile");
        JButton logoutBtn = AppTheme.createPrimaryButton("Logout");

        navActions.add(notifBtn);
        navActions.add(profileBtn);
        navActions.add(logoutBtn);
        topNav.add(navActions, BorderLayout.EAST);

        add(topNav, BorderLayout.NORTH);

        // 2. Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout(12, 12));
        mainContent.setBackground(AppTheme.BG_DARK);
        mainContent.setBorder(new EmptyBorder(12, 24, 16, 24));

        // Metric Cards Panel
        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        metricsPanel.setBackground(AppTheme.BG_DARK);

        JPanel card1 = UIUtils.createCard("My Complaints", "0", AppTheme.PRIMARY);
        JPanel card2 = UIUtils.createCard("Open / New", "0", AppTheme.ACCENT);
        JPanel card3 = UIUtils.createCard("In Progress", "0", AppTheme.WARNING);
        JPanel card4 = UIUtils.createCard("Resolved", "0", AppTheme.SUCCESS);
        JPanel card5 = UIUtils.createCard("Closed", "0", AppTheme.TEXT_MUTED);

        totalCardVal = (JLabel) card1.getComponent(1);
        openCardVal = (JLabel) card2.getComponent(1);
        progressCardVal = (JLabel) card3.getComponent(1);
        resolvedCardVal = (JLabel) card4.getComponent(1);
        closedCardVal = (JLabel) card5.getComponent(1);

        metricsPanel.add(card1);
        metricsPanel.add(card2);
        metricsPanel.add(card3);
        metricsPanel.add(card4);
        metricsPanel.add(card5);

        mainContent.add(metricsPanel, BorderLayout.NORTH);

        // Center Panel: Actions + Search Bar + Table
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setBackground(AppTheme.BG_DARK);

        // Action Toolbar + Search Bar
        JPanel toolBar = new JPanel(new BorderLayout(10, 10));
        toolBar.setBackground(AppTheme.BG_DARK);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBtns.setBackground(AppTheme.BG_DARK);

        JButton submitBtn = AppTheme.createSuccessButton("+ Submit New Complaint");
        JButton trackBtn = AppTheme.createPrimaryButton("🔍 Track by Tracking ID");
        JButton refreshBtn = AppTheme.createSecondaryButton("🔄 Refresh");

        actionBtns.add(submitBtn);
        actionBtns.add(trackBtn);
        actionBtns.add(refreshBtn);
        toolBar.add(actionBtns, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setBackground(AppTheme.BG_DARK);

        JTextField searchField = AppTheme.createTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search my complaints...");
        JButton searchBtn = AppTheme.createSecondaryButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        toolBar.add(searchPanel, BorderLayout.EAST);

        // Typo Suggestion Label
        typoSuggestionLabel = new JLabel("", SwingConstants.LEFT);
        typoSuggestionLabel.setFont(AppTheme.FONT_BODY);
        typoSuggestionLabel.setForeground(AppTheme.ACCENT);
        typoSuggestionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel topBarStack = new JPanel(new BorderLayout(4, 4));
        topBarStack.setBackground(AppTheme.BG_DARK);
        topBarStack.add(toolBar, BorderLayout.NORTH);
        topBarStack.add(typoSuggestionLabel, BorderLayout.SOUTH);

        tableContainer.add(topBarStack, BorderLayout.NORTH);

        // Table
        String[] columns = new String[]{"Tracking ID", "Title", "Category", "Department", "Status", "Priority", "Last Updated", "DSA Strategy"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintsTable = new JTable(tableModel);
        UIUtils.styleTable(complaintsTable);
        complaintsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        complaintsTable.getColumnModel().getColumn(7).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        scrollPane.getViewport().setBackground(AppTheme.BG_CARD);
        scrollPane.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(tableContainer, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Event Handlers
        submitBtn.addActionListener(e -> {
            new SubmitComplaintDialog(this, currentCustomer, () -> refreshData()).setVisible(true);
        });

        trackBtn.addActionListener(e -> {
            new TrackComplaintDialog(this).setVisible(true);
        });

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            typoSuggestionLabel.setText("");
            refreshData();
        });

        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            performSearch(q);
        });

        searchField.addActionListener(e -> {
            String q = searchField.getText().trim();
            performSearch(q);
        });

        typoSuggestionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String txt = typoSuggestionLabel.getText();
                if (txt.contains("Did you mean: ")) {
                    String corrected = txt.replace("Did you mean: ", "").replace("? (Click to search)", "").trim();
                    searchField.setText(corrected);
                    performSearch(corrected);
                }
            }
        });

        complaintsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = complaintsTable.getSelectedRow();
                    if (row >= 0 && row < displayedComplaints.size()) {
                        Complaint selected = displayedComplaints.get(row);
                        new CustomerComplaintDetailDialog(CustomerDashboardFrame.this, selected, currentCustomer, () -> refreshData()).setVisible(true);
                    }
                }
            }
        });

        notifBtn.addActionListener(e -> {
            new CustomerNotificationsDialog(this, currentCustomer, () -> {
                notifBtn.setText("🔔 Notifications (" + notifRepo.getUnreadCount(currentCustomer.getCustomerId()) + ")");
            }).setVisible(true);
        });

        profileBtn.addActionListener(e -> {
            new CustomerProfileDialog(this, currentCustomer).setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                new CustomerLoginRegisterFrame().setVisible(true);
                dispose();
            }
        });
    }

    public void refreshData() {
        complaintRepo.reloadFromFile();
        CustomArrayList<Complaint> myComplaints = complaintRepo.findByCustomerId(currentCustomer.getCustomerId());

        // Sort latest updated first
        myComplaints.sort((c1, c2) -> Long.compare(c2.getUpdatedTimestamp(), c1.getUpdatedTimestamp()));

        displayedComplaints = myComplaints;
        populateTable(displayedComplaints);

        // Update cards
        int total = myComplaints.size();
        int open = 0, inProg = 0, res = 0, closed = 0;

        for (int i = 0; i < myComplaints.size(); i++) {
            Complaint c = myComplaints.get(i);
            if (c.getStatus() == ComplaintStatus.NEW || c.getStatus() == ComplaintStatus.UNDER_REVIEW) open++;
            else if (c.getStatus() == ComplaintStatus.ASSIGNED || c.getStatus() == ComplaintStatus.IN_PROGRESS || c.getStatus() == ComplaintStatus.WAITING_FOR_CUSTOMER || c.getStatus() == ComplaintStatus.ESCALATED || c.getStatus() == ComplaintStatus.REOPENED) inProg++;
            else if (c.getStatus() == ComplaintStatus.RESOLVED) res++;
            else if (c.getStatus() == ComplaintStatus.CLOSED || c.getStatus() == ComplaintStatus.CANCELLED) closed++;
        }

        totalCardVal.setText(String.valueOf(total));
        openCardVal.setText(String.valueOf(open));
        progressCardVal.setText(String.valueOf(inProg));
        resolvedCardVal.setText(String.valueOf(res));
        closedCardVal.setText(String.valueOf(closed));
    }

    private void performSearch(String query) {
        CustomArrayList<Complaint> myComplaints = complaintRepo.findByCustomerId(currentCustomer.getCustomerId());
        SearchEngineService.SearchResponse resp = searchEngine.search(myComplaints, query);

        displayedComplaints = resp.results;
        populateTable(displayedComplaints);

        if (resp.typoSuggestion != null) {
            typoSuggestionLabel.setText("Did you mean: " + resp.typoSuggestion + "? (Click to search)");
        } else {
            typoSuggestionLabel.setText("");
        }
    }

    private void populateTable(CustomArrayList<Complaint> list) {
        tableModel.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Complaint c = list.get(i);
            tableModel.addRow(new Object[]{
                    c.getTrackingId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "Pending",
                    c.getStatus().getDisplayName(),
                    c.getPriority().getDisplayName(),
                    DateFormatter.formatDateTime(c.getUpdatedTimestamp()),
                    c.getDsaStrategy()
            });
        }
    }
}
