package repository;

import filehandling.NotificationFileHandler;
import interfaces.Repository;
import model.Notification;
import datastructures.CustomArrayList;
import datastructures.CustomHashTable;

import java.io.IOException;

public class NotificationRepository implements Repository<Notification, String> {

    private static NotificationRepository instance;
    private final NotificationFileHandler fileHandler;
    private final CustomHashTable<String, CustomArrayList<Notification>> userIndex;
    private final CustomArrayList<Notification> notificationList;

    public NotificationRepository() {
        this.fileHandler = new NotificationFileHandler();
        this.userIndex = new CustomHashTable<>(64);
        this.notificationList = new CustomArrayList<>(128);
        reloadFromFile();
    }

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    @Override
    public synchronized void add(Notification item) {
        if (item == null) return;
        notificationList.add(item);

        if (item.getRecipientUserId() != null) {
            CustomArrayList<Notification> list = userIndex.get(item.getRecipientUserId());
            if (list == null) {
                list = new CustomArrayList<>();
                userIndex.put(item.getRecipientUserId(), list);
            }
            list.add(item);
        }

        try {
            fileHandler.append(item);
        } catch (IOException e) {
            System.err.println("Error appending notification: " + e.getMessage());
        }
    }

    public synchronized CustomArrayList<Notification> findByUserId(String userId) {
        if (userId == null) return new CustomArrayList<>();
        CustomArrayList<Notification> list = userIndex.get(userId);
        if (list == null) return new CustomArrayList<>();
        CustomArrayList<Notification> copy = new CustomArrayList<>(list.size());
        copy.addAll(list);
        copy.sort((n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp())); // newest first
        return copy;
    }

    public synchronized int getUnreadCount(String userId) {
        if (userId == null) return 0;
        CustomArrayList<Notification> list = userIndex.get(userId);
        if (list == null) return 0;
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isRead()) count++;
        }
        return count;
    }

    public synchronized void markAllAsRead(String userId) {
        if (userId == null) return;
        CustomArrayList<Notification> list = userIndex.get(userId);
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRead(true);
        }
        syncToFile();
    }

    @Override
    public synchronized Notification findById(String id) {
        for (int i = 0; i < notificationList.size(); i++) {
            if (notificationList.get(i).getNotificationId().equals(id)) return notificationList.get(i);
        }
        return null;
    }

    @Override
    public synchronized CustomArrayList<Notification> findAll() {
        CustomArrayList<Notification> copy = new CustomArrayList<>(notificationList.size());
        copy.addAll(notificationList);
        return copy;
    }

    @Override
    public synchronized boolean update(Notification item) {
        if (item == null) return false;
        Notification existing = findById(item.getNotificationId());
        if (existing == null) return false;
        existing.setRead(item.isRead());
        syncToFile();
        return true;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        Notification existing = findById(id);
        if (existing == null) return false;
        notificationList.remove(existing);
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
        return notificationList.size();
    }

    @Override
    public synchronized void syncToFile() {
        try {
            fileHandler.saveAll(notificationList);
        } catch (IOException e) {
            System.err.println("Error syncing notifications: " + e.getMessage());
        }
    }

    @Override
    public synchronized void reloadFromFile() {
        try {
            notificationList.clear();
            userIndex.clear();
            CustomArrayList<Notification> loaded = fileHandler.loadAll();
            notificationList.addAll(loaded);
            for (int i = 0; i < notificationList.size(); i++) {
                Notification n = notificationList.get(i);
                if (n.getRecipientUserId() != null) {
                    CustomArrayList<Notification> list = userIndex.get(n.getRecipientUserId());
                    if (list == null) {
                        list = new CustomArrayList<>();
                        userIndex.put(n.getRecipientUserId(), list);
                    }
                    list.add(n);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        }
    }

    private void rebuildIndices() {
        userIndex.clear();
        for (int i = 0; i < notificationList.size(); i++) {
            Notification n = notificationList.get(i);
            if (n.getRecipientUserId() != null) {
                CustomArrayList<Notification> list = userIndex.get(n.getRecipientUserId());
                if (list == null) {
                    list = new CustomArrayList<>();
                    userIndex.put(n.getRecipientUserId(), list);
                }
                list.add(n);
            }
        }
    }
}
