package DAO.Impl;

import DAO.EmployeeDAO;
import DatabaseConnection.ConnectionPool;
import Models.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EmployeeDAO using JDBC and connection pooling.
 */
public class EmployeeDAOImpl implements EmployeeDAO {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDAOImpl.class);
    private final ConnectionPool connectionPool;

    public EmployeeDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Employee save(Employee employee) {
        String sql = "INSERT INTO Employees (employee_name, contact_number, email, position, salary, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getContactNumber());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPosition());
            if (employee.getSalary() != null) {
                stmt.setDouble(5, employee.getSalary());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }
            stmt.setString(6, employee.getStatus() != null ? employee.getStatus() : "Active");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Employee saved successfully with ID: {}", employee.getEmployeeId());
            return employee;
        } catch (SQLException e) {
            logger.error("Error saving employee", e);
            throw new RuntimeException("Failed to save employee", e);
        }
    }

    @Override
    public Employee update(Employee employee) {
        String sql = "UPDATE Employees SET employee_name=?, contact_number=?, email=?, position=?, salary=?, status=? WHERE employee_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getContactNumber());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPosition());
            if (employee.getSalary() != null) {
                stmt.setDouble(5, employee.getSalary());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }
            stmt.setString(6, employee.getStatus() != null ? employee.getStatus() : "Active");
            stmt.setInt(7, employee.getEmployeeId());
            
            stmt.executeUpdate();
            logger.info("Employee updated successfully: {}", employee.getEmployeeId());
            return employee;
        } catch (SQLException e) {
            logger.error("Error updating employee", e);
            throw new RuntimeException("Failed to update employee", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Employees WHERE employee_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Employee deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting employee", e);
            throw new RuntimeException("Failed to delete employee", e);
        }
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        String sql = "SELECT * FROM Employees WHERE employee_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employees ORDER BY employee_name";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all employees", e);
        }
        return employees;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Employees";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting employees", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Employees WHERE employee_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking employee existence", e);
        }
        return false;
    }

    @Override
    public List<Employee> findByPosition(String position) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employees WHERE position=? ORDER BY employee_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, position);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by position", e);
        }
        return employees;
    }

    @Override
    public Employee findByEmail(String email) {
        String sql = "SELECT * FROM Employees WHERE email=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by email", e);
        }
        return null;
    }

    @Override
    public List<Integer> getAllEmployeeIds() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT employee_id FROM Employees ORDER BY employee_id";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ids.add(rs.getInt("employee_id"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching employee IDs", e);
        }
        return ids;
    }

    @Override
    public String getEmailById(int employeeId) {
        String sql = "SELECT email FROM Employees WHERE employee_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee email", e);
        }
        return null;
    }

    @Override
    public List<Employee> search(String keyword) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employees WHERE employee_name LIKE ? OR email LIKE ? OR position LIKE ? ORDER BY employee_name";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching employees", e);
        }
        return employees;
    }

    @Override
    public List<Employee> getAvailableEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.* FROM Employees e " +
                     "LEFT JOIN Jobs j ON e.employee_id = j.employee_id AND j.status != 'Completed' " +
                     "GROUP BY e.employee_id " +
                     "HAVING COUNT(j.job_id) < 3 " +
                     "ORDER BY COUNT(j.job_id) ASC";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching available employees", e);
        }
        return employees;
    }

    @Override
    public int getEmployeeWorkload(int employeeId) {
        String sql = "SELECT COUNT(*) FROM Jobs WHERE employee_id=? AND status != 'Completed'";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting employee workload", e);
        }
        return 0;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setEmployeeName(rs.getString("employee_name"));
        employee.setContactNumber(rs.getString("contact_number"));
        employee.setEmail(rs.getString("email"));
        employee.setPosition(rs.getString("position"));
        
        // Handle nullable salary field
        double salary = rs.getDouble("salary");
        if (!rs.wasNull()) {
            employee.setSalary(salary);
        }
        
        // Handle status field
        String status = rs.getString("status");
        employee.setStatus(status != null ? status : "Active");
        
        return employee;
    }
}
