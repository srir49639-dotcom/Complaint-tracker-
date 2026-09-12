package gui.common;

import model.Complaint;
import model.ComplaintStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimelinePanel extends JPanel {

    private final Complaint complaint;

    public TimelinePanel(Complaint complaint) {
        this.complaint = complaint;
        setBackground(AppTheme.BG_CARD);
        setLayout(new GridLayout(1, 6, 8, 0));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        buildTimeline();
    }

    private void buildTimeline() {
        String[] stages = new String[]{"Submitted", "Under Review", "Assigned", "In Progress", "Resolved", "Closed"};
        int currentStageIndex = getStageIndex(complaint.getStatus());

        for (int i = 0; i < stages.length; i++) {
            boolean completed = (i <= currentStageIndex);
            boolean current = (i == currentStageIndex);

            JPanel stageCard = new JPanel(new BorderLayout(4, 4));
            stageCard.setBackground(current ? new Color(30, 58, 138) : (completed ? new Color(20, 83, 45) : AppTheme.BG_CARD_LIGHT));
            stageCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(current ? AppTheme.ACCENT : (completed ? AppTheme.SUCCESS : AppTheme.BORDER_COLOR), 1),
                    new EmptyBorder(8, 6, 8, 6)
            ));

            JLabel numLabel = new JLabel((completed ? "✓ " : (i + 1) + ". ") + stages[i], SwingConstants.CENTER);
            numLabel.setFont(current ? AppTheme.FONT_BODY_BOLD : AppTheme.FONT_SMALL);
            numLabel.setForeground(completed || current ? Color.WHITE : AppTheme.TEXT_MUTED);

            stageCard.add(numLabel, BorderLayout.CENTER);
            add(stageCard);
        }
    }

    private int getStageIndex(ComplaintStatus status) {
        if (status == null) return 0;
        switch (status) {
            case NEW: return 0;
            case UNDER_REVIEW: return 1;
            case ASSIGNED: return 2;
            case IN_PROGRESS:
            case WAITING_FOR_CUSTOMER:
            case ESCALATED:
            case REOPENED: return 3;
            case RESOLVED: return 4;
            case CLOSED:
            case CANCELLED: return 5;
            default: return 0;
        }
    }
}
