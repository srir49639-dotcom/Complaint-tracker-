package model;

public class Staff extends User {
    private static final long serialVersionUID = 1L;

    public enum StaffStatus {
        AVAILABLE,
        BUSY,
        OFFLINE,
        ON_LEAVE
    }

    private String departmentId;
    private String departmentName;
    private String specialization;
    private StaffStatus status;
    private int currentWorkload;
    private int maxWorkload;
    private double efficiencyRating;

    public Staff() {
        super();
        this.role = UserRole.STAFF;
        this.status = StaffStatus.AVAILABLE;
        this.currentWorkload = 0;
        this.maxWorkload = 8;
        this.efficiencyRating = 4.5;
    }

    public Staff(String staffId, String username, String passwordHash, String fullName, String email, String phone,
                 String departmentId, String departmentName, String specialization, StaffStatus status,
                 int currentWorkload, int maxWorkload, double efficiencyRating, UserRole role) {
        super(staffId, username, passwordHash, fullName, email, phone, role);
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.specialization = specialization;
        this.status = status != null ? status : StaffStatus.AVAILABLE;
        this.currentWorkload = currentWorkload;
        this.maxWorkload = maxWorkload > 0 ? maxWorkload : 8;
        this.efficiencyRating = efficiencyRating;
    }

    public String getStaffId() { return getUserId(); }
    public void setStaffId(String staffId) { setUserId(staffId); }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public StaffStatus getStatus() { return status; }
    public void setStatus(StaffStatus status) { this.status = status; }

    public int getCurrentWorkload() { return currentWorkload; }
    public void setCurrentWorkload(int currentWorkload) { this.currentWorkload = currentWorkload; }

    public int getMaxWorkload() { return maxWorkload; }
    public void setMaxWorkload(int maxWorkload) { this.maxWorkload = maxWorkload; }

    public double getEfficiencyRating() { return efficiencyRating; }
    public void setEfficiencyRating(double efficiencyRating) { this.efficiencyRating = efficiencyRating; }

    public boolean canAcceptComplaint() {
        return (status == StaffStatus.AVAILABLE || (status == StaffStatus.BUSY && currentWorkload < maxWorkload))
                && currentWorkload < maxWorkload;
    }
}
