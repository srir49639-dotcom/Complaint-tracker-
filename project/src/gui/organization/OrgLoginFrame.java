package gui.organization;

import gui.common.AppTheme;
import model.Staff;
import model.User;
import services.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OrgLoginFrame extends JFrame {

    private final AuthenticationService authService = AuthenticationService.getInstance();

    public OrgLoginFrame() {
        setTitle("Organization Portal — Smart Complaint Tracker");
        setSize(480, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(5, 5));
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel title = new JLabel("Organization Management Portal", SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Staff & Administrator Authentication", SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Login Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_DARK);
        form.setBorder(new EmptyBorder(24, 30, 24, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel uLbl = new JLabel("Staff Username / Admin ID");
        uLbl.setFont(AppTheme.FONT_BODY_BOLD);
        uLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(uLbl, gbc); gbc.gridy++;

        JTextField usernameField = AppTheme.createTextField(20);
        usernameField.setText("admin"); // Default convenience
        form.add(usernameField, gbc); gbc.gridy++;

        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(AppTheme.FONT_BODY_BOLD);
        pLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(pLbl, gbc); gbc.gridy++;

        JPasswordField passwordField = AppTheme.createPasswordField(20);
        passwordField.setText("admin123");
        form.add(passwordField, gbc); gbc.gridy++;

        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(AppTheme.FONT_SMALL);
        msgLabel.setForeground(AppTheme.DANGER);
        form.add(msgLabel, gbc); gbc.gridy++;

        JButton loginBtn = AppTheme.createPrimaryButton("Access Organization Portal");
        form.add(loginBtn, gbc);

        add(form, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword());

            if (u.isEmpty() || p.isEmpty()) {
                msgLabel.setText("Please enter username and password.");
                return;
            }

            User user = authService.authenticateStaffOrAdmin(u, p);
            if (user != null) {
                new OrgDashboardFrame(user).setVisible(true);
                dispose();
            } else {
                msgLabel.setText("Invalid credentials or unauthorized staff account.");
            }
        });
    }
}
