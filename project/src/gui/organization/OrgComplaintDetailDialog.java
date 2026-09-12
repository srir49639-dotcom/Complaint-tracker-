package gui.organization;

import gui.common.AppTheme;
import gui.common.TimelinePanel;
import gui.common.UIUtils;
import model.*;
import repository.HistoryRepository;
import repository.ResponseRepository;
import services.ComplaintService;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class OrgComplaintDetailDialog extends JDialog {

    private final Complaint complaint;
    private final User currentUser;
    private final Runnable onUpdateCallback;

    private final ComplaintService complaintService = ComplaintService.getInstance();
    private final HistoryRepository historyRepo = HistoryRepository.getInstance();
    private final ResponseRepository responseRepo = ResponseRepository.getInstance();

    private JPanel responsesContainer;

    public OrgComplaintDetailDialog(Frame parent, Complaint complaint, User currentUser, Runnable onUpdateCallback) {
        super(parent, "Organization Complaint Management — " + complaint.getTrackingId(), true);
        this.complaint = complaint;
        this.currentUser = currentUser;
        this.onUpdateCallback = onUpdateCallback;

        setSize(980, 750);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    public OrgComplaintDetailDialog(Dialog parent, Complaint complaint, User currentUser, Runnable onUpdateCallback) {
        super(parent, "Organization Complaint Management — " + complaint.getTrackingId(), true);
        this.complaint = complaint;
        this.currentUser = currentUser;
        this.onUpdateCallback = onUpdateCallback;

        setSize(980, 750);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel leftH = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftH.setBackground(AppTheme.BG_SIDEBAR);

        JLabel trackId = new JLabel(complaint.getTrackingId());
        trackId.setFont(AppTheme.FONT_TITLE);
        trackId.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel statusBadge = new JLabel("  " + complaint.getStatus().getDisplayName() + "  ");
        statusBadge.setFont(AppTheme.FONT_BODY_BOLD);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(UIUtils.getStatusColor(complaint.getStatus()));
        statusBadge.setForeground(Color.WHITE);

        JLabel prioBadge = new JLabel("  " + complaint.getPriority().getDisplayName() + " Priority  ");
        prioBadge.setFont(AppTheme.FONT_BODY_BOLD);
        prioBadge.setOpaque(true);
        prioBadge.setBackground(UIUtils.getPriorityColor(complaint.getPriority()));
        prioBadge.setForeground(Color.WHITE);

        leftH.add(trackId);
        leftH.add(statusBadge);
        leftH.add(prioBadge);
        header.add(leftH, BorderLayout.WEST);

        // SLA status
        SLAInfo sla = new SLAInfo(complaint.getComplaintId(), complaint.getPriority(), complaint.getCreatedTimestamp(), complaint.getExpectedResolutionTimestamp());
        JLabel slaLabel = new JLabel("SLA Status: " + sla.getStatus().getLabel() + " (" + DateFormatter.formatDurationHours(sla.getHoursRemaining()) + ")");
        slaLabel.setFont(AppTheme.FONT_BODY_BOLD);
        slaLabel.setForeground(sla.getStatus() == SLAInfo.SLAStatus.BREACHED ? AppTheme.DANGER : (sla.getStatus() == SLAInfo.SLAStatus.AT_RISK ? AppTheme.WARNING : AppTheme.SUCCESS));
        header.add(slaLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Top of Center: Timeline & Meta
        JPanel topBox = new JPanel(new BorderLayout(8, 8));
        topBox.setBackground(AppTheme.BG_DARK);
        topBox.add(new TimelinePanel(complaint), BorderLayout.NORTH);

        JPanel infoGrid = new JPanel(new GridLayout(3, 3, 10, 6));
        infoGrid.setBackground(AppTheme.BG_CARD);
        infoGrid.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        infoGrid.add(createMeta("Title", complaint.getTitle()));
        infoGrid.add(createMeta("Category", complaint.getCategory()));
        infoGrid.add(createMeta("Location", complaint.getLocation()));
        infoGrid.add(createMeta("Customer", complaint.getCustomerName() + " (" + complaint.getCustomerEmail() + ")"));
        infoGrid.add(createMeta("Department", complaint.getDepartmentName()));
        infoGrid.add(createMeta("Assigned Staff", complaint.getAssignedStaffName() != null && !complaint.getAssignedStaffName().isEmpty() ? complaint.getAssignedStaffName() : "Unassigned"));
        infoGrid.add(createMeta("Submitted Date", DateFormatter.formatDateTime(complaint.getCreatedTimestamp())));
        infoGrid.add(createMeta("Target Deadline", DateFormatter.formatDateTime(complaint.getExpectedResolutionTimestamp())));
        infoGrid.add(createMeta("Escalation", complaint.getEscalationLevel().getDescription()));

        topBox.add(infoGrid, BorderLayout.CENTER);
        center.add(topBox, BorderLayout.NORTH);

        // Tabs: 1. Full Details, 2. Audit History, 3. Communications (Public & Internal Notes)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        tabs.addTab("Complaint Details & Notes", createDescriptionPanel());
        tabs.addTab("Audit Trail History", createHistoryPanel());
        tabs.addTab("Messages & Internal Notes", createResponsesPanel());

        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton assignBtn = AppTheme.createPrimaryButton("👤 Assign / Reassign");
        JButton statusBtn = AppTheme.createSecondaryButton("🔄 Update Status");
        JButton escalateBtn = AppTheme.createSecondaryButton("⚡ Escalate");
        JButton duplicateBtn = AppTheme.createSecondaryButton("🔍 Scan Similar");
        JButton closeBtn = AppTheme.createSecondaryButton("Close");

        footer.add(duplicateBtn);
        footer.add(assignBtn);
        footer.add(statusBtn);
        footer.add(escalateBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());

        assignBtn.addActionListener(e -> {
            new AssignComplaintDialog(this, complaint, currentUser, () -> {
                if (onUpdateCallback != null) onUpdateCallback.run();
                dispose();
            }).setVisible(true);
        });

        duplicateBtn.addActionListener(e -> {
            new DuplicateDetectionDialog(this, complaint).setVisible(true);
        });

        statusBtn.addActionListener(e -> {
            JComboBox<ComplaintStatus> statusCombo = new JComboBox<>(ComplaintStatus.values());
            statusCombo.setSelectedItem(complaint.getStatus());
            JTextField commentF = AppTheme.createTextField(20);

            JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
            p.add(new JLabel("Select New Status:")); p.add(statusCombo);
            p.add(new JLabel("Status Update Note:")); p.add(commentF);

            int opt = JOptionPane.showConfirmDialog(this, p, "Update Status", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                ComplaintStatus newSt = (ComplaintStatus) statusCombo.getSelectedItem();
                complaintService.updateStatus(complaint.getComplaintId(), newSt, currentUser.getUserId(), currentUser.getFullName(), commentF.getText().trim());
                JOptionPane.showMessageDialog(this, "Status updated to " + newSt.getDisplayName() + "!", "Updated", JOptionPane.INFORMATION_MESSAGE);
                if (onUpdateCallback != null) onUpdateCallback.run();
                dispose();
            }
        });

        escalateBtn.addActionListener(e -> {
            JComboBox<EscalationLevel> escCombo = new JComboBox<>(EscalationLevel.values());
            escCombo.setSelectedItem(complaint.getEscalationLevel());
            JTextField reasonF = AppTheme.createTextField(20);

            JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
            p.add(new JLabel("Escalation Tier:")); p.add(escCombo);
            p.add(new JLabel("Escalation Reason:")); p.add(reasonF);

            int opt = JOptionPane.showConfirmDialog(this, p, "Escalate Complaint", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                EscalationLevel newLvl = (EscalationLevel) escCombo.getSelectedItem();
                complaintService.escalateComplaint(complaint.getComplaintId(), newLvl, currentUser.getUserId(), currentUser.getFullName(), reasonF.getText().trim());
                JOptionPane.showMessageDialog(this, "Complaint escalated to " + newLvl.getDescription() + "!", "Escalated", JOptionPane.INFORMATION_MESSAGE);
                if (onUpdateCallback != null) onUpdateCallback.run();
                dispose();
            }
        });
    }

    private JPanel createDescriptionPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea area = AppTheme.createTextArea(6, 40);
        area.setText("DESCRIPTION:\n" + complaint.getDescription() +
                "\n\nSUPPORTING INFORMATION:\n" + (complaint.getSupportingInfo().isEmpty() ? "None provided" : complaint.getSupportingInfo()) +
                "\n\nTAGS:\n" + (complaint.getTags().isEmpty() ? "None" : complaint.getTags()) +
                "\n\nREOPEN COUNT: " + complaint.getReopenCount() + " times" +
                "\nESTIMATED EFFORT: " + complaint.getEstimatedEffortHours() + " hours");
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        CustomArrayList<ComplaintHistory> historyList = historyRepo.findByComplaintId(complaint.getComplaintId());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(AppTheme.BG_DARK);

        for (int i = 0; i < historyList.size(); i++) {
            ComplaintHistory h = historyList.get(i);
            JPanel item = new JPanel(new BorderLayout(4, 4));
            item.setBackground(AppTheme.BG_CARD);
            item.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(AppTheme.BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
            ));

            JLabel action = new JLabel("Action: " + h.getAction().replace("_", " ") + " by " + h.getPerformedByUserName());
            action.setFont(AppTheme.FONT_BODY_BOLD);
            action.setForeground(AppTheme.TEXT_PRIMARY);

            JLabel time = new JLabel(DateFormatter.formatDateTime(h.getTimestamp()));
            time.setFont(AppTheme.FONT_SMALL);
            time.setForeground(AppTheme.TEXT_MUTED);

            JLabel comment = new JLabel(h.getComment().isEmpty() ? "No comment." : h.getComment());
            comment.setFont(AppTheme.FONT_BODY);
            comment.setForeground(AppTheme.TEXT_SECONDARY);

            item.add(action, BorderLayout.NORTH);
            item.add(comment, BorderLayout.CENTER);
            item.add(time, BorderLayout.SOUTH);

            listPanel.add(item);
            listPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createResponsesPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        responsesContainer = new JPanel();
        responsesContainer.setLayout(new BoxLayout(responsesContainer, BoxLayout.Y_AXIS));
        responsesContainer.setBackground(AppTheme.BG_DARK);

        reloadResponses();

        JScrollPane scroll = new JScrollPane(responsesContainer);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);

        // Reply input box
        JPanel replyBox = new JPanel(new BorderLayout(6, 6));
        replyBox.setBackground(AppTheme.BG_DARK);
        replyBox.setBorder(new EmptyBorder(8, 0, 0, 0));

        JTextField replyInput = AppTheme.createTextField(25);
        replyInput.putClientProperty("JTextField.placeholderText", "Type response or internal note...");

        JCheckBox internalCheck = new JCheckBox("Internal Staff Note Only (Hidden from Customer)");
        internalCheck.setBackground(AppTheme.BG_DARK);
        internalCheck.setForeground(AppTheme.WARNING);
        internalCheck.setFont(AppTheme.FONT_SMALL);

        JButton sendBtn = AppTheme.createPrimaryButton("Post Message");

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBackground(AppTheme.BG_DARK);
        bottomRow.add(internalCheck, BorderLayout.WEST);
        bottomRow.add(sendBtn, BorderLayout.EAST);

        replyBox.add(replyInput, BorderLayout.CENTER);
        replyBox.add(bottomRow, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> {
            String msg = replyInput.getText().trim();
            if (!msg.isEmpty()) {
                boolean isInt = internalCheck.isSelected();
                complaintService.addResponse(complaint.getComplaintId(), currentUser.getUserId(), currentUser.getFullName(), currentUser.getRole(), msg, isInt);
                replyInput.setText("");
                reloadResponses();
            }
        });

        p.add(replyBox, BorderLayout.SOUTH);
        return p;
    }

    private void reloadResponses() {
        responsesContainer.removeAll();
        CustomArrayList<ComplaintResponse> list = responseRepo.findByComplaintId(complaint.getComplaintId(), true);

        if (list.isEmpty()) {
            JLabel empty = new JLabel("No messages recorded yet.", SwingConstants.CENTER);
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.TEXT_MUTED);
            responsesContainer.add(empty);
        } else {
            for (int i = 0; i < list.size(); i++) {
                ComplaintResponse r = list.get(i);
                boolean isInternal = r.isInternalNote();
                boolean isCustomer = (r.getSenderRole() == UserRole.CUSTOMER);

                JPanel msgCard = new JPanel(new BorderLayout(4, 4));
                msgCard.setBackground(isInternal ? new Color(69, 26, 3) : (isCustomer ? new Color(30, 58, 138) : AppTheme.BG_CARD));
                msgCard.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(isInternal ? AppTheme.WARNING : (isCustomer ? AppTheme.ACCENT : AppTheme.BORDER_COLOR), 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));

                JLabel sender = new JLabel((isInternal ? "[INTERNAL NOTE] " : "") + r.getSenderName() + " (" + r.getSenderRole().getTitle() + ")");
                sender.setFont(AppTheme.FONT_BODY_BOLD);
                sender.setForeground(isInternal ? AppTheme.WARNING : (isCustomer ? Color.WHITE : AppTheme.ACCENT));

                JLabel txt = new JLabel(r.getMessage());
                txt.setFont(AppTheme.FONT_BODY);
                txt.setForeground(AppTheme.TEXT_PRIMARY);

                JLabel time = new JLabel(DateFormatter.formatDateTime(r.getTimestamp()));
                time.setFont(AppTheme.FONT_SMALL);
                time.setForeground(AppTheme.TEXT_MUTED);

                msgCard.add(sender, BorderLayout.NORTH);
                msgCard.add(txt, BorderLayout.CENTER);
                msgCard.add(time, BorderLayout.SOUTH);

                responsesContainer.add(msgCard);
                responsesContainer.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }
        responsesContainer.revalidate();
        responsesContainer.repaint();
    }

    private JPanel createMeta(String k, String v) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(AppTheme.BG_CARD);
        JLabel l = new JLabel(k.toUpperCase());
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_MUTED);

        JLabel val = new JLabel(v != null && !v.isEmpty() ? v : "N/A");
        val.setFont(AppTheme.FONT_BODY_BOLD);
        val.setForeground(AppTheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }
}
