package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private static final String SALT = "SMART_COMPLAINT_TRACKER_DSA_2026";

    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(SALT.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(password.hashCode());
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        return hashPassword(plainPassword).equalsIgnoreCase(storedHash);
    }
}
