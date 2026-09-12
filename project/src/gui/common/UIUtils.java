package gui.common;

import model.ComplaintStatus;
import model.PriorityLevel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIUtils {

    public static JPanel createCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(AppTheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(AppTheme.FONT_SMALL);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(accentColor != null ? accentColor : AppTheme.TEXT_PRIMARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setBackground(AppTheme.BG_CARD);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(AppTheme.BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setBackground(AppTheme.BG_DARK);
        header.setForeground(AppTheme.TEXT_SECONDARY);
        header.setFont(AppTheme.FONT_BODY_BOLD);
        header.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Custom status column renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                if (!isSel) {
                    c.setBackground((row % 2 == 0) ? AppTheme.BG_CARD : new Color(34, 47, 67));
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    public static Color getStatusColor(ComplaintStatus status) {
        if (status == null) return AppTheme.TEXT_SECONDARY;
        switch (status) {
            case NEW: return AppTheme.ACCENT;
            case UNDER_REVIEW: return AppTheme.PURPLE;
            case ASSIGNED: return new Color(59, 130, 246);
            case IN_PROGRESS: return AppTheme.WARNING;
            case WAITING_FOR_CUSTOMER: return new Color(249, 115, 22);
            case ESCALATED: return AppTheme.DANGER;
            case RESOLVED: return AppTheme.SUCCESS;
            case CLOSED: return new Color(100, 116, 139);
            case REOPENED: return new Color(236, 72, 153);
            case CANCELLED: return new Color(71, 85, 105);
            default: return AppTheme.TEXT_SECONDARY;
        }
    }

    public static Color getPriorityColor(PriorityLevel priority) {
        if (priority == null) return AppTheme.TEXT_SECONDARY;
        switch (priority) {
            case CRITICAL: return AppTheme.DANGER;
            case HIGH: return AppTheme.WARNING;
            case MEDIUM: return AppTheme.ACCENT;
            case LOW: return AppTheme.SUCCESS;
            default: return AppTheme.TEXT_SECONDARY;
        }
    }
}
