package utils;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String clean = email.trim();
        return clean.contains("@") && clean.contains(".") && clean.indexOf("@") < clean.lastIndexOf(".");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String clean = phone.trim().replaceAll("[^0-9]", "");
        return clean.length() >= 7 && clean.length() <= 15;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
