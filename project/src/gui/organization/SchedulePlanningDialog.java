package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Complaint;
import model.Staff;
import repository.ComplaintRepository;
import repository.StaffRepository;
import services.SchedulingService;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomPair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SchedulePlanningDialog extends JDialog {

    private final SchedulingService schedulingService = SchedulingService.getInstance();
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private final StaffRepository staffRepo = StaffRepository.getInstance();

    public SchedulePlanningDialog(Frame parent) {
        super(parent, "Workload Scheduling & Makespan Optimizer", true);
        setSize(880, 620);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Optimal Workload & Complaint Scheduling");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Distributes active complaints to balance staff load and minimize total resolution makespan.");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 20, 12, 20));

        CustomArrayList<Complaint> active = new CustomArrayList<>();
        CustomArrayList<Complaint> all = complaintRepo.findAll();
        for (int i = 0; i < all.size(); i++) {
            Complaint c = all.get(i);
            if (!c.isResolvedOrClosed()) {
                active.add(c);
            }
        }

        SchedulingService.SchedulePlan plan = schedulingService.generateWorkSchedule(active);

        // Summary KPI Banner
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        kpiPanel.setBackground(AppTheme.BG_DARK);

        kpiPanel.add(UIUtils.createCard("Active Complaints", String.valueOf(active.size()), AppTheme.PRIMARY));
        kpiPanel.add(UIUtils.createCard("Estimated Makespan", String.format("%.1f hrs", plan.estimatedMakespanHours), AppTheme.WARNING));
        kpiPanel.add(UIUtils.createCard("Schedule Quality", plan.scheduleQuality, AppTheme.SUCCESS));
        center.add(kpiPanel, BorderLayout.NORTH);

        // Schedule Table
        String[] cols = new String[]{"Staff Name", "Department", "Current Load", "Scheduled Complaints Count", "Assigned Tracking IDs"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        CustomArrayList<Staff> allStaff = staffRepo.findAll();
        for (int i = 0; i < allStaff.size(); i++) {
            Staff s = allStaff.get(i);
            CustomArrayList<Complaint> assignedList = plan.staffAllocations.get(s.getStaffId());
            int count = assignedList != null ? assignedList.size() : 0;

            StringBuilder ids = new StringBuilder();
            if (assignedList != null) {
                for (int j = 0; j < assignedList.size(); j++) {
                    if (j > 0) ids.append(", ");
                    ids.append(assignedList.get(j).getTrackingId());
                }
            }

            model.addRow(new Object[]{
                    s.getFullName(),
                    s.getDepartmentName(),
                    s.getCurrentWorkload() + " / " + s.getMaxWorkload(),
                    count,
                    ids.length() > 0 ? ids.toString() : "None"
            });
        }

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));
        center.add(scroll, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }
}
