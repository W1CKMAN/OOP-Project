package DAO;

import Models.Employee;
import java.util.List;

/**
 * DAO interface for Employee operations.
 */
public interface EmployeeDAO extends GenericDAO<Employee, Integer> {
    
    /**
     * Find employees by position
     */
    List<Employee> findByPosition(String position);
    
    /**
     * Find employee by email
     */
    Employee findByEmail(String email);
    
    /**
     * Get all employee IDs
     */
    List<Integer> getAllEmployeeIds();
    
    /**
     * Get employee email by ID
     */
    String getEmailById(int employeeId);
    
    /**
     * Search employees by keyword
     */
    List<Employee> search(String keyword);
    
    /**
     * Get available employees (not assigned to active jobs)
     */
    List<Employee> getAvailableEmployees();
    
    /**
     * Get employee workload (number of active jobs)
     */
    int getEmployeeWorkload(int employeeId);
}
