package DAO.Impl;

import DAO.CustomerDAO;
import DatabaseConnection.ConnectionPool;
import Models.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerDAO using JDBC and connection pooling.
 */
public class CustomerDAOImpl implements CustomerDAO {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDAOImpl.class);
    private final ConnectionPool connectionPool;

    public CustomerDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO Customers (name, email, phone, address, vehicle_history, active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getVehicleHistory());
            stmt.setBoolean(6, customer.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Customer saved successfully with ID: {}", customer.getCustomerId());
            return customer;
        } catch (SQLException e) {
            logger.error("Error saving customer", e);
            throw new RuntimeException("Failed to save customer", e);
        }
    }

    @Override
    public Customer update(Customer customer) {
        String sql = "UPDATE Customers SET name=?, email=?, phone=?, address=?, vehicle_history=?, active=? WHERE customer_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getVehicleHistory());
            stmt.setBoolean(6, customer.isActive());
            stmt.setInt(7, customer.getCustomerId());
            
            stmt.executeUpdate();
            logger.info("Customer updated successfully: {}", customer.getCustomerId());
            return customer;
        } catch (SQLException e) {
            logger.error("Error updating customer", e);
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        // Soft delete - set active to false
        String sql = "UPDATE Customers SET active=false WHERE customer_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Customer soft-deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting customer", e);
            throw new RuntimeException("Failed to delete customer", e);
        }
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        String sql = "SELECT * FROM Customers WHERE customer_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all customers", e);
        }
        return customers;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Customers WHERE active=true";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting customers", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Customers WHERE customer_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking customer existence", e);
        }
        return false;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM Customers WHERE email=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by email", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        String sql = "SELECT * FROM Customers WHERE phone=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by phone", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> search(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers WHERE (name LIKE ? OR email LIKE ? OR phone LIKE ?) AND active=true ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching customers", e);
        }
        return customers;
    }

    @Override
    public List<Customer> findAllActive() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers WHERE active=true ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching active customers", e);
        }
        return customers;
    }

    @Override
    public Optional<Customer> findByIdWithHistory(int customerId) {
        return findById(customerId);
    }

    @Override
    public void addVehicleHistory(int customerId, String vehicleInfo) {
        String sql = "UPDATE Customers SET vehicle_history = CONCAT(IFNULL(vehicle_history, ''), ?) WHERE customer_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "\n" + vehicleInfo);
            stmt.setInt(2, customerId);
            
            stmt.executeUpdate();
            logger.info("Vehicle history added for customer: {}", customerId);
        } catch (SQLException e) {
            logger.error("Error adding vehicle history", e);
            throw new RuntimeException("Failed to add vehicle history", e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Customers WHERE email=?";
        
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
    public boolean phoneExists(String phone) {
        String sql = "SELECT 1 FROM Customers WHERE phone=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking phone existence", e);
        }
        return false;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setVehicleHistory(rs.getString("vehicle_history"));
        customer.setActive(rs.getBoolean("active"));
        return customer;
    }
}
