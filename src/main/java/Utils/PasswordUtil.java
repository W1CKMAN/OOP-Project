package Utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 */
public class PasswordUtil {
    private static final int BCRYPT_COST = 12;

    /**
     * Hash a password using BCrypt
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }

    /**
     * Verify a password against a hash
     */
    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }

    /**
     * Check if password meets minimum requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Get password strength description
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Empty";
        }
        
        int score = 0;
        
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) score++;
        
        switch (score) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Fair";
            case 4:
                return "Strong";
            default:
                return "Very Strong";
        }
    }
}
