package gui.customer;

import gui.common.AppTheme;
import model.Customer;
import model.User;
import services.AuthenticationService;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CustomerLoginRegisterFrame extends JFrame {

    private final AuthenticationService authService = AuthenticationService.getInstance();

    public CustomerLoginRegisterFrame() {
        setTitle("Smart Complaint Tracker — Customer Portal");
        setSize(480, 620);
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
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Customer Portal", SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Submit & Track Your Service Complaints", SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Tabbed Panel for Login / Register
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FONT_BODY_BOLD);
        tabs.setBackground(AppTheme.BG_DARK);
        tabs.setForeground(AppTheme.TEXT_PRIMARY);

        tabs.addTab("Customer Login", createLoginPanel());
        tabs.addTab("New Registration", createRegisterPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BG_DARK);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel uLbl = new JLabel("Username / Account ID");
        uLbl.setFont(AppTheme.FONT_BODY_BOLD);
        uLbl.setForeground(AppTheme.TEXT_SECONDARY);
        panel.add(uLbl, gbc);

        gbc.gridy++;
        JTextField usernameField = AppTheme.createTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridy++;
        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(AppTheme.FONT_BODY_BOLD);
        pLbl.setForeground(AppTheme.TEXT_SECONDARY);
        panel.add(pLbl, gbc);

        gbc.gridy++;
        JPasswordField passwordField = AppTheme.createPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(AppTheme.FONT_SMALL);
        msgLabel.setForeground(AppTheme.DANGER);
        panel.add(msgLabel, gbc);

        gbc.gridy++;
        JButton loginBtn = AppTheme.createPrimaryButton("Login to Customer Dashboard");
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                msgLabel.setText("Please enter both username and password.");
                return;
            }

            User user = authService.authenticateCustomer(u, p);
            if (user instanceof Customer) {
                new CustomerDashboardFrame((Customer) user).setVisible(true);
                dispose();
            } else {
                msgLabel.setText("Invalid customer credentials. Please try again.");
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField nameField = AppTheme.createTextField(20);
        JTextField emailField = AppTheme.createTextField(20);
        JTextField phoneField = AppTheme.createTextField(20);
        JTextField userField = AppTheme.createTextField(20);
        JPasswordField passField = AppTheme.createPasswordField(20);
        JTextField addressField = AppTheme.createTextField(20);

        panel.add(createFieldLabel("Full Name"), gbc); gbc.gridy++;
        panel.add(nameField, gbc); gbc.gridy++;

        panel.add(createFieldLabel("Email Address"), gbc); gbc.gridy++;
        panel.add(emailField, gbc); gbc.gridy++;

        panel.add(createFieldLabel("Phone Number"), gbc); gbc.gridy++;
        panel.add(phoneField, gbc); gbc.gridy++;

        panel.add(createFieldLabel("Preferred Username"), gbc); gbc.gridy++;
        panel.add(userField, gbc); gbc.gridy++;

        panel.add(createFieldLabel("Password"), gbc); gbc.gridy++;
        panel.add(passField, gbc); gbc.gridy++;

        panel.add(createFieldLabel("Address / Location"), gbc); gbc.gridy++;
        panel.add(addressField, gbc); gbc.gridy++;

        JLabel regMsg = new JLabel("", SwingConstants.CENTER);
        regMsg.setFont(AppTheme.FONT_SMALL);
        panel.add(regMsg, gbc); gbc.gridy++;

        JButton regBtn = AppTheme.createSuccessButton("Create Customer Account");
        panel.add(regBtn, gbc);

        regBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String addr = addressField.getText().trim();

            if (!ValidationUtils.isNotEmpty(name) || !ValidationUtils.isNotEmpty(user) || pass.isEmpty()) {
                regMsg.setForeground(AppTheme.DANGER);
                regMsg.setText("Name, username and password are required.");
                return;
            }

            Customer registered = authService.registerCustomer(name, user, pass, email, phone, addr);
            if (registered != null) {
                JOptionPane.showMessageDialog(this,
                        "Registration Successful!\nYour Customer ID: " + registered.getCustomerId() + "\nPlease login now.",
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText(""); emailField.setText(""); phoneField.setText("");
                userField.setText(""); passField.setText(""); addressField.setText("");
                regMsg.setText("");
            } else {
                regMsg.setForeground(AppTheme.DANGER);
                regMsg.setText("Username already exists. Please choose another.");
            }
        });

        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        return l;
    }
}
