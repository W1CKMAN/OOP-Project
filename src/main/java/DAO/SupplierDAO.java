package DAO;

import Models.Supplier;
import java.util.List;

/**
 * DAO interface for Supplier operations.
 */
public interface SupplierDAO extends GenericDAO<Supplier, Integer> {
    
    /**
     * Find suppliers by category
     */
    List<Supplier> findByCategory(String category);
    
    /**
     * Find suppliers by status
     */
    List<Supplier> findByStatus(String status);
    
    /**
     * Find supplier by email
     */
    Supplier findByEmail(String email);
    
    /**
     * Search suppliers by keyword
     */
    List<Supplier> search(String keyword);
    
    /**
     * Get active suppliers
     */
    List<Supplier> getActiveSuppliers();
    
    /**
     * Get all categories
     */
    List<String> getAllCategories();
}
