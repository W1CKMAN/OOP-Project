package Utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class ValidationUtil {
    
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile(
        "^[A-Z]{2,3}[-\\s]?[0-9]{4}$"
    );

    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate vehicle number format (Sri Lankan format)
     */
    public static boolean isValidVehicleNumber(String vehicleNumber) {
        if (isEmpty(vehicleNumber)) return false;
        String clean = vehicleNumber.toUpperCase().replaceAll("\\s", "");
        return VEHICLE_NUMBER_PATTERN.matcher(clean).matches();
    }

    /**
     * Validate integer
     */
    public static boolean isValidInteger(String str) {
        if (isEmpty(str)) return false;
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive integer
     */
    public static boolean isValidPositiveInteger(String str) {
        if (isEmpty(str)) return false;
        try {
            return Integer.parseInt(str.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate double
     */
    public static boolean isValidDouble(String str) {
        if (isEmpty(str)) return false;
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive double
     */
    public static boolean isValidPositiveDouble(String str) {
        if (isEmpty(str)) return false;
        try {
            return Double.parseDouble(str.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return minLength == 0;
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Sanitize string input
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"']", "");
    }

    /**
     * Parse integer safely
     */
    public static int parseIntSafe(String str, int defaultValue) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Parse double safely
     */
    public static double parseDoubleSafe(String str, double defaultValue) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Format currency
     */
    public static String formatCurrency(double amount) {
        return String.format("Rs. %.2f", amount);
    }

    /**
     * Format phone number
     */
    public static String formatPhone(String phone) {
        if (isEmpty(phone)) return "";
        String clean = phone.replaceAll("[^0-9+]", "");
        if (clean.length() == 10 && !clean.startsWith("+")) {
            return clean.substring(0, 3) + "-" + clean.substring(3, 6) + "-" + clean.substring(6);
        }
        return clean;
    }
}
