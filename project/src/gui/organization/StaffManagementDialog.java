package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Department;
import model.Staff;
import model.Staff.StaffStatus;
import model.UserRole;
import repository.DepartmentRepository;
import repository.StaffRepository;
import utils.IdGenerator;
import utils.PasswordHasher;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffManagementDialog extends JDialog {

    private final StaffRepository staffRepo = StaffRepository.getInstance();
    private final DepartmentRepository deptRepo = DepartmentRepository.getInstance();
    private DefaultTableModel tableModel;

    public StaffManagementDialog(Frame parent) {
        super(parent, "Staff & Capacity Management", true);
        setSize(920, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        buildUI();
        refreshTable();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.BG_SIDEBAR);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Staff Directory & Workload Capacity");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JButton addBtn = AppTheme.createSuccessButton("+ Add Staff Member");
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"Staff ID", "Full Name", "Department", "Specialization", "Workload", "Status", "Rating"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        UIUtils.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new LineBorder(AppTheme.BORDER_COLOR, 1));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(AppTheme.BG_DARK);
        center.setBorder(new EmptyBorder(12, 20, 12, 20));
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(AppTheme.BG_SIDEBAR);
        JButton closeBtn = AppTheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JTextField nameF = AppTheme.createTextField(20);
            JTextField userF = AppTheme.createTextField(20);
            JPasswordField passF = AppTheme.createPasswordField(20);
            JTextField emailF = AppTheme.createTextField(20);
            JTextField phoneF = AppTheme.createTextField(20);
            JTextField specF = AppTheme.createTextField(20);

            CustomArrayList<Department> depts = deptRepo.findAll();
            String[] deptArr = new String[depts.size()];
            for (int i = 0; i < depts.size(); i++) deptArr[i] = depts.get(i).getName();
            JComboBox<String> deptC = new JComboBox<>(deptArr);

            JComboBox<StaffStatus> statC = new JComboBox<>(StaffStatus.values());

            JPanel form = new JPanel(new GridLayout(8, 2, 8, 6));
            form.add(new JLabel("Full Name:")); form.add(nameF);
            form.add(new JLabel("Username:")); form.add(userF);
            form.add(new JLabel("Password:")); form.add(passF);
            form.add(new JLabel("Email:")); form.add(emailF);
            form.add(new JLabel("Phone:")); form.add(phoneF);
            form.add(new JLabel("Department:")); form.add(deptC);
            form.add(new JLabel("Specialization:")); form.add(specF);
            form.add(new JLabel("Availability Status:")); form.add(statC);

            int opt = JOptionPane.showConfirmDialog(this, form, "Add Staff Member", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION && !nameF.getText().trim().isEmpty() && !userF.getText().trim().isEmpty()) {
                String sid = IdGenerator.generateStaffId();
                String deptName = (String) deptC.getSelectedItem();
                Department d = deptRepo.findByName(deptName);
                String pHash = PasswordHasher.hashPassword(new String(passF.getPassword()).trim());

                Staff st = new Staff(sid, userF.getText().trim(), pHash, nameF.getText().trim(),
                        emailF.getText().trim(), phoneF.getText().trim(),
                        d != null ? d.getDepartmentId() : "", deptName,
                        specF.getText().trim(), (StaffStatus) statC.getSelectedItem(),
                        0, 8, 4.8, UserRole.STAFF);

                staffRepo.add(st);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        CustomArrayList<Staff> list = staffRepo.findAll();
        for (int i = 0; i < list.size(); i++) {
            Staff s = list.get(i);
            tableModel.addRow(new Object[]{
                    s.getStaffId(),
                    s.getFullName(),
                    s.getDepartmentName(),
                    s.getSpecialization(),
                    s.getCurrentWorkload() + " / " + s.getMaxWorkload(),
                    s.getStatus().name(),
                    String.format("%.1f ★", s.getEfficiencyRating())
            });
        }
    }
}
