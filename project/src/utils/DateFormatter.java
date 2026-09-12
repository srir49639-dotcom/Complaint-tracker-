package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static synchronized String formatDateTime(long timestamp) {
        if (timestamp <= 0) return "N/A";
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static synchronized String formatDateOnly(long timestamp) {
        if (timestamp <= 0) return "N/A";
        return DATE_ONLY_FORMAT.format(new Date(timestamp));
    }

    public static String formatDurationHours(double hours) {
        if (hours < 0) {
            return String.format("%.1f hrs overdue", Math.abs(hours));
        } else if (hours < 1.0) {
            return String.format("%.0f mins remaining", hours * 60);
        } else {
            return String.format("%.1f hrs remaining", hours);
        }
    }
}
