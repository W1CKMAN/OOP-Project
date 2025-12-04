package DAO;

import Models.User;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for User operations.
 */
public interface UserDAO extends GenericDAO<User, Integer> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Update last login timestamp
     */
    void updateLastLogin(int userId);
    
    /**
     * Authenticate user
     */
    Optional<User> authenticate(String username, String password);
    
    /**
     * Check if username exists
     */
    boolean usernameExists(String username);
    
    /**
     * Check if email exists
     */
    boolean emailExists(String email);
    
    /**
     * Change user password
     */
    boolean changePassword(int userId, String newPasswordHash);
    
    /**
     * Deactivate user
     */
    boolean deactivateUser(int userId);
    
    /**
     * Activate user
     */
    boolean activateUser(int userId);
}
