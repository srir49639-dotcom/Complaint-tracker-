package gui.organization;

import filehandling.BackupManager;
import filehandling.FileManager;
import gui.common.AppTheme;
import gui.common.UIUtils;
import repository.*;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class FileManagementDialog extends JDialog {

    private DefaultTableModel filesTableModel;

    public FileManagementDialog(Frame parent) {
        super(parent, "Physical File Management & Backup / Restore", true);
        setSize(920, 620);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
        refreshFileList();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("File Storage & Backup Management");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Inspect persistent flat files in data/ directory, generate backups, and restore archive snapshots.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Action Toolbar
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolBar.setBackground(AppTheme.BG_DARK);

        JButton backupBtn = AppTheme.createSuccessButton("💾 Create Full Backup");
        JButton restoreBtn = AppTheme.createPrimaryButton("📂 Restore from Backup");
        JButton syncBtn = AppTheme.createSecondaryButton("⚡ Sync All to Disk");
        JButton reloadBtn = AppTheme.createSecondaryButton("🔄 Refresh Status");

        toolBar.add(backupBtn);
        toolBar.add(restoreBtn);
        toolBar.add(syncBtn);
        toolBar.add(reloadBtn);

        // Files Table
        String[] cols = new String[]{"File Name", "Record Count", "File Size", "Last Modified", "Integrity Status"};
        filesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(filesTableModel);
        UIUtils.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(10, 20, 10, 20));
        center.add(toolBar, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        // Event Handlers
        backupBtn.addActionListener(e -> {
            try {
                String path = BackupManager.createBackup();
                JOptionPane.showMessageDialog(this,
                        "Backup created successfully!\nLocation: " + path,
                        "Backup Complete", JOptionPane.INFORMATION_MESSAGE);
                refreshFileList();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating backup: " + ex.getMessage(), "Backup Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        restoreBtn.addActionListener(e -> {
            CustomArrayList<BackupManager.BackupInfo> backups = BackupManager.listBackups();
            if (backups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No previous backups found in backup/ directory.", "No Backups", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] choices = new String[backups.size()];
            for (int i = 0; i < backups.size(); i++) {
                BackupManager.BackupInfo b = backups.get(i);
                choices[i] = b.folderName + " (" + b.fileCount + " files - " + DateFormatter.formatDateTime(b.timestamp) + ")";
            }

            String selected = (String) JOptionPane.showInputDialog(
                    this, "Select a backup snapshot to restore:", "Restore Backup",
                    JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]
            );

            if (selected != null) {
                String folderName = selected.split(" ")[0];
                int confirm = JOptionPane.showConfirmDialog(
                        this, "Warning: Restoring will overwrite existing data files with snapshot " + folderName + ".\nProceed?",
                        "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean ok = BackupManager.restoreBackup(folderName);
                        if (ok) {
                            // Reload all repositories
                            ComplaintRepository.getInstance().reloadFromFile();
                            CustomerRepository.getInstance().reloadFromFile();
                            StaffRepository.getInstance().reloadFromFile();
                            DepartmentRepository.getInstance().reloadFromFile();
                            HistoryRepository.getInstance().reloadFromFile();
                            ResponseRepository.getInstance().reloadFromFile();
                            FeedbackRepository.getInstance().reloadFromFile();
                            NotificationRepository.getInstance().reloadFromFile();

                            JOptionPane.showMessageDialog(this, "Data files successfully restored from backup " + folderName + "!", "Restore Complete", JOptionPane.INFORMATION_MESSAGE);
                            refreshFileList();
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error restoring backup: " + ex.getMessage(), "Restore Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        syncBtn.addActionListener(e -> {
            ComplaintRepository.getInstance().syncToFile();
            CustomerRepository.getInstance().syncToFile();
            StaffRepository.getInstance().syncToFile();
            DepartmentRepository.getInstance().syncToFile();
            HistoryRepository.getInstance().syncToFile();
            ResponseRepository.getInstance().syncToFile();
            FeedbackRepository.getInstance().syncToFile();
            NotificationRepository.getInstance().syncToFile();
            JOptionPane.showMessageDialog(this, "All in-memory data structures synchronized to physical disk files.", "Sync Complete", JOptionPane.INFORMATION_MESSAGE);
            refreshFileList();
        });

        reloadBtn.addActionListener(e -> refreshFileList());
    }

    private void refreshFileList() {
        filesTableModel.setRowCount(0);
        CustomArrayList<FileManager.FileInfo> files = FileManager.getAllDataFilesInfo();
        for (int i = 0; i < files.size(); i++) {
            FileManager.FileInfo f = files.get(i);
            filesTableModel.addRow(new Object[]{
                    f.fileName,
                    f.recordCount + " records",
                    String.format("%.1f KB", f.sizeBytes / 1024.0),
                    DateFormatter.formatDateTime(f.lastModified),
                    f.status
            });
        }
    }
}
