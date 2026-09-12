package gui.customer;

import gui.common.AppTheme;
import gui.common.TimelinePanel;
import gui.common.UIUtils;
import model.*;
import repository.ComplaintRepository;
import repository.FeedbackRepository;
import repository.HistoryRepository;
import repository.ResponseRepository;
import services.ComplaintService;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CustomerComplaintDetailDialog extends JDialog {

    private final Complaint complaint;
    private final Customer customer;
    private final Runnable onUpdateCallback;

    private final ComplaintService complaintService = ComplaintService.getInstance();
    private final HistoryRepository historyRepo = HistoryRepository.getInstance();
    private final ResponseRepository responseRepo = ResponseRepository.getInstance();
    private final FeedbackRepository feedbackRepo = FeedbackRepository.getInstance();

    private JPanel responsesContainer;

    public CustomerComplaintDetailDialog(Frame parent, Complaint complaint, Customer customer, Runnable onUpdateCallback) {
        super(parent, "Complaint Details — " + complaint.getTrackingId(), true);
        this.complaint = complaint;
        this.customer = customer;
        this.onUpdateCallback = onUpdateCallback;

        setSize(850, 720);
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

        leftH.add(trackId);
        leftH.add(statusBadge);
        header.add(leftH, BorderLayout.WEST);

        JLabel dateLbl = new JLabel("Submitted: " + DateFormatter.formatDateTime(complaint.getCreatedTimestamp()));
        dateLbl.setFont(AppTheme.FONT_BODY);
        dateLbl.setForeground(AppTheme.TEXT_SECONDARY);
        header.add(dateLbl, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center Content: Timeline + Meta + Tabs (History, Messages)
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Top of Center: Timeline Panel
        JPanel topCenter = new JPanel(new BorderLayout(8, 8));
        topCenter.setBackground(AppTheme.BG_DARK);
        topCenter.add(new TimelinePanel(complaint), BorderLayout.NORTH);

        // Metadata grid
        JPanel metaGrid = new JPanel(new GridLayout(3, 2, 12, 6));
        metaGrid.setBackground(AppTheme.BG_CARD);
        metaGrid.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        metaGrid.add(createMetaItem("Title", complaint.getTitle()));
        metaGrid.add(createMetaItem("Category", complaint.getCategory()));
        metaGrid.add(createMetaItem("Location", complaint.getLocation()));
        metaGrid.add(createMetaItem("Assigned Department", complaint.getDepartmentName() != null ? complaint.getDepartmentName() : "Under Review"));
        metaGrid.add(createMetaItem("Assigned Staff", complaint.getAssignedStaffName() != null && !complaint.getAssignedStaffName().isEmpty() ? complaint.getAssignedStaffName() : "In Queue"));
        metaGrid.add(createMetaItem("Target SLA Deadline", DateFormatter.formatDateTime(complaint.getExpectedResolutionTimestamp())));

        topCenter.add(metaGrid, BorderLayout.CENTER);
        center.add(topCenter, BorderLayout.NORTH);

        // Tabs: 1. Description & Info, 2. Timeline History, 3. Organization Responses & Replies
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        tabs.addTab("Description & Details", createDescriptionPanel());
        tabs.addTab("Activity History Timeline", createHistoryPanel());
        tabs.addTab("Responses & Messages", createResponsesPanel());

        center.add(tabs, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton closeBtn = AppTheme.createSecondaryButton("Close");
        footer.add(closeBtn);
        closeBtn.addActionListener(e -> dispose());

        if (complaint.getStatus() == ComplaintStatus.RESOLVED || complaint.getStatus() == ComplaintStatus.CLOSED) {
            JButton feedbackBtn = AppTheme.createPrimaryButton("⭐ Give Feedback");
            JButton reopenBtn = AppTheme.createSecondaryButton("⚠️ Reopen Complaint");

            footer.add(feedbackBtn);
            footer.add(reopenBtn);

            feedbackBtn.addActionListener(e -> {
                new CustomerFeedbackDialog(this, complaint, customer).setVisible(true);
            });

            reopenBtn.addActionListener(e -> {
                String reason = JOptionPane.showInputDialog(this, "Please specify reason for reopening this complaint:", "Reopen Complaint", JOptionPane.QUESTION_MESSAGE);
                if (reason != null && !reason.trim().isEmpty()) {
                    complaintService.reopenComplaint(complaint.getComplaintId(), customer.getCustomerId(), customer.getFullName(), reason.trim());
                    JOptionPane.showMessageDialog(this, "Complaint has been reopened for further investigation.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                    if (onUpdateCallback != null) onUpdateCallback.run();
                    dispose();
                }
            });
        }

        if (complaint.getStatus() == ComplaintStatus.NEW) {
            JButton cancelBtn = AppTheme.createSecondaryButton("Cancel Complaint");
            footer.add(cancelBtn);
            cancelBtn.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this complaint?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    complaintService.updateStatus(complaint.getComplaintId(), ComplaintStatus.CANCELLED, customer.getCustomerId(), customer.getFullName(), "Cancelled by customer.");
                    if (onUpdateCallback != null) onUpdateCallback.run();
                    dispose();
                }
            });
        }

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createDescriptionPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(AppTheme.BG_DARK);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea descArea = AppTheme.createTextArea(6, 40);
        descArea.setText(complaint.getDescription() + "\n\nSupporting Notes: " +
                (complaint.getSupportingInfo().isEmpty() ? "None" : complaint.getSupportingInfo()) +
                "\nTags: " + (complaint.getTags().isEmpty() ? "None" : complaint.getTags()));
        descArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(descArea);
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

            JLabel comment = new JLabel(h.getComment().isEmpty() ? "No additional comment." : h.getComment());
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

        JTextField replyInput = AppTheme.createTextField(30);
        replyInput.putClientProperty("JTextField.placeholderText", "Type a message or response to the organization...");

        JButton sendBtn = AppTheme.createPrimaryButton("Send Reply");
        replyBox.add(replyInput, BorderLayout.CENTER);
        replyBox.add(sendBtn, BorderLayout.EAST);

        sendBtn.addActionListener(e -> {
            String msg = replyInput.getText().trim();
            if (!msg.isEmpty()) {
                complaintService.addResponse(complaint.getComplaintId(), customer.getCustomerId(), customer.getFullName(), UserRole.CUSTOMER, msg, false);
                replyInput.setText("");
                reloadResponses();
            }
        });

        p.add(replyBox, BorderLayout.SOUTH);
        return p;
    }

    private void reloadResponses() {
        responsesContainer.removeAll();
        CustomArrayList<ComplaintResponse> list = responseRepo.findByComplaintId(complaint.getComplaintId(), false);

        if (list.isEmpty()) {
            JLabel empty = new JLabel("No messages yet. Send a message below if you have any questions.");
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.TEXT_MUTED);
            responsesContainer.add(empty);
        } else {
            for (int i = 0; i < list.size(); i++) {
                ComplaintResponse r = list.get(i);
                boolean isCustomer = (r.getSenderRole() == UserRole.CUSTOMER);

                JPanel msgCard = new JPanel(new BorderLayout(4, 4));
                msgCard.setBackground(isCustomer ? new Color(30, 58, 138) : AppTheme.BG_CARD);
                msgCard.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(isCustomer ? AppTheme.ACCENT : AppTheme.BORDER_COLOR, 1),
                        new EmptyBorder(8, 12, 8, 12)
                ));

                JLabel sender = new JLabel(r.getSenderName() + " (" + r.getSenderRole().getTitle() + ")");
                sender.setFont(AppTheme.FONT_BODY_BOLD);
                sender.setForeground(isCustomer ? Color.WHITE : AppTheme.ACCENT);

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

    private JPanel createMetaItem(String label, String val) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(AppTheme.BG_CARD);
        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_MUTED);

        JLabel v = new JLabel(val);
        v.setFont(AppTheme.FONT_BODY_BOLD);
        v.setForeground(AppTheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}
