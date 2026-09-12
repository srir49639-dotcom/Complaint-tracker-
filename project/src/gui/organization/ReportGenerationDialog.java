package gui.organization;

import gui.common.AppTheme;
import services.ReportExportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ReportGenerationDialog extends JDialog {

    private final ReportExportService reportService = ReportExportService.getInstance();

    public ReportGenerationDialog(Frame parent) {
        super(parent, "Generate & Export Reports", true);
        setSize(540, 420);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Export Organizational Reports");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Export structured reports to disk in TXT or CSV formats.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Option 1: Executive Audit Summary (TXT)
        JPanel opt1 = new JPanel(new BorderLayout(8, 8));
        opt1.setBackground(AppTheme.BG_CARD);
        opt1.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel t1 = new JLabel("Executive Audit Summary Report (.txt)");
        t1.setFont(AppTheme.FONT_BODY_BOLD);
        t1.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel d1 = new JLabel("Includes department workload, staff capacity, and customer satisfaction metrics.");
        d1.setFont(AppTheme.FONT_SMALL);
        d1.setForeground(AppTheme.TEXT_SECONDARY);

        JButton expTxtBtn = AppTheme.createPrimaryButton("Export TXT Report");
        opt1.add(t1, BorderLayout.NORTH);
        opt1.add(d1, BorderLayout.CENTER);
        opt1.add(expTxtBtn, BorderLayout.EAST);
        center.add(opt1);

        // Option 2: Full Complaints Dataset (CSV)
        JPanel opt2 = new JPanel(new BorderLayout(8, 8));
        opt2.setBackground(AppTheme.BG_CARD);
        opt2.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel t2 = new JLabel("Complaints Dataset (.csv)");
        t2.setFont(AppTheme.FONT_BODY_BOLD);
        t2.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel d2 = new JLabel("Complete structured table of all complaints with timestamps, SLA, and staff.");
        d2.setFont(AppTheme.FONT_SMALL);
        d2.setForeground(AppTheme.TEXT_SECONDARY);

        JButton expCsvBtn = AppTheme.createSuccessButton("Export CSV Dataset");
        opt2.add(t2, BorderLayout.NORTH);
        opt2.add(d2, BorderLayout.CENTER);
        opt2.add(expCsvBtn, BorderLayout.EAST);
        center.add(opt2);

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // Handlers
        expTxtBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("complaint_audit_summary.txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    reportService.exportSummaryReportTXT(f);
                    JOptionPane.showMessageDialog(this, "Summary report exported successfully to:\n" + f.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        expCsvBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("complaints_export.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    reportService.exportComplaintsToCSV(f);
                    JOptionPane.showMessageDialog(this, "CSV file exported successfully to:\n" + f.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
