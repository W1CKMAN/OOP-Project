package DAO;

import Models.Job;
import java.util.List;

/**
 * DAO interface for Job operations.
 */
public interface JobDAO extends GenericDAO<Job, Integer> {
    
    /**
     * Find jobs by order ID
     */
    List<Job> findByOrderId(int orderId);
    
    /**
     * Find jobs by employee ID
     */
    List<Job> findByEmployeeId(int employeeId);
    
    /**
     * Find jobs by status
     */
    List<Job> findByStatus(String status);
    
    /**
     * Count jobs by status
     */
    int countByStatus(String status);
    
    /**
     * Get jobs for an employee with pending status
     */
    List<Job> getPendingJobsForEmployee(int employeeId);
    
    /**
     * Search jobs by keyword
     */
    List<Job> search(String keyword);
    
    /**
     * Get monthly job statistics
     */
    List<Object[]> getMonthlyStats(int year);
}
