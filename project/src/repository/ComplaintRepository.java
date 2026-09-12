package repository;

import filehandling.ComplaintFileHandler;
import interfaces.Repository;
import model.Complaint;
import model.ComplaintStatus;
import model.PriorityLevel;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import datastructures.CustomPriorityQueue;

import java.io.IOException;

public class ComplaintRepository implements Repository<Complaint, String> {

    private static ComplaintRepository instance;

    private final ComplaintFileHandler fileHandler;
    private final CustomHashTable<String, Complaint> idIndex;
    private final CustomHashTable<String, Complaint> trackingIndex;
    private final CustomHashTable<String, CustomArrayList<Complaint>> customerIndex;
    private final CustomHashTable<String, CustomArrayList<Complaint>> staffIndex;
    private final CustomHashTable<String, CustomArrayList<Complaint>> departmentIndex;
    private final CustomArrayList<Complaint> complaintsList;

    public ComplaintRepository() {
        this.fileHandler = new ComplaintFileHandler();
        this.idIndex = new CustomHashTable<>(128);
        this.trackingIndex = new CustomHashTable<>(128);
        this.customerIndex = new CustomHashTable<>(64);
        this.staffIndex = new CustomHashTable<>(64);
        this.departmentIndex = new CustomHashTable<>(32);
        this.complaintsList = new CustomArrayList<>(128);
        reloadFromFile();
    }

    public static synchronized ComplaintRepository getInstance() {
        if (instance == null) {
            instance = new ComplaintRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Complaint complaint) {
        if (complaint == null || complaint.getComplaintId() == null) return;
        if (idIndex.containsKey(complaint.getComplaintId())) {
            update(complaint);
            return;
        }

        complaintsList.add(complaint);
        indexComplaint(complaint);

        try {
            fileHandler.append(complaint);
        } catch (IOException e) {
            System.err.println("Error appending complaint to file: " + e.getMessage());
        }
    }

    @Override
    public synchronized Complaint findById(String id) {
        if (id == null) return null;
        return idIndex.get(id);
    }

    public synchronized Complaint findByTrackingId(String trackingId) {
        if (trackingId == null) return null;
        return trackingIndex.get(trackingId.trim().toUpperCase());
    }

    @Override
    public synchronized CustomArrayList<Complaint> findAll() {
        CustomArrayList<Complaint> list = new CustomArrayList<>(complaintsList.size());
        list.addAll(complaintsList);
        return list;
    }

    public synchronized CustomArrayList<Complaint> findByCustomerId(String customerId) {
        CustomArrayList<Complaint> list = customerIndex.get(customerId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Complaint> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    public synchronized CustomArrayList<Complaint> findByStaffId(String staffId) {
        CustomArrayList<Complaint> list = staffIndex.get(staffId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Complaint> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    public synchronized CustomArrayList<Complaint> findByDepartmentName(String deptName) {
        if (deptName == null) return new CustomArrayList<>();
        CustomArrayList<Complaint> list = departmentIndex.get(deptName.toLowerCase());
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Complaint> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    public synchronized CustomArrayList<Complaint> findByStatus(ComplaintStatus status) {
        CustomArrayList<Complaint> result = new CustomArrayList<>();
        for (int i = 0; i < complaintsList.size(); i++) {
            Complaint c = complaintsList.get(i);
            if (c.getStatus() == status) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public synchronized boolean update(Complaint item) {
        if (item == null || item.getComplaintId() == null) return false;
        Complaint existing = idIndex.get(item.getComplaintId());
        if (existing == null) return false;

        // Replace in list
        int idx = complaintsList.indexOf(existing);
        if (idx != -1) {
            complaintsList.set(idx, item);
        }

        rebuildIndices();
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Complaint existing = idIndex.get(id);
        if (existing == null) return false;

        complaintsList.remove(existing);
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
        return complaintsList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(complaintsList);
        } catch (IOException e) {
            System.err.println("Error saving complaints to file: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            complaintsList.clear();
            CustomArrayList<Complaint> loaded = fileHandler.loadAll();
            complaintsList.addAll(loaded);
            rebuildIndices();
        } catch (IOException e) {
            System.err.println("Error loading complaints from file: " + e.getMessage());
        }
    }

    private void rebuildIndices() {
        idIndex.clear();
        trackingIndex.clear();
        customerIndex.clear();
        staffIndex.clear();
        departmentIndex.clear();

        for (int i = 0; i < complaintsList.size(); i++) {
            indexComplaint(complaintsList.get(i));
        }
    }

    private void indexComplaint(Complaint c) {
        idIndex.put(c.getComplaintId(), c);
        if (c.getTrackingId() != null) {
            trackingIndex.put(c.getTrackingId().toUpperCase(), c);
        }

        if (c.getCustomerId() != null) {
            CustomArrayList<Complaint> cList = customerIndex.get(c.getCustomerId());
            if (cList == null) {
                cList = new CustomArrayList<>();
                customerIndex.put(c.getCustomerId(), cList);
            }
            cList.add(c);
        }

        if (c.getAssignedStaffId() != null && !c.getAssignedStaffId().isEmpty()) {
            CustomArrayList<Complaint> sList = staffIndex.get(c.getAssignedStaffId());
            if (sList == null) {
                sList = new CustomArrayList<>();
                staffIndex.put(c.getAssignedStaffId(), sList);
            }
            sList.add(c);
        }

        if (c.getDepartmentName() != null && !c.getDepartmentName().isEmpty()) {
            String dKey = c.getDepartmentName().toLowerCase();
            CustomArrayList<Complaint> dList = departmentIndex.get(dKey);
            if (dList == null) {
                dList = new CustomArrayList<>();
                departmentIndex.put(dKey, dList);
            }
            dList.add(c);
        }
    }
}
