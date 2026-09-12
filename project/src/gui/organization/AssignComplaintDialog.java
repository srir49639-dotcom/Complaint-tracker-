package gui.organization;

import gui.common.AppTheme;
import model.Complaint;
import model.Department;
import model.Staff;
import model.User;
import repository.DepartmentRepository;
import repository.StaffRepository;
import services.AssignmentEngineService;
import services.ComplaintService;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AssignComplaintDialog extends JDialog {

    private final Complaint complaint;
    private final User currentUser;
    private final Runnable onAssignedCallback;

    private final AssignmentEngineService assignmentEngine = AssignmentEngineService.getInstance();
    private final ComplaintService complaintService = ComplaintService.getInstance();
    private final DepartmentRepository deptRepo = DepartmentRepository.getInstance();
    private final StaffRepository staffRepo = StaffRepository.getInstance();

    public AssignComplaintDialog(Dialog parent, Complaint complaint, User currentUser, Runnable onAssignedCallback) {
        super(parent, "Complaint Assignment Engine — " + complaint.getTrackingId(), true);
        this.complaint = complaint;
        this.currentUser = currentUser;
        this.onAssignedCallback = onAssignedCallback;

        setSize(650, 580);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Smart Complaint Assignment");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Tracking ID: " + complaint.getTrackingId() + " | Category: " + complaint.getCategory());
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Center: Recommendation Card + Manual Selector
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(14, 20, 14, 20));

        // 1. Recommendation Card (Engine Output)
        AssignmentEngineService.Recommendation rec = assignmentEngine.recommendAssignment(complaint);

        JPanel recCard = new JPanel(new BorderLayout(6, 6));
        recCard.setBackground(new Color(30, 58, 138));
        recCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppTheme.ACCENT, 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel recTitle = new JLabel("RECOMMENDED ALLOCATION");
        recTitle.setFont(AppTheme.FONT_SMALL);
        recTitle.setForeground(AppTheme.ACCENT);

        String deptName = (rec != null && rec.recommendedDepartment != null) ? rec.recommendedDepartment.getName() : "General";
        String staffName = (rec != null && rec.recommendedStaff != null) ? rec.recommendedStaff.getFullName() + " (" + rec.recommendedStaff.getDepartmentName() + ")" : "Available Pool";
        String reason = (rec != null) ? rec.reasoning : "Optimal allocation calculated based on category and staff availability.";

        JLabel recVal = new JLabel("Department: " + deptName + "  |  Staff: " + staffName);
        recVal.setFont(AppTheme.FONT_BODY_BOLD);
        recVal.setForeground(Color.WHITE);

        JLabel recReason = new JLabel("Reason: " + reason);
        recReason.setFont(AppTheme.FONT_BODY);
        recReason.setForeground(AppTheme.TEXT_PRIMARY);

        recCard.add(recTitle, BorderLayout.NORTH);
        recCard.add(recVal, BorderLayout.CENTER);
        recCard.add(recReason, BorderLayout.SOUTH);
        center.add(recCard, BorderLayout.NORTH);

        // 2. Selection Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel dLbl = new JLabel("Assign to Department:");
        dLbl.setFont(AppTheme.FONT_BODY_BOLD);
        dLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(dLbl, gbc); gbc.gridy++;

        CustomArrayList<Department> allDepts = deptRepo.findAll();
        String[] deptsArray = new String[allDepts.size()];
        int selectedDeptIdx = 0;
        for (int i = 0; i < allDepts.size(); i++) {
            deptsArray[i] = allDepts.get(i).getName();
            if (rec != null && rec.recommendedDepartment != null && deptsArray[i].equalsIgnoreCase(rec.recommendedDepartment.getName())) {
                selectedDeptIdx = i;
            }
        }
        JComboBox<String> deptCombo = new JComboBox<>(deptsArray);
        if (deptsArray.length > 0) deptCombo.setSelectedIndex(selectedDeptIdx);
        deptCombo.setBackground(AppTheme.BG_INPUT);
        deptCombo.setForeground(AppTheme.TEXT_PRIMARY);
        deptCombo.setFont(AppTheme.FONT_BODY);
        form.add(deptCombo, gbc); gbc.gridy++;

        JLabel sLbl = new JLabel("Assign to Staff Member:");
        sLbl.setFont(AppTheme.FONT_BODY_BOLD);
        sLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(sLbl, gbc); gbc.gridy++;

        JComboBox<Staff> staffCombo = new JComboBox<>();
        staffCombo.setBackground(AppTheme.BG_INPUT);
        staffCombo.setForeground(AppTheme.TEXT_PRIMARY);
        staffCombo.setFont(AppTheme.FONT_BODY);
        form.add(staffCombo, gbc); gbc.gridy++;

        Runnable updateStaffDropdown = () -> {
            staffCombo.removeAllItems();
            String selDept = (String) deptCombo.getSelectedItem();
            CustomArrayList<Staff> available = (selDept != null) ? staffRepo.findByDepartmentName(selDept) : staffRepo.findAll();
            for (int i = 0; i < available.size(); i++) {
                Staff s = available.get(i);
                staffCombo.addItem(s);
            }
            if (rec != null && rec.recommendedStaff != null) {
                for (int i = 0; i < staffCombo.getItemCount(); i++) {
                    Staff s = staffCombo.getItemAt(i);
                    if (s.getStaffId().equals(rec.recommendedStaff.getStaffId())) {
                        staffCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        };

        deptCombo.addActionListener(e -> updateStaffDropdown.run());
        updateStaffDropdown.run();

        JLabel nLbl = new JLabel("Assignment Notes / Instructions:");
        nLbl.setFont(AppTheme.FONT_BODY_BOLD);
        nLbl.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(nLbl, gbc); gbc.gridy++;

        JTextField notesField = AppTheme.createTextField(30);
        notesField.setText(reason);
        form.add(notesField, gbc); gbc.gridy++;

        center.add(form, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);

        JButton cancelBtn = AppTheme.createSecondaryButton("Cancel");
        JButton confirmBtn = AppTheme.createPrimaryButton("Confirm Assignment");
        footer.add(cancelBtn);
        footer.add(confirmBtn);
        add(footer, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());

        confirmBtn.addActionListener(e -> {
            String selectedDept = (String) deptCombo.getSelectedItem();
            Staff selectedStaff = (Staff) staffCombo.getSelectedItem();
            String notes = notesField.getText().trim();

            String sId = selectedStaff != null ? selectedStaff.getStaffId() : "";

            complaintService.assignComplaint(
                    complaint.getComplaintId(), selectedDept, sId,
                    currentUser.getUserId(), currentUser.getFullName(), notes
            );

            JOptionPane.showMessageDialog(this,
                    "Complaint assigned successfully to " + (selectedStaff != null ? selectedStaff.getFullName() : selectedDept) + "!",
                    "Assignment Confirmed", JOptionPane.INFORMATION_MESSAGE);

            if (onAssignedCallback != null) onAssignedCallback.run();
            dispose();
        });
    }
}
