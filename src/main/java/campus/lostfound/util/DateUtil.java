package campus.lostfound.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getToday() {
        return LocalDate.now().format(FORMATTER);
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return false;
        try {
            LocalDate.parse(dateStr.trim(), FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static long daysBetween(String date1, String date2) {
        try {
            LocalDate d1 = LocalDate.parse(date1.trim(), FORMATTER);
            LocalDate d2 = LocalDate.parse(date2.trim(), FORMATTER);
            return Math.abs(ChronoUnit.DAYS.between(d1, d2));
        } catch (Exception e) {
            return 999; // Far away if parsing fails
        }
    }
}
