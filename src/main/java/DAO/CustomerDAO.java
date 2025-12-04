package DAO;

import Models.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Customer entity.
 */
public interface CustomerDAO extends GenericDAO<Customer, Integer> {
    
    /**
     * Find customer by email address
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Find customer by phone number
     */
    Optional<Customer> findByPhone(String phone);
    
    /**
     * Search customers by name, email, or phone
     */
    List<Customer> search(String keyword);
    
    /**
     * Find all active customers
     */
    List<Customer> findAllActive();
    
    /**
     * Get customer with their vehicle history
     */
    Optional<Customer> findByIdWithHistory(int customerId);
    
    /**
     * Add vehicle history entry for a customer
     */
    void addVehicleHistory(int customerId, String vehicleInfo);
    
    /**
     * Check if email is already registered
     */
    boolean emailExists(String email);
    
    /**
     * Check if phone is already registered
     */
    boolean phoneExists(String phone);
}
