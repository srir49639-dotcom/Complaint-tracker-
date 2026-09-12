package notifications;

import interfaces.NotificationListener;
import model.Notification;
import repository.NotificationRepository;
import utils.IdGenerator;
import datastructures.CustomArrayList;

public class NotificationManager {

    private static NotificationManager instance;
    private final NotificationRepository repository;
    private final CustomArrayList<NotificationListener> listeners;

    public NotificationManager() {
        this.repository = NotificationRepository.getInstance();
        this.listeners = new CustomArrayList<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void addListener(NotificationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    public void notify(String recipientUserId, String recipientRole, String complaintId, String title, String message) {
        String nid = IdGenerator.generateNotificationId();
        Notification n = new Notification(nid, recipientUserId, recipientRole, complaintId, title, message, System.currentTimeMillis(), false);

        repository.add(n);

        // Dispatch to registered observers
        for (int i = 0; i < listeners.size(); i++) {
            try {
                listeners.get(i).onNotificationReceived(n);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }
}
