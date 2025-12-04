package Services;

import DAO.UserDAO;
import DAO.Impl.UserDAOImpl;
import Models.User;
import Utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Authentication service for managing user sessions.
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static AuthService instance;
    private final UserDAO userDAO;
    private User currentUser;

    private AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Attempt to login with username and password
     */
    public boolean login(String username, String password) {
        Optional<User> userOpt = userDAO.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            logger.info("User logged in: {}", username);
            return true;
        }
        
        logger.warn("Login failed for: {}", username);
        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Get the currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Register a new user
     */
    public User register(String username, String password, String fullName, String email, User.Role role) {
        // Validate inputs
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (!PasswordUtil.isPasswordStrong(password)) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        
        // Create user
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);
        
        return userDAO.save(user);
    }

    /**
     * Change password for current user
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        // Verify old password
        if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
            return false;
        }
        
        // Validate new password
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("New password does not meet security requirements");
        }
        
        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean success = userDAO.changePassword(currentUser.getUserId(), newHash);
        
        if (success) {
            currentUser.setPasswordHash(newHash);
        }
        
        return success;
    }

    /**
     * Check if current user has permission
     */
    public boolean hasPermission(String permission) {
        if (currentUser == null) {
            return false;
        }
        
        switch (permission) {
            case "MANAGE_USERS":
                return currentUser.canManageUsers();
            case "VIEW_REPORTS":
                return currentUser.canViewReports();
            case "MANAGE_EMPLOYEES":
                return currentUser.canManageEmployees();
            case "MANAGE_ORDERS":
                return currentUser.canManageOrders();
            case "MANAGE_INVENTORY":
                return currentUser.canManageInventory();
            case "MANAGE_SUPPLIERS":
                return currentUser.canManageSuppliers();
            default:
                return false;
        }
    }

    /**
     * Create default admin user if no users exist
     */
    public void ensureDefaultAdminExists() {
        if (userDAO.count() == 0) {
            try {
                register("admin", "Admin@123", "System Administrator", "admin@carcare.com", User.Role.ADMIN);
                logger.info("Default admin user created");
            } catch (Exception e) {
                logger.error("Failed to create default admin", e);
            }
        }
    }
}
