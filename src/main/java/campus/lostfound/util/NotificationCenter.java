package campus.lostfound.util;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenter {
    private static final List<String> notifications = new ArrayList<>();

    // Synchronized method to ensure thread safety when background threads post alerts
    public static synchronized void addNotification(String message) {
        if (message != null && !message.trim().isEmpty()) {
            notifications.add(message.trim());
        }
    }

    // Synchronized method to safely retrieve and clear pending notifications
    public static synchronized List<String> retrievePendingNotifications() {
        List<String> copy = new ArrayList<>(notifications);
        notifications.clear();
        return copy;
    }

    public static synchronized boolean hasNotifications() {
        return !notifications.isEmpty();
    }
}
