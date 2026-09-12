package gui.customer;

import gui.common.AppTheme;
import model.Customer;
import repository.CustomerRepository;
import utils.DateFormatter;
import utils.PasswordHasher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomerProfileDialog extends JDialog {

    private final Customer customer;
    private final CustomerRepository customerRepo = CustomerRepository.getInstance();

    public CustomerProfileDialog(Frame parent, Customer customer) {
        super(parent, "Customer Profile", true);
        this.customer = customer;

        setSize(480, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("My Account Profile");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_DARK);
        form.setBorder(new EmptyBorder(16, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        form.add(createItem("Customer ID", customer.getCustomerId()), gbc); gbc.gridy++;
        form.add(createItem("Full Name", customer.getFullName()), gbc); gbc.gridy++;
        form.add(createItem("Username", customer.getUsername()), gbc); gbc.gridy++;
        form.add(createItem("Email", customer.getEmail()), gbc); gbc.gridy++;
        form.add(createItem("Phone", customer.getPhone()), gbc); gbc.gridy++;
        form.add(createItem("Member Since", DateFormatter.formatDateOnly(customer.getRegisteredTimestamp())), gbc); gbc.gridy++;

        // Change Password Section
        JLabel pLbl = new JLabel("Change Password (optional):");
        pLbl.setFont(AppTheme.FONT_BODY_BOLD);
        pLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(pLbl, gbc); gbc.gridy++;

        JPasswordField newPassField = AppTheme.createPasswordField(20);
        newPassField.putClientProperty("JTextField.placeholderText", "Enter new password");
        form.add(newPassField, gbc); gbc.gridy++;

        add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton saveBtn = AppTheme.createPrimaryButton("Save Changes");
        JButton closeBtn = AppTheme.createSecondaryButton("Close");

        footer.add(closeBtn);
        footer.add(saveBtn);
        add(footer, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> {
            String newPass = new String(newPassField.getPassword()).trim();
            if (!newPass.isEmpty()) {
                customer.setPasswordHash(PasswordHasher.hashPassword(newPass));
                customerRepo.update(customer);
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Profile Saved", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                dispose();
            }
        });
    }

    private JPanel createItem(String k, String v) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(AppTheme.BG_DARK);
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
