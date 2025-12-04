package DAO.Impl;

import DAO.JobDAO;
import DatabaseConnection.ConnectionPool;
import Models.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of JobDAO using JDBC and connection pooling.
 */
public class JobDAOImpl implements JobDAO {
    private static final Logger logger = LoggerFactory.getLogger(JobDAOImpl.class);
    private final ConnectionPool connectionPool;

    public JobDAOImpl() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    @Override
    public Job save(Job job) {
        String sql = "INSERT INTO Jobs (order_id, employee_id, job_description, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, job.getOrderId());
            stmt.setInt(2, job.getEmployeeId());
            stmt.setString(3, job.getJobDescription());
            stmt.setString(4, job.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        job.setJobId(generatedKeys.getInt(1));
                    }
                }
            }
            logger.info("Job saved successfully with ID: {}", job.getJobId());
            return job;
        } catch (SQLException e) {
            logger.error("Error saving job", e);
            throw new RuntimeException("Failed to save job", e);
        }
    }

    @Override
    public Job update(Job job) {
        String sql = "UPDATE Jobs SET order_id=?, employee_id=?, job_description=?, status=? WHERE job_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, job.getOrderId());
            stmt.setInt(2, job.getEmployeeId());
            stmt.setString(3, job.getJobDescription());
            stmt.setString(4, job.getStatus());
            stmt.setInt(5, job.getJobId());
            
            stmt.executeUpdate();
            logger.info("Job updated successfully: {}", job.getJobId());
            return job;
        } catch (SQLException e) {
            logger.error("Error updating job", e);
            throw new RuntimeException("Failed to update job", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Jobs WHERE job_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            logger.info("Job deleted: {}", id);
            return affected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting job", e);
            throw new RuntimeException("Failed to delete job", e);
        }
    }

    @Override
    public Optional<Job> findById(Integer id) {
        String sql = "SELECT * FROM Jobs WHERE job_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding job by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Job> findAll() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs ORDER BY job_id DESC";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all jobs", e);
        }
        return jobs;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Jobs";
        
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting jobs", e);
        }
        return 0;
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM Jobs WHERE job_id=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking job existence", e);
        }
        return false;
    }

    @Override
    public List<Job> findByOrderId(int orderId) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs WHERE order_id=? ORDER BY job_id";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding jobs by order ID", e);
        }
        return jobs;
    }

    @Override
    public List<Job> findByEmployeeId(int employeeId) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs WHERE employee_id=? ORDER BY job_id DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding jobs by employee ID", e);
        }
        return jobs;
    }

    @Override
    public List<Job> findByStatus(String status) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs WHERE status=? ORDER BY job_id DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding jobs by status", e);
        }
        return jobs;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Jobs WHERE status=?";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting jobs by status", e);
        }
        return 0;
    }

    @Override
    public List<Job> getPendingJobsForEmployee(int employeeId) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs WHERE employee_id=? AND status IN ('Pending', 'In Progress') ORDER BY job_id";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding pending jobs for employee", e);
        }
        return jobs;
    }

    @Override
    public List<Job> search(String keyword) {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM Jobs WHERE job_description LIKE ? OR status LIKE ? ORDER BY job_id DESC";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching jobs", e);
        }
        return jobs;
    }

    @Override
    public List<Object[]> getMonthlyStats(int year) {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT MONTH(created_at) as month, COUNT(*) as total, " +
                     "SUM(CASE WHEN status='Completed' THEN 1 ELSE 0 END) as completed " +
                     "FROM Jobs WHERE YEAR(created_at)=? GROUP BY MONTH(created_at) ORDER BY month";
        
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new Object[]{
                        rs.getInt("month"),
                        rs.getInt("total"),
                        rs.getInt("completed")
                    });
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting monthly job stats", e);
        }
        return stats;
    }

    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setJobId(rs.getInt("job_id"));
        job.setOrderId(rs.getInt("order_id"));
        job.setEmployeeId(rs.getInt("employee_id"));
        job.setJobDescription(rs.getString("job_description"));
        job.setStatus(rs.getString("status"));
        return job;
    }
}
