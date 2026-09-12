package repository;

import filehandling.HistoryFileHandler;
import interfaces.Repository;
import model.ComplaintHistory;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class HistoryRepository implements Repository<ComplaintHistory, String> {

    private static HistoryRepository instance;
    private final HistoryFileHandler fileHandler;
    private final CustomHashTable<String, CustomArrayList<ComplaintHistory>> complaintIndex;
    private final CustomArrayList<ComplaintHistory> historyList;

    public HistoryRepository() {
        this.fileHandler = new HistoryFileHandler();
        this.complaintIndex = new CustomHashTable<>(128);
        this.historyList = new CustomArrayList<>(256);
        reloadFromFile();
    }

    public static synchronized HistoryRepository getInstance() {
        if (instance == null) {
            instance = new HistoryRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(ComplaintHistory item) {
        if (item == null) return;
        historyList.add(item);

        if (item.getComplaintId() != null) {
            CustomArrayList<ComplaintHistory> list = complaintIndex.get(item.getComplaintId());
            if (list == null) {
                list = new CustomArrayList<>();
                complaintIndex.put(item.getComplaintId(), list);
            }
            list.add(item);
        }

        try {
            fileHandler.append(item);
        } catch (IOException e) {
            System.err.println("Error appending history: " + e.getMessage());
        }
    }

    public synchronized CustomArrayList<ComplaintHistory> findByComplaintId(String complaintId) {
        if (complaintId == null) return new CustomArrayList<>();
        CustomArrayList<ComplaintHistory> list = complaintIndex.get(complaintId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<ComplaintHistory> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        // Sort chronologically ascending
        copy.sort((h1, h2) -> Long.compare(h1.getTimestamp(), h2.getTimestamp()));
        return copy;
    }

    @Override
    public synchronized ComplaintHistory findById(String id) {
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).getHistoryId().equals(id)) return historyList.get(i);
        }
        return null;
    }

    @Override
    public synchronized CustomArrayList<ComplaintHistory> findAll() {
        CustomArrayList<ComplaintHistory> copy = new CustomArrayList<>(historyList.size());
        copy.addAll(historyList);
        return copy;
    }

    @Override
    public synchronized boolean update(ComplaintHistory item) {
        return false; // Histories are append-only audit logs
    }

    @Override
    public synchronized boolean deleteById(String id) {
        return false; // Immutable audit log
    }

    @Override
    public synchronized boolean existsById(String id) {
        return findById(id) != null;
    }

    @Override
    public synchronized int count() {
        return historyList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(historyList);
        } catch (IOException e) {
            System.err.println("Error syncing history: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            historyList.clear();
            complaintIndex.clear();
            CustomArrayList<ComplaintHistory> loaded = fileHandler.loadAll();
            historyList.addAll(loaded);
            for (int i = 0; i < historyList.size(); i++) {
                ComplaintHistory h = historyList.get(i);
                if (h.getComplaintId() != null) {
                    CustomArrayList<ComplaintHistory> list = complaintIndex.get(h.getComplaintId());
                    if (list == null) {
                        list = new CustomArrayList<>();
                        complaintIndex.put(h.getComplaintId(), list);
                    }
                    list.add(h);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading history: " + e.getMessage());
        }
    }
}
