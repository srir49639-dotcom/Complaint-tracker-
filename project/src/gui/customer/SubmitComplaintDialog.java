package gui.customer;

import gui.common.AppTheme;
import model.Complaint;
import model.Customer;
import model.Department;
import model.PriorityLevel;
import repository.DepartmentRepository;
import services.ComplaintService;
import utils.ValidationUtils;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SubmitComplaintDialog extends JDialog {

    private final Customer customer;
    private final Runnable onSuccessCallback;
    private final ComplaintService complaintService = ComplaintService.getInstance();
    private final DepartmentRepository deptRepo = DepartmentRepository.getInstance();

    public SubmitComplaintDialog(Frame parent, Customer customer, Runnable onSuccessCallback) {
        super(parent, "Submit New Complaint", true);
        this.customer = customer;
        this.onSuccessCallback = onSuccessCallback;

        setSize(650, 680);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Submit a Service Complaint");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Please provide detailed information so our team can resolve the issue swiftly.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Form in ScrollPane
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_DARK);
        form.setBorder(new EmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField titleField = AppTheme.createTextField(30);
        JTextArea descArea = AppTheme.createTextArea(4, 30);
        JTextField locationField = AppTheme.createTextField(30);

        String[] categories = new String[]{
                "Internet / Wi-Fi", "Water & Plumbing", "Electricity / Power",
                "Hostel Facilities", "Academic / Classroom", "Finance & Fees",
                "Security & Access", "Transport / Bus", "Library Services",
                "Food Services / Canteen", "Infrastructure & Maintenance", "Administration"
        };
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setBackground(AppTheme.BG_INPUT);
        categoryCombo.setForeground(AppTheme.TEXT_PRIMARY);
        categoryCombo.setFont(AppTheme.FONT_BODY);

        JComboBox<PriorityLevel> priorityCombo = new JComboBox<>(PriorityLevel.values());
        priorityCombo.setSelectedItem(PriorityLevel.MEDIUM);
        priorityCombo.setBackground(AppTheme.BG_INPUT);
        priorityCombo.setForeground(AppTheme.TEXT_PRIMARY);
        priorityCombo.setFont(AppTheme.FONT_BODY);

        // Preferred department combo
        CustomArrayList<Department> allDepts = deptRepo.findAll();
        String[] deptNames = new String[allDepts.size() + 1];
        deptNames[0] = "Auto-Detect Best Department";
        for (int i = 0; i < allDepts.size(); i++) {
            deptNames[i + 1] = allDepts.get(i).getName();
        }
        JComboBox<String> deptCombo = new JComboBox<>(deptNames);
        deptCombo.setBackground(AppTheme.BG_INPUT);
        deptCombo.setForeground(AppTheme.TEXT_PRIMARY);
        deptCombo.setFont(AppTheme.FONT_BODY);

        JTextField tagsField = AppTheme.createTextField(30);
        tagsField.putClientProperty("JTextField.placeholderText", "e.g. urgent, leakage, hostel-a, wifi");

        JTextField supportingField = AppTheme.createTextField(30);
        supportingField.putClientProperty("JTextField.placeholderText", "Optional reference, room number, or attachment note");

        // Add fields to form
        form.add(createLabel("Complaint Title *"), gbc); gbc.gridy++;
        form.add(titleField, gbc); gbc.gridy++;

        form.add(createLabel("Category *"), gbc); gbc.gridy++;
        form.add(categoryCombo, gbc); gbc.gridy++;

        form.add(createLabel("Detailed Description *"), gbc); gbc.gridy++;
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        form.add(descScroll, gbc); gbc.gridy++;

        form.add(createLabel("Location / Building / Room *"), gbc); gbc.gridy++;
        form.add(locationField, gbc); gbc.gridy++;

        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setBackground(AppTheme.BG_DARK);

        JPanel pCol = new JPanel(new BorderLayout(2, 2));
        pCol.setBackground(AppTheme.BG_DARK);
        pCol.add(createLabel("Severity / Priority"), BorderLayout.NORTH);
        pCol.add(priorityCombo, BorderLayout.CENTER);

        JPanel dCol = new JPanel(new BorderLayout(2, 2));
        dCol.setBackground(AppTheme.BG_DARK);
        dCol.add(createLabel("Preferred Department"), BorderLayout.NORTH);
        dCol.add(deptCombo, BorderLayout.CENTER);

        row2.add(pCol);
        row2.add(dCol);
        form.add(row2, gbc); gbc.gridy++;

        form.add(createLabel("Tags (comma-separated keywords)"), gbc); gbc.gridy++;
        form.add(tagsField, gbc); gbc.gridy++;

        form.add(createLabel("Supporting Notes / Text Reference"), gbc); gbc.gridy++;
        form.add(supportingField, gbc); gbc.gridy++;

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.getViewport().setBackground(AppTheme.BG_DARK);
        formScroll.setBorder(null);
        add(formScroll, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton cancelBtn = AppTheme.createSecondaryButton("Cancel");
        JButton submitBtn = AppTheme.createSuccessButton("Submit Complaint");

        footer.add(cancelBtn);
        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());

        submitBtn.addActionListener(e -> {
            String titleText = titleField.getText().trim();
            String descText = descArea.getText().trim();
            String locText = locationField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            PriorityLevel prio = (PriorityLevel) priorityCombo.getSelectedItem();
            String prefDept = (String) deptCombo.getSelectedItem();
            if (prefDept != null && prefDept.startsWith("Auto")) prefDept = "";
            String tags = tagsField.getText().trim();
            String sup = supportingField.getText().trim();

            if (!ValidationUtils.isNotEmpty(titleText) || !ValidationUtils.isNotEmpty(descText) || !ValidationUtils.isNotEmpty(locText)) {
                JOptionPane.showMessageDialog(this, "Title, description, and location are mandatory.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Complaint created = complaintService.submitComplaint(
                    customer.getCustomerId(), customer.getFullName(), customer.getEmail(), customer.getPhone(),
                    titleText, descText, cat, locText, prio, prefDept, tags, sup
            );

            JOptionPane.showMessageDialog(this,
                    "Complaint Submitted Successfully!\n\nTracking ID: " + created.getTrackingId() +
                            "\nCategory: " + created.getCategory() +
                            "\nExpected SLA Target: " + created.getPriority().getSlaHours() + " hours" +
                            "\n\nYou can use your Tracking ID to track status anytime.",
                    "Complaint Registered", JOptionPane.INFORMATION_MESSAGE);

            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();
        });
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        return l;
    }
}
