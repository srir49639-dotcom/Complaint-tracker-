import filehandling.FileManager;
import gui.common.AppTheme;
import gui.customer.CustomerDashboardFrame;
import gui.customer.CustomerLoginRegisterFrame;
import gui.organization.OrgDashboardFrame;
import gui.organization.OrgLoginFrame;
import model.Customer;
import model.Staff;
import model.UserRole;
import repository.CustomerRepository;
import repository.DepartmentRepository;
import repository.StaffRepository;
import utils.SampleDataGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Initialize directories & sample data if needed
        FileManager.initializeDirectories();
        SampleDataGenerator.generateSampleDataIfEmpty();

        SwingUtilities.invokeLater(() -> {
            showPortalSelector();
        });
    }

    public static void showPortalSelector() {
        JFrame frame = new JFrame("Smart Complaint Tracker — Unified Portal Launcher");
        frame.setSize(560, 420);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(AppTheme.BG_DARK);
        frame.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel title = new JLabel("Smart Complaint Tracker", SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("File-Based Enterprise Complaint Management System", SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        frame.add(header, BorderLayout.NORTH);

        // Center Buttons
        JPanel center = new JPanel(new GridLayout(2, 1, 16, 16));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Portal 1: Customer Side
        JButton custBtn = AppTheme.createPrimaryButton("👤 Launch Customer Portal");
        custBtn.setFont(AppTheme.FONT_SUBTITLE);
        custBtn.addActionListener(e -> {
            new CustomerLoginRegisterFrame().setVisible(true);
        });

        // Portal 2: Organization Side
        JButton orgBtn = AppTheme.createSuccessButton("🏢 Launch Organization Portal");
        orgBtn.setFont(AppTheme.FONT_SUBTITLE);
        orgBtn.addActionListener(e -> {
            new OrgLoginFrame().setVisible(true);
        });

        center.add(custBtn);
        center.add(orgBtn);
        frame.add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JLabel note = new JLabel("Offline Pure Java • Zero Database • Flat File Storage • Advanced DSA Engine");
        note.setFont(AppTheme.FONT_SMALL);
        note.setForeground(AppTheme.TEXT_MUTED);
        footer.add(note);
        frame.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
