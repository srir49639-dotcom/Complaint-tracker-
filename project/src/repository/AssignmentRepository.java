package repository;

import filehandling.AssignmentFileHandler;
import interfaces.Repository;
import model.Assignment;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class AssignmentRepository implements Repository<Assignment, String> {

    private static AssignmentRepository instance;
    private final AssignmentFileHandler fileHandler;
    private final CustomHashTable<String, CustomArrayList<Assignment>> complaintIndex;
    private final CustomHashTable<String, CustomArrayList<Assignment>> staffIndex;
    private final CustomArrayList<Assignment> assignmentList;

    public AssignmentRepository() {
        this.fileHandler = new AssignmentFileHandler();
        this.complaintIndex = new CustomHashTable<>(64);
        this.staffIndex = new CustomHashTable<>(64);
        this.assignmentList = new CustomArrayList<>(128);
        reloadFromFile();
    }

    public static synchronized AssignmentRepository getInstance() {
        if (instance == null) {
            instance = new AssignmentRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Assignment item) {
        if (item == null) return;
        assignmentList.add(item);
        indexItem(item);

        try {
            fileHandler.append(item);
        } catch (IOException e) {
            System.err.println("Error appending assignment: " + e.getMessage());
        }
    }

    public synchronized CustomArrayList<Assignment> findByComplaintId(String complaintId) {
        if (complaintId == null) return new CustomArrayList<>();
        CustomArrayList<Assignment> list = complaintIndex.get(complaintId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Assignment> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    public synchronized CustomArrayList<Assignment> findByStaffId(String staffId) {
        if (staffId == null) return new CustomArrayList<>();
        CustomArrayList<Assignment> list = staffIndex.get(staffId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Assignment> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    @Override
    public synchronized Assignment findById(String id) {
        for (int i = 0; i < assignmentList.size(); i++) {
            if (assignmentList.get(i).getAssignmentId().equals(id)) return assignmentList.get(i);
        }
        return null;
    }

    @Override
    public synchronized CustomArrayList<Assignment> findAll() {
        CustomArrayList<Assignment> copy = new CustomArrayList<>(assignmentList.size());
        copy.addAll(assignmentList);
        return copy;
    }

    @Override
    public synchronized boolean update(Assignment item) {
        if (item == null) return false;
        Assignment existing = findById(item.getAssignmentId());
        if (existing == null) return false;

        int idx = assignmentList.indexOf(existing);
        if (idx != -1) {
            assignmentList.set(idx, item);
        }
        rebuildIndices();
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Assignment existing = findById(id);
        if (existing == null) return false;
        assignmentList.remove(existing);
        rebuildIndices();
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id) != null;
    }

    @Override
    public synchronized int count() {
        return assignmentList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(assignmentList);
        } catch (IOException e) {
            System.err.println("Error syncing assignments: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            assignmentList.clear();
            CustomArrayList<Assignment> loaded = fileHandler.loadAll();
            assignmentList.addAll(loaded);
            rebuildIndices();
        } catch (IOException e) {
            System.err.println("Error loading assignments: " + e.getMessage());
        }
    }

    private void rebuildIndices() {
        complaintIndex.clear();
        staffIndex.clear();
        for (int i = 0; i < assignmentList.size(); i++) {
            indexItem(assignmentList.get(i));
        }
    }

    private void indexItem(Assignment a) {
        if (a.getComplaintId() != null) {
            CustomArrayList<Assignment> cList = complaintIndex.get(a.getComplaintId());
            if (cList == null) {
                cList = new CustomArrayList<>();
                complaintIndex.put(a.getComplaintId(), cList);
            }
            cList.add(a);
        }
        if (a.getStaffId() != null) {
            CustomArrayList<Assignment> sList = staffIndex.get(a.getStaffId());
            if (sList == null) {
                sList = new CustomArrayList<>();
                staffIndex.put(a.getStaffId(), sList);
            }
            sList.add(a);
        }
    }
}
