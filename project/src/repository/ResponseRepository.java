package repository;

import filehandling.ResponseFileHandler;
import interfaces.Repository;
import model.ComplaintResponse;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class ResponseRepository implements Repository<ComplaintResponse, String> {

    private static ResponseRepository instance;
    private final ResponseFileHandler fileHandler;
    private final CustomHashTable<String, CustomArrayList<ComplaintResponse>> complaintIndex;
    private final CustomArrayList<ComplaintResponse> responseList;

    public ResponseRepository() {
        this.fileHandler = new ResponseFileHandler();
        this.complaintIndex = new CustomHashTable<>(128);
        this.responseList = new CustomArrayList<>(256);
        reloadFromFile();
    }

    public static synchronized ResponseRepository getInstance() {
        if (instance == null) {
            instance = new ResponseRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(ComplaintResponse item) {
        if (item == null) return;
        responseList.add(item);

        if (item.getComplaintId() != null) {
            CustomArrayList<ComplaintResponse> list = complaintIndex.get(item.getComplaintId());
            if (list == null) {
                list = new CustomArrayList<>();
                complaintIndex.put(item.getComplaintId(), list);
            }
            list.add(item);
        }

        try {
            fileHandler.append(item);
        } catch (IOException e) {
            System.err.println("Error appending response: " + e.getMessage());
        }
    }

    public synchronized CustomArrayList<ComplaintResponse> findByComplaintId(String complaintId, boolean includeInternalNotes) {
        if (complaintId == null) return new CustomArrayList<>();
        CustomArrayList<ComplaintResponse> list = complaintIndex.get(complaintId);
        if (list == null) return new CustomArrayList<>();

        CustomArrayList<ComplaintResponse> filtered = new CustomArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ComplaintResponse r = list.get(i);
            if (includeInternalNotes || !r.isInternalNote()) {
                filtered.add(r);
            }
        }
        filtered.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));
        return filtered;
    }

    @Override
    public synchronized ComplaintResponse findById(String id) {
        for (int i = 0; i < responseList.size(); i++) {
            if (responseList.get(i).getResponseId().equals(id)) return responseList.get(i);
        }
        return null;
    }

    @Override
    public synchronized CustomArrayList<ComplaintResponse> findAll() {
        CustomArrayList<ComplaintResponse> copy = new CustomArrayList<>(responseList.size());
        copy.addAll(responseList);
        return copy;
    }

    @Override
    public synchronized boolean update(ComplaintResponse item) {
        return false;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        return false;
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id) != null;
    }

    @Override
    public synchronized int count() {
        return responseList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(responseList);
        } catch (IOException e) {
            System.err.println("Error syncing responses: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            responseList.clear();
            complaintIndex.clear();
            CustomArrayList<ComplaintResponse> loaded = fileHandler.loadAll();
            responseList.addAll(loaded);
            for (int i = 0; i < responseList.size(); i++) {
                ComplaintResponse r = responseList.get(i);
                if (r.getComplaintId() != null) {
                    CustomArrayList<ComplaintResponse> list = complaintIndex.get(r.getComplaintId());
                    if (list == null) {
                        list = new CustomArrayList<>();
                        complaintIndex.put(r.getComplaintId(), list);
                    }
                    list.add(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading responses: " + e.getMessage());
        }
    }
}
