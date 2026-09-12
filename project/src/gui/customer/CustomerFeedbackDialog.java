package gui.customer;

import gui.common.AppTheme;
import model.Complaint;
import model.Customer;
import services.ComplaintService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomerFeedbackDialog extends JDialog {

    private final Complaint complaint;
    private final Customer customer;
    private final ComplaintService complaintService = ComplaintService.getInstance();

    public CustomerFeedbackDialog(Dialog parent, Complaint complaint, Customer customer) {
        super(parent, "Provide Resolution Feedback", true);
        this.complaint = complaint;
        this.customer = customer;

        setSize(520, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Complaint Resolution Feedback");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Tracking ID: " + complaint.getTrackingId());
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_DARK);
        form.setBorder(new EmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel rLbl = new JLabel("Overall Rating (1 to 5 Stars):");
        rLbl.setFont(AppTheme.FONT_BODY_BOLD);
        rLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(rLbl, gbc); gbc.gridy++;

        String[] ratings = new String[]{"⭐⭐⭐⭐⭐ (5 - Excellent)", "⭐⭐⭐⭐ (4 - Good)", "⭐⭐⭐ (3 - Average)", "⭐⭐ (2 - Poor)", "⭐ (1 - Terrible)"};
        JComboBox<String> ratingCombo = new JComboBox<>(ratings);
        ratingCombo.setBackground(AppTheme.BG_INPUT);
        ratingCombo.setForeground(AppTheme.TEXT_PRIMARY);
        ratingCombo.setFont(AppTheme.FONT_BODY);
        form.add(ratingCombo, gbc); gbc.gridy++;

        JCheckBox satisfiedCheck = new JCheckBox("I am satisfied with how this complaint was handled and resolved");
        satisfiedCheck.setSelected(true);
        satisfiedCheck.setBackground(AppTheme.BG_DARK);
        satisfiedCheck.setForeground(AppTheme.TEXT_PRIMARY);
        satisfiedCheck.setFont(AppTheme.FONT_BODY);
        form.add(satisfiedCheck, gbc); gbc.gridy++;

        JLabel cLbl = new JLabel("Comments / Feedback:");
        cLbl.setFont(AppTheme.FONT_BODY_BOLD);
        cLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(cLbl, gbc); gbc.gridy++;

        JTextArea commentsArea = AppTheme.createTextArea(4, 25);
        JScrollPane scroll = new JScrollPane(commentsArea);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        form.add(scroll, gbc); gbc.gridy++;

        add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton cancelBtn = AppTheme.createSecondaryButton("Cancel");
        JButton submitBtn = AppTheme.createSuccessButton("Submit Feedback");
        footer.add(cancelBtn);
        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());

        submitBtn.addActionListener(e -> {
            int rating = 5 - ratingCombo.getSelectedIndex();
            boolean sat = satisfiedCheck.isSelected();
            String comments = commentsArea.getText().trim();

            complaintService.submitFeedback(complaint.getComplaintId(), customer.getCustomerId(), rating, comments, sat);
            JOptionPane.showMessageDialog(this, "Thank you for your valuable feedback!", "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
    }
}
