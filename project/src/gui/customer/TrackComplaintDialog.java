package gui.customer;

import gui.common.AppTheme;
import gui.common.TimelinePanel;
import gui.common.UIUtils;
import model.Complaint;
import repository.ComplaintRepository;
import utils.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TrackComplaintDialog extends JDialog {

    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private JPanel resultContainer;

    public TrackComplaintDialog(Frame parent) {
        super(parent, "Track Complaint by Tracking ID", true);
        setSize(780, 580);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        // Header & Search Box
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(AppTheme.BG_SIDEBAR);
        top.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lbl = new JLabel("Complaint Tracking System");
        lbl.setFont(AppTheme.FONT_TITLE);
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        top.add(lbl, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(AppTheme.BG_SIDEBAR);

        JTextField trackField = AppTheme.createTextField(22);
        trackField.putClientProperty("JTextField.placeholderText", "Enter Tracking ID e.g. CMP-2026-000101");

        JButton trackBtn = AppTheme.createPrimaryButton("Track Status");
        searchRow.add(trackField);
        searchRow.add(trackBtn);
        top.add(searchRow, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // Result Container
        resultContainer = new JPanel(new BorderLayout(10, 10));
        resultContainer.setBackground(AppTheme.BG_DARK);
        resultContainer.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel initialPrompt = new JLabel("Enter a valid Tracking ID above to track the live progress and history.", SwingConstants.CENTER);
        initialPrompt.setFont(AppTheme.FONT_BODY);
        initialPrompt.setForeground(AppTheme.TEXT_MUTED);
        resultContainer.add(initialPrompt, BorderLayout.CENTER);

        add(resultContainer, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_DARK);
        JButton closeBtn = AppTheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // Actions
        trackBtn.addActionListener(e -> {
            String tid = trackField.getText().trim();
            performTrack(tid);
        });

        trackField.addActionListener(e -> {
            String tid = trackField.getText().trim();
            performTrack(tid);
        });
    }

    private void performTrack(String trackingId) {
        resultContainer.removeAll();
        if (trackingId.isEmpty()) {
            JLabel err = new JLabel("Please enter a Tracking ID.", SwingConstants.CENTER);
            err.setForeground(AppTheme.DANGER);
            resultContainer.add(err, BorderLayout.CENTER);
        } else {
            Complaint cmp = complaintRepo.findByTrackingId(trackingId);
            if (cmp == null) {
                JLabel err = new JLabel("No complaint found matching Tracking ID: " + trackingId, SwingConstants.CENTER);
                err.setFont(AppTheme.FONT_BODY_BOLD);
                err.setForeground(AppTheme.DANGER);
                resultContainer.add(err, BorderLayout.CENTER);
            } else {
                JPanel card = new JPanel(new BorderLayout(10, 10));
                card.setBackground(AppTheme.BG_DARK);

                // Timeline
                card.add(new TimelinePanel(cmp), BorderLayout.NORTH);

                // Info Grid
                JPanel info = new JPanel(new GridLayout(4, 2, 10, 8));
                info.setBackground(AppTheme.BG_CARD);
                info.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(AppTheme.BORDER_COLOR, 1),
                        new EmptyBorder(14, 16, 14, 16)
                ));

                info.add(createItem("Tracking ID", cmp.getTrackingId()));
                info.add(createItem("Current Status", cmp.getStatus().getDisplayName()));
                info.add(createItem("Title", cmp.getTitle()));
                info.add(createItem("Category", cmp.getCategory()));
                info.add(createItem("Department", cmp.getDepartmentName()));
                info.add(createItem("Assigned Staff", cmp.getAssignedStaffName() != null && !cmp.getAssignedStaffName().isEmpty() ? cmp.getAssignedStaffName() : "In Queue"));
                info.add(createItem("Date Submitted", DateFormatter.formatDateTime(cmp.getCreatedTimestamp())));
                info.add(createItem("Target Resolution", DateFormatter.formatDateTime(cmp.getExpectedResolutionTimestamp())));

                card.add(info, BorderLayout.CENTER);
                resultContainer.add(card, BorderLayout.CENTER);
            }
        }
        resultContainer.revalidate();
        resultContainer.repaint();
    }

    private JPanel createItem(String k, String v) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(AppTheme.BG_CARD);
        JLabel l = new JLabel(k.toUpperCase());
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_MUTED);

        JLabel val = new JLabel(v);
        val.setFont(AppTheme.FONT_BODY_BOLD);
        val.setForeground(AppTheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }
}
