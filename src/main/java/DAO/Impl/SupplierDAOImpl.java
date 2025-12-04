package DAO.Impl;

import DAO.SupplierDAO;
import DatabaseConnection.ConnectionPool;
import Models.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of SupplierDAO using JDBC and connection pooling.
 */
public class SupplierDAOImpl implements SupplierDAO {
    private static final Logger logger = LoggerFactory.getLogger(SupplierDAOImpl.class);
    private final ConnectionPool connectionPool;

    public SupplierDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Supplier save(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (company_name, contact_person, email, phone, address, category, active, created_at, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, supplier.getCompanyName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCategory());
            stmt.setBoolean(7, supplier.isActive());
            stmt.setTimestamp(8, new Timestamp(supplier.getCreatedAt().getTime()));
            stmt.setString(9, supplier.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Supplier saved successfully with ID: {}", supplier.getSupplierId());
            return supplier;
        } catch (SQLException e) {
            logger.error("Error saving supplier", e);
            throw new RuntimeException("Failed to save supplier", e);
        }
    }

    @Override
    public Supplier update(Supplier supplier) {
        String sql = "UPDATE Suppliers SET company_name=?, contact_person=?, email=?, phone=?, address=?, category=?, active=?, notes=? WHERE supplier_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplier.getCompanyName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCategory());
            stmt.setBoolean(7, supplier.isActive());
            stmt.setString(8, supplier.getNotes());
            stmt.setInt(9, supplier.getSupplierId());
            
            stmt.executeUpdate();
            logger.info("Supplier updated successfully: {}", supplier.getSupplierId());
            return supplier;
        } catch (SQLException e) {
            logger.error("Error updating supplier", e);
            throw new RuntimeException("Failed to update supplier", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Suppliers WHERE supplier_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Supplier deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting supplier", e);
            throw new RuntimeException("Failed to delete supplier", e);
        }
    }

    @Override
    public Optional<Supplier> findById(Integer id) {
        String sql = "SELECT * FROM Suppliers WHERE supplier_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding supplier by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers ORDER BY company_name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all suppliers", e);
        }
        return suppliers;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Suppliers";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting suppliers", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Suppliers WHERE supplier_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking supplier existence", e);
        }
        return false;
    }

    @Override
    public List<Supplier> findByCategory(String category) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE category=? ORDER BY company_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding suppliers by category", e);
        }
        return suppliers;
    }

    @Override
    public List<Supplier> findByStatus(String status) {
        List<Supplier> suppliers = new ArrayList<>();
        boolean isActive = "Active".equalsIgnoreCase(status);
        String sql = "SELECT * FROM Suppliers WHERE active=? ORDER BY company_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isActive);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding suppliers by status", e);
        }
        return suppliers;
    }

    @Override
    public Supplier findByEmail(String email) {
        String sql = "SELECT * FROM Suppliers WHERE email=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding supplier by email", e);
        }
        return null;
    }

    @Override
    public List<Supplier> search(String keyword) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE company_name LIKE ? OR contact_person LIKE ? OR email LIKE ? OR category LIKE ? ORDER BY company_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching suppliers", e);
        }
        return suppliers;
    }

    @Override
    public List<Supplier> getActiveSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE active=true ORDER BY company_name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching active suppliers", e);
        }
        return suppliers;
    }

    @Override
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM Suppliers WHERE category IS NOT NULL ORDER BY category";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching categories", e);
        }
        return categories;
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setCompanyName(rs.getString("company_name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCategory(rs.getString("category"));
        supplier.setActive(rs.getBoolean("active"));
        supplier.setCreatedAt(rs.getTimestamp("created_at"));
        supplier.setNotes(rs.getString("notes"));
        return supplier;
    }
}
