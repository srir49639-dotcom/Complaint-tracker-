package repository;

import filehandling.StaffFileHandler;
import interfaces.Repository;
import model.Staff;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class StaffRepository implements Repository<Staff, String> {

    private static StaffRepository instance;
    private final StaffFileHandler fileHandler;
    private final CustomHashTable<String, Staff> idIndex;
    private final CustomHashTable<String, Staff> usernameIndex;
    private final CustomHashTable<String, CustomArrayList<Staff>> departmentIndex;
    private final CustomArrayList<Staff> staffList;

    public StaffRepository() {
        this.fileHandler = new StaffFileHandler();
        this.idIndex = new CustomHashTable<>(32);
        this.usernameIndex = new CustomHashTable<>(32);
        this.departmentIndex = new CustomHashTable<>(32);
        this.staffList = new CustomArrayList<>(32);
        reloadFromFile();
    }

    public static synchronized StaffRepository getInstance() {
        if (instance == null) {
            instance = new StaffRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Staff staff) {
        if (staff == null || staff.getStaffId() == null) return;
        if (idIndex.containsKey(staff.getStaffId())) {
            update(staff);
            return;
        }

        staffList.add(staff);
        indexStaff(staff);

        try {
            fileHandler.append(staff);
        } catch (IOException e) {
            System.err.println("Error appending staff: " + e.getMessage());
        }
    }

    @Override
    public synchronized Staff findById(String id) {
        return (id != null) ? idIndex.get(id) : null;
    }

    public synchronized Staff findByUsername(String username) {
        return (username != null) ? usernameIndex.get(username.trim().toLowerCase()) : null;
    }

    public synchronized CustomArrayList<Staff> findByDepartmentName(String deptName) {
        if (deptName == null) return new CustomArrayList<>();
        CustomArrayList<Staff> list = departmentIndex.get(deptName.toLowerCase());
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Staff> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    @Override
    public synchronized CustomArrayList<Staff> findAll() {
        CustomArrayList<Staff> copy = new CustomArrayList<>(staffList.size());
        copy.addAll(staffList);
        return copy;
    }

    @Override
    public synchronized boolean update(Staff item) {
        if (item == null || item.getStaffId() == null) return false;
        Staff existing = idIndex.get(item.getStaffId());
        if (existing == null) return false;

        int idx = staffList.indexOf(existing);
        if (idx != -1) {
            staffList.set(idx, item);
        }

        rebuildIndices();
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Staff existing = idIndex.get(id);
        if (existing == null) return false;

        staffList.remove(existing);
        rebuildIndices();
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return id != null && idIndex.containsKey(id);
    }

    @Override
    public synchronized int count() {
        return staffList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(staffList);
        } catch (IOException e) {
            System.err.println("Error syncing staff: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            staffList.clear();
            CustomArrayList<Staff> loaded = fileHandler.loadAll();
            staffList.addAll(loaded);
            rebuildIndices();
        } catch (IOException e) {
            System.err.println("Error loading staff: " + e.getMessage());
        }
    }

    private void rebuildIndices() {
        idIndex.clear();
        usernameIndex.clear();
        departmentIndex.clear();

        for (int i = 0; i < staffList.size(); i++) {
            indexStaff(staffList.get(i));
        }
    }

    private void indexStaff(Staff s) {
        idIndex.put(s.getStaffId(), s);
        if (s.getUsername() != null) {
            usernameIndex.put(s.getUsername().toLowerCase(), s);
        }
        if (s.getDepartmentName() != null) {
            String dKey = s.getDepartmentName().toLowerCase();
            CustomArrayList<Staff> dList = departmentIndex.get(dKey);
            if (dList == null) {
                dList = new CustomArrayList<>();
                departmentIndex.put(dKey, dList);
            }
            dList.add(s);
        }
    }
}
