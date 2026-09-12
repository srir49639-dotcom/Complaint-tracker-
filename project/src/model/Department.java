package model;

import java.io.Serializable;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private String departmentId;
    private String name;
    private String description;
    private String headStaffId;
    private String headName;
    private int capacity;
    private int activeComplaints;
    private int defaultSlaHours;
    private String parentDepartmentId; // For tree hierarchy (Tree DP)

    public Department() {
        this.capacity = 50;
        this.defaultSlaHours = 48;
    }

    public Department(String departmentId, String name, String description, String headStaffId, String headName,
                      int capacity, int activeComplaints, int defaultSlaHours, String parentDepartmentId) {
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.headStaffId = headStaffId;
        this.headName = headName;
        this.capacity = capacity;
        this.activeComplaints = activeComplaints;
        this.defaultSlaHours = defaultSlaHours;
        this.parentDepartmentId = parentDepartmentId != null ? parentDepartmentId : "";
    }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHeadStaffId() { return headStaffId; }
    public void setHeadStaffId(String headStaffId) { this.headStaffId = headStaffId; }

    public String getHeadName() { return headName; }
    public void setHeadName(String headName) { this.headName = headName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getActiveComplaints() { return activeComplaints; }
    public void setActiveComplaints(int activeComplaints) { this.activeComplaints = activeComplaints; }

    public int getDefaultSlaHours() { return defaultSlaHours; }
    public void setDefaultSlaHours(int defaultSlaHours) { this.defaultSlaHours = defaultSlaHours; }

    public String getParentDepartmentId() { return parentDepartmentId; }
    public void setParentDepartmentId(String parentDepartmentId) { this.parentDepartmentId = parentDepartmentId; }

    @Override
    public String toString() {
        return name;
    }
}
