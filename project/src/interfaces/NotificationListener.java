package interfaces;

import model.Notification;

public interface NotificationListener {
    void onNotificationReceived(Notification notification);
}
