package DAO.Impl;

import DAO.UserDAO;
import DatabaseConnection.ConnectionPool;
import Models.User;
import Utils.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserDAO using JDBC and connection pooling.
 */
public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private final ConnectionPool connectionPool;

    public UserDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO Users (username, password_hash, full_name, email, role, active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setTimestamp(7, new Timestamp(user.getCreatedAt().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("User saved successfully with ID: {}", user.getUserId());
            return user;
        } catch (SQLException e) {
            logger.error("Error saving user", e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET username=?, full_name=?, email=?, role=?, active=? WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setBoolean(5, user.isActive());
            stmt.setInt(6, user.getUserId());
            
            stmt.executeUpdate();
            logger.info("User updated successfully: {}", user.getUserId());
            return user;
        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Users WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("User deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user", e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM Users WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY full_name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all users", e);
        }
        return users;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Users";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting users", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Users WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking user existence", e);
        }
        return false;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role=? ORDER BY full_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding users by role", e);
        }
        return users;
    }

    @Override
    public void updateLastLogin(int userId) {
        String sql = "UPDATE Users SET last_login=? WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, userId);
            
            stmt.executeUpdate();
            logger.info("Last login updated for user: {}", userId);
        } catch (SQLException e) {
            logger.error("Error updating last login", e);
        }
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive() && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                updateLastLogin(user.getUserId());
                logger.info("User authenticated successfully: {}", username);
                return Optional.of(user);
            }
        }
        
        logger.warn("Authentication failed for user: {}", username);
        return Optional.empty();
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM Users WHERE username=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking username existence", e);
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE email=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence", e);
        }
        return false;
    }

    @Override
    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE Users SET password_hash=? WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            
            int affected = stmt.executeUpdate();
            logger.info("Password changed for user: {}", userId);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error changing password", e);
            return false;
        }
    }

    @Override
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE Users SET active=false WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int affected = stmt.executeUpdate();
            logger.info("User deactivated: {}", userId);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deactivating user", e);
            return false;
        }
    }

    @Override
    public boolean activateUser(int userId) {
        String sql = "UPDATE Users SET active=true WHERE user_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int affected = stmt.executeUpdate();
            logger.info("User activated: {}", userId);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error activating user", e);
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin);
        }
        return user;
    }
}
