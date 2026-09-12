package gui.organization;

import gui.common.AppTheme;
import gui.common.UIUtils;
import model.Department;
import repository.ComplaintRepository;
import repository.DepartmentRepository;
import utils.IdGenerator;
import datastructures.CustomArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DepartmentManagementDialog extends JDialog {

    private final DepartmentRepository deptRepo = DepartmentRepository.getInstance();
    private final ComplaintRepository complaintRepo = ComplaintRepository.getInstance();
    private DefaultTableModel tableModel;

    public DepartmentManagementDialog(Frame parent) {
        super(parent, "Department Management", true);
        setSize(880, 580);
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

        JLabel title = new JLabel("Organization Departments");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JButton addBtn = AppTheme.createSuccessButton("+ Add New Department");
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"Dept ID", "Department Name", "Active Complaints", "Capacity", "Default SLA", "Head / Lead"};
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
            JTextField descF = AppTheme.createTextField(20);
            JTextField capF = AppTheme.createTextField(10);
            capF.setText("50");
            JTextField slaF = AppTheme.createTextField(10);
            slaF.setText("48");
            JTextField headF = AppTheme.createTextField(20);

            JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
            form.add(new JLabel("Department Name:")); form.add(nameF);
            form.add(new JLabel("Description:")); form.add(descF);
            form.add(new JLabel("Max Capacity:")); form.add(capF);
            form.add(new JLabel("Default SLA (Hours):")); form.add(slaF);
            form.add(new JLabel("Department Head Name:")); form.add(headF);

            int opt = JOptionPane.showConfirmDialog(this, form, "Add Department", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION && !nameF.getText().trim().isEmpty()) {
                String dId = IdGenerator.generateDepartmentId();
                int cap = Integer.parseInt(capF.getText().trim());
                int sla = Integer.parseInt(slaF.getText().trim());
                Department d = new Department(dId, nameF.getText().trim(), descF.getText().trim(), "", headF.getText().trim(), cap, 0, sla, "");
                deptRepo.add(d);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        CustomArrayList<Department> list = deptRepo.findAll();
        for (int i = 0; i < list.size(); i++) {
            Department d = list.get(i);
            int activeCnt = complaintRepo.findByDepartmentName(d.getName()).size();
            tableModel.addRow(new Object[]{
                    d.getDepartmentId(),
                    d.getName(),
                    activeCnt + " complaints",
                    d.getCapacity() + " max",
                    d.getDefaultSlaHours() + " hours",
                    d.getHeadName().isEmpty() ? "Unassigned" : d.getHeadName()
            });
        }
    }
}
