package repository;

import filehandling.FeedbackFileHandler;
import interfaces.Repository;
import model.Feedback;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class FeedbackRepository implements Repository<Feedback, String> {

    private static FeedbackRepository instance;
    private final FeedbackFileHandler fileHandler;
    private final CustomHashTable<String, Feedback> complaintIndex;
    private final CustomHashTable<String, CustomArrayList<Feedback>> customerIndex;
    private final CustomArrayList<Feedback> feedbackList;

    public FeedbackRepository() {
        this.fileHandler = new FeedbackFileHandler();
        this.complaintIndex = new CustomHashTable<>(64);
        this.customerIndex = new CustomHashTable<>(64);
        this.feedbackList = new CustomArrayList<>(128);
        reloadFromFile();
    }

    public static synchronized FeedbackRepository getInstance() {
        if (instance == null) {
            instance = new FeedbackRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Feedback item) {
        if (item == null) return;
        feedbackList.add(item);
        indexItem(item);

        try {
            fileHandler.append(item);
        } catch (IOException e) {
            System.err.println("Error appending feedback: " + e.getMessage());
        }
    }

    public synchronized Feedback findByComplaintId(String complaintId) {
        return (complaintId != null) ? complaintIndex.get(complaintId) : null;
    }

    public synchronized CustomArrayList<Feedback> findByCustomerId(String customerId) {
        if (customerId == null) return new CustomArrayList<>();
        CustomArrayList<Feedback> list = customerIndex.get(customerId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Feedback> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        return copy;
    }

    @Override
    public synchronized Feedback findById(String id) {
        for (int i = 0; i < feedbackList.size(); i++) {
            if (feedbackList.get(i).getFeedbackId().equals(id)) return feedbackList.get(i);
        }
        return null;
    }

    @Override
    public synchronized CustomArrayList<Feedback> findAll() {
        CustomArrayList<Feedback> copy = new CustomArrayList<>(feedbackList.size());
        copy.addAll(feedbackList);
        return copy;
    }

    @Override
    public synchronized boolean update(Feedback item) {
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
        return feedbackList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(feedbackList);
        } catch (IOException e) {
            System.err.println("Error syncing feedback: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            feedbackList.clear();
            CustomArrayList<Feedback> loaded = fileHandler.loadAll();
            feedbackList.addAll(loaded);
            rebuildIndices();
        } catch (IOException e) {
            System.err.println("Error loading feedback: " + e.getMessage());
        }
    }

    private void rebuildIndices() {
        complaintIndex.clear();
        customerIndex.clear();
        for (int i = 0; i < feedbackList.size(); i++) {
            indexItem(feedbackList.get(i));
        }
    }

    private void indexItem(Feedback f) {
        if (f.getComplaintId() != null) {
            complaintIndex.put(f.getComplaintId(), f);
        }
        if (f.getCustomerId() != null) {
            CustomArrayList<Feedback> list = customerIndex.get(f.getCustomerId());
            if (list == null) {
                list = new CustomArrayList<>();
                customerIndex.put(f.getCustomerId(), list);
            }
            list.add(f);
        }
    }
}
