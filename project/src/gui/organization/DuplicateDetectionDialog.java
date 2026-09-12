package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Complaint;
import repository.ComplaintRepository;
import services.DuplicateDetectionService;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DuplicateDetectionDialog extends JDialog {

    private final Complaint targetComplaint;
    private final DuplicateDetectionService duplicateService = DuplicateDetectionService.getInstance();
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();

    public DuplicateDetectionDialog(Dialog parent, Complaint targetComplaint) {
        super(parent, "Similar & Duplicate Complaint Scanner", true);
        this.targetComplaint = targetComplaint;

        setSize(780, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Potential Duplicate & Similar Complaints");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Target: [" + targetComplaint.getTrackingId() + "] " + targetComplaint.getTitle());
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Table
        CustomArrayList<Complaint> all = complaintRepo.findAll();
        CustomArrayList<DuplicateDetectionService.SimilarityMatch> matches = duplicateService.findSimilarComplaints(targetComplaint, all, 0.40);

        String[] cols = new String[]{"Tracking ID", "Title", "Category", "Location", "Status", "Similarity Rating"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (int i = 0; i < matches.size(); i++) {
            DuplicateDetectionService.SimilarityMatch m = matches.get(i);
            model.addRow(new Object[]{
                    m.existingComplaint.getTrackingId(),
                    m.existingComplaint.getTitle(),
                    m.existingComplaint.getCategory(),
                    m.existingComplaint.getLocation(),
                    m.existingComplaint.getStatus().getDisplayName(),
                    m.similarityTier + " (" + String.format("%.0f%%", m.similarityScore * 100) + ")"
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 16, 12, 16));

        if (matches.isEmpty()) {
            JLabel empty = new JLabel("No potential duplicates detected for this complaint. The issue appears unique.", SwingConstants.CENTER);
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.SUCCESS);
            center.add(empty, BorderLayout.CENTER);
        } else {
            JScrollPane scroll = new JScrollPane(table);
            scroll.getViewport().setBackground(AppTheme.BG_CARD);
            scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
            center.add(scroll, BorderLayout.CENTER);
        }

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close Scanner");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }
}
