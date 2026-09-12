package gui.customer;

import gui.common.AppTheme;
import model.Customer;
import model.Notification;
import repository.NotificationRepository;
import utils.DateFormatter;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CustomerNotificationsDialog extends JDialog {

    private final Customer customer;
    private final Runnable onDismiss;
    private final NotificationRepository notifRepo = NotificationRepository.getInstance();

    public CustomerNotificationsDialog(Frame parent, Customer customer, Runnable onDismiss) {
        super(parent, "My Notifications", true);
        this.customer = customer;
        this.onDismiss = onDismiss;

        setSize(650, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("System & Complaint Notifications");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JButton markAllBtn = AppTheme.createSecondaryButton("Mark All as Read");
        header.add(title, BorderLayout.WEST);
        header.add(markAllBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // List
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(AppTheme.BG_DARK);
        listPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        CustomArrayList<Notification> notifs = notifRepo.findByUserId(customer.getCustomerId());

        if (notifs.isEmpty()) {
            JLabel empty = new JLabel("You have no notifications.", SwingConstants.CENTER);
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.TEXT_MUTED);
            listPanel.add(empty);
        } else {
            for (int i = 0; i < notifs.size(); i++) {
                Notification n = notifs.get(i);
                JPanel card = new JPanel(new BorderLayout(4, 4));
                card.setBackground(n.isRead() ? AppTheme.BG_CARD : new Color(30, 58, 138));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(n.isRead() ? AppTheme.BORDER_COLOR : AppTheme.ACCENT, 1),
                        new EmptyBorder(10, 14, 10, 14)
                ));

                JLabel t = new JLabel((n.isRead() ? "" : "● ") + n.getTitle());
                t.setFont(AppTheme.FONT_BODY_BOLD);
                t.setForeground(Color.WHITE);

                JLabel m = new JLabel(n.getMessage());
                m.setFont(AppTheme.FONT_BODY);
                m.setForeground(AppTheme.TEXT_PRIMARY);

                JLabel d = new JLabel(DateFormatter.formatDateTime(n.getTimestamp()));
                d.setFont(AppTheme.FONT_SMALL);
                d.setForeground(AppTheme.TEXT_MUTED);

                card.add(t, BorderLayout.NORTH);
                card.add(m, BorderLayout.CENTER);
                card.add(d, BorderLayout.SOUTH);

                listPanel.add(card);
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        markAllBtn.addActionListener(e -> {
            notifRepo.markAllAsRead(customer.getCustomerId());
            if (onDismiss != null) onDismiss.run();
            dispose();
        });
    }
}
