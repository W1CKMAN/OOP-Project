package DAO.Impl;

import DAO.OrderDAO;
import DatabaseConnection.ConnectionPool;
import Models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderDAO using JDBC and connection pooling.
 */
public class OrderDAOImpl implements OrderDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);
    private final ConnectionPool connectionPool;

    public OrderDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO Orders (customer_id, order_date, vehicle_model, vehicle_number, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, order.getCustomerId());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setString(3, order.getVehicleModel());
            stmt.setString(4, order.getVehicleNumber());
            stmt.setString(5, order.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Order saved successfully with ID: {}", order.getOrderId());
            return order;
        } catch (SQLException e) {
            logger.error("Error saving order", e);
            throw new RuntimeException("Failed to save order", e);
        }
    }

    @Override
    public Order update(Order order) {
        String sql = "UPDATE Orders SET customer_id=?, vehicle_model=?, vehicle_number=?, status=? WHERE order_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, order.getCustomerId());
            stmt.setString(2, order.getVehicleModel());
            stmt.setString(3, order.getVehicleNumber());
            stmt.setString(4, order.getStatus());
            stmt.setInt(5, order.getOrderId());
            
            stmt.executeUpdate();
            logger.info("Order updated successfully: {}", order.getOrderId());
            return order;
        } catch (SQLException e) {
            logger.error("Error updating order", e);
            throw new RuntimeException("Failed to update order", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Orders WHERE order_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Order deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting order", e);
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        String sql = "SELECT * FROM Orders WHERE order_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding order by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all orders", e);
        }
        return orders;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Orders";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting orders", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Orders WHERE order_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking order existence", e);
        }
        return false;
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE customer_id=? ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by customer ID", e);
        }
        return orders;
    }

    @Override
    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE status=? ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by status", e);
        }
        return orders;
    }

    @Override
    public List<Order> findByDateRange(Date startDate, Date endDate) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by date range", e);
        }
        return orders;
    }

    @Override
    public List<Order> findByVehicleNumber(String vehicleNumber) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE vehicle_number LIKE ? ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + vehicleNumber + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding orders by vehicle number", e);
        }
        return orders;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE status=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting orders by status", e);
        }
        return 0;
    }

    @Override
    public double getTotalRevenue(Date startDate, Date endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM Orders WHERE order_date BETWEEN ? AND ? AND status='Completed'";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating total revenue", e);
        }
        return 0.0;
    }

    @Override
    public List<Object[]> getMonthlyStats(int year) {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT MONTH(order_date) as month, COUNT(*) as count, " +
                     "SUM(CASE WHEN status='Completed' THEN 1 ELSE 0 END) as completed " +
                     "FROM Orders WHERE YEAR(order_date)=? GROUP BY MONTH(order_date) ORDER BY month";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new Object[]{
                        rs.getInt("month"),
                        rs.getInt("count"),
                        rs.getInt("completed")
                    });
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting monthly stats", e);
        }
        return stats;
    }

    @Override
    public List<Order> search(String keyword) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE vehicle_model LIKE ? OR vehicle_number LIKE ? OR status LIKE ? ORDER BY order_date DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching orders", e);
        }
        return orders;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setOrderDate(rs.getDate("order_date"));
        order.setVehicleModel(rs.getString("vehicle_model"));
        order.setVehicleNumber(rs.getString("vehicle_number"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}
