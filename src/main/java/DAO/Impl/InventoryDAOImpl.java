package DAO.Impl;

import DAO.InventoryDAO;
import DatabaseConnection.ConnectionPool;
import Models.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InventoryDAO using JDBC and connection pooling.
 */
public class InventoryDAOImpl implements InventoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(InventoryDAOImpl.class);
    private final ConnectionPool connectionPool;

    public InventoryDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Inventory save(Inventory item) {
        String sql = "INSERT INTO Inventory (name, sku, category, quantity, unit, min_quantity, unit_price, cost_price, supplier_id, location, description, last_restocked, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getSku());
            stmt.setString(3, item.getCategory());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, item.getUnit());
            stmt.setInt(6, item.getMinQuantity());
            stmt.setDouble(7, item.getUnitPrice());
            stmt.setDouble(8, item.getCostPrice());
            if (item.getSupplierId() > 0) {
                stmt.setInt(9, item.getSupplierId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, item.getLocation());
            stmt.setString(11, item.getDescription());
            stmt.setTimestamp(12, item.getLastRestocked() != null ? new Timestamp(item.getLastRestocked().getTime()) : null);
            stmt.setBoolean(13, item.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setProductId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Inventory item saved successfully with ID: {}", item.getProductId());
            return item;
        } catch (SQLException e) {
            logger.error("Error saving inventory item", e);
            throw new RuntimeException("Failed to save inventory item", e);
        }
    }

    @Override
    public Inventory update(Inventory item) {
        String sql = "UPDATE Inventory SET name=?, sku=?, category=?, quantity=?, unit=?, min_quantity=?, unit_price=?, cost_price=?, supplier_id=?, location=?, description=?, active=? WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getSku());
            stmt.setString(3, item.getCategory());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, item.getUnit());
            stmt.setInt(6, item.getMinQuantity());
            stmt.setDouble(7, item.getUnitPrice());
            stmt.setDouble(8, item.getCostPrice());
            if (item.getSupplierId() > 0) {
                stmt.setInt(9, item.getSupplierId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            stmt.setString(10, item.getLocation());
            stmt.setString(11, item.getDescription());
            stmt.setBoolean(12, item.isActive());
            stmt.setInt(13, item.getProductId());
            
            stmt.executeUpdate();
            logger.info("Inventory item updated successfully: {}", item.getProductId());
            return item;
        } catch (SQLException e) {
            logger.error("Error updating inventory item", e);
            throw new RuntimeException("Failed to update inventory item", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Inventory WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Inventory item deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting inventory item", e);
            throw new RuntimeException("Failed to delete inventory item", e);
        }
    }

    @Override
    public Optional<Inventory> findById(Integer id) {
        String sql = "SELECT * FROM Inventory WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding inventory item by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Inventory> findAll() {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT i.*, s.company_name as supplier_name FROM Inventory i " +
                     "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id ORDER BY i.name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Inventory item = mapResultSetToInventory(rs);
                try {
                    item.setSupplierName(rs.getString("supplier_name"));
                } catch (SQLException ignored) {}
                items.add(item);
            }
        } catch (SQLException e) {
            logger.error("Error fetching all inventory items", e);
        }
        return items;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Inventory";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting inventory items", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Inventory WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking inventory item existence", e);
        }
        return false;
    }

    @Override
    public List<Inventory> findByCategory(String category) {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT * FROM Inventory WHERE category=? ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding inventory by category", e);
        }
        return items;
    }

    @Override
    public Inventory findBySku(String sku) {
        String sql = "SELECT * FROM Inventory WHERE sku=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInventory(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding inventory by SKU", e);
        }
        return null;
    }

    @Override
    public List<Inventory> findLowStockItems() {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT i.*, s.company_name as supplier_name FROM Inventory i " +
                     "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                     "WHERE i.quantity <= i.min_quantity AND i.quantity > 0 ORDER BY i.quantity ASC";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Inventory item = mapResultSetToInventory(rs);
                try {
                    item.setSupplierName(rs.getString("supplier_name"));
                } catch (SQLException ignored) {}
                items.add(item);
            }
        } catch (SQLException e) {
            logger.error("Error fetching low stock items", e);
        }
        return items;
    }

    @Override
    public List<Inventory> findOutOfStockItems() {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT * FROM Inventory WHERE quantity <= 0 ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapResultSetToInventory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching out of stock items", e);
        }
        return items;
    }

    @Override
    public List<Inventory> findBySupplierId(int supplierId) {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT * FROM Inventory WHERE supplier_id=? ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding inventory by supplier", e);
        }
        return items;
    }

    @Override
    public boolean updateQuantity(int productId, int quantity) {
        String sql = "UPDATE Inventory SET quantity=? WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            
            int affected = stmt.executeUpdate();
            logger.info("Inventory quantity updated for product {}: new quantity = {}", productId, quantity);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error updating inventory quantity", e);
            return false;
        }
    }

    @Override
    public boolean addStock(int productId, int quantityToAdd) {
        String sql = "UPDATE Inventory SET quantity = quantity + ?, last_restocked = ? WHERE product_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityToAdd);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, productId);
            
            int affected = stmt.executeUpdate();
            logger.info("Stock added for product {}: +{}", productId, quantityToAdd);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error adding stock", e);
            return false;
        }
    }

    @Override
    public boolean reduceStock(int productId, int quantityToReduce) {
        String sql = "UPDATE Inventory SET quantity = quantity - ? WHERE product_id=? AND quantity >= ?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityToReduce);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantityToReduce);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                logger.info("Stock reduced for product {}: -{}", productId, quantityToReduce);
                return true;
            } else {
                logger.warn("Insufficient stock for product {}", productId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error reducing stock", e);
            return false;
        }
    }

    @Override
    public List<Inventory> search(String keyword) {
        List<Inventory> items = new ArrayList<>();
        String sql = "SELECT * FROM Inventory WHERE name LIKE ? OR sku LIKE ? OR category LIKE ? ORDER BY name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching inventory", e);
        }
        return items;
    }

    @Override
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM Inventory WHERE category IS NOT NULL ORDER BY category";
        
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

    @Override
    public double getTotalInventoryValue() {
        String sql = "SELECT SUM(quantity * unit_price) as total_value FROM Inventory WHERE active=true";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
        } catch (SQLException e) {
            logger.error("Error calculating total inventory value", e);
        }
        return 0.0;
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory item = new Inventory();
        item.setProductId(rs.getInt("product_id"));
        item.setName(rs.getString("name"));
        item.setSku(rs.getString("sku"));
        item.setCategory(rs.getString("category"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnit(rs.getString("unit"));
        item.setMinQuantity(rs.getInt("min_quantity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setCostPrice(rs.getDouble("cost_price"));
        item.setSupplierId(rs.getInt("supplier_id"));
        item.setLocation(rs.getString("location"));
        item.setDescription(rs.getString("description"));
        Timestamp lastRestocked = rs.getTimestamp("last_restocked");
        if (lastRestocked != null) {
            item.setLastRestocked(lastRestocked);
        }
        item.setActive(rs.getBoolean("active"));
        return item;
    }
}
