package gui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AppTheme {

    // Modern Deep Slate & Indigo / Teal Aesthetic
    public static final Color PRIMARY = new Color(37, 99, 235);        // Royal Blue / Indigo
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_DARK = new Color(30, 58, 138);

    public static final Color ACCENT = new Color(14, 165, 233);         // Sky Blue
    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald Green
    public static final Color WARNING = new Color(245, 158, 11);       // Amber
    public static final Color DANGER = new Color(239, 68, 68);         // Crimson Red
    public static final Color PURPLE = new Color(139, 92, 246);

    public static final Color BG_DARK = new Color(15, 23, 42);          // Deep Slate Background
    public static final Color BG_SIDEBAR = new Color(30, 41, 59);      // Slate 800
    public static final Color BG_CARD = new Color(30, 41, 59);         // Card Background
    public static final Color BG_CARD_LIGHT = new Color(51, 65, 85);   // Lighter slate
    public static final Color BG_INPUT = new Color(15, 23, 42);

    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);  // White
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);// Slate 400
    public static final Color TEXT_MUTED = new Color(100, 116, 139);    // Slate 500
    public static final Color BORDER_COLOR = new Color(51, 65, 85);

    // Modern Standard Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 12);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(PRIMARY_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SUCCESS);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_CARD_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_INPUT);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return pf;
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setForeground(TEXT_PRIMARY);
        ta.setBackground(BG_INPUT);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        return ta;
    }
}
