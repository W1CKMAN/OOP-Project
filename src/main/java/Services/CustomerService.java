package Services;

import DAO.CustomerDAO;
import DAO.Impl.CustomerDAOImpl;
import Models.Customer;
import Utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Customer operations.
 * Provides business logic and validation on top of the DAO layer.
 */
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAOImpl();
    }

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * Register a new customer with validation
     */
    public Customer registerCustomer(String name, String email, String phone, String address) 
            throws ValidationException {
        // Validate inputs
        validateCustomerData(name, email, phone, true);
        
        // Check for duplicate email
        if (customerDAO.emailExists(email)) {
            throw new ValidationException("Email address is already registered");
        }
        
        // Check for duplicate phone
        if (customerDAO.phoneExists(phone)) {
            throw new ValidationException("Phone number is already registered");
        }
        
        // Create and save customer
        Customer customer = new Customer(name, email, phone, address);
        customer = customerDAO.save(customer);
        
        logger.info("New customer registered: {} (ID: {})", name, customer.getCustomerId());
        return customer;
    }

    /**
     * Update existing customer with validation
     */
    public Customer updateCustomer(Customer customer) throws ValidationException {
        if (customer.getCustomerId() <= 0) {
            throw new ValidationException("Invalid customer ID");
        }
        
        // Check if customer exists
        if (!customerDAO.existsById(customer.getCustomerId())) {
            throw new ValidationException("Customer not found");
        }
        
        // Validate data
        validateCustomerData(customer.getName(), customer.getEmail(), customer.getPhone(), false);
        
        // Check for duplicate email (excluding current customer)
        Optional<Customer> existingByEmail = customerDAO.findByEmail(customer.getEmail());
        if (existingByEmail.isPresent() && existingByEmail.get().getCustomerId() != customer.getCustomerId()) {
            throw new ValidationException("Email address is already registered to another customer");
        }
        
        customer = customerDAO.update(customer);
        logger.info("Customer updated: {} (ID: {})", customer.getName(), customer.getCustomerId());
        return customer;
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(int customerId) {
        return customerDAO.findById(customerId);
    }

    /**
     * Get customer by email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerDAO.findByEmail(email);
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    /**
     * Get all active customers
     */
    public List<Customer> getActiveCustomers() {
        return customerDAO.findAllActive();
    }

    /**
     * Search customers by keyword
     */
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.search(keyword.trim());
    }

    /**
     * Deactivate a customer (soft delete)
     */
    public boolean deactivateCustomer(int customerId) {
        if (!customerDAO.existsById(customerId)) {
            return false;
        }
        boolean result = customerDAO.delete(customerId);
        if (result) {
            logger.info("Customer deactivated: ID {}", customerId);
        }
        return result;
    }

    /**
     * Add vehicle history entry
     */
    public void addVehicleHistory(int customerId, String vehicleInfo) throws ValidationException {
        if (!customerDAO.existsById(customerId)) {
            throw new ValidationException("Customer not found");
        }
        
        if (vehicleInfo == null || vehicleInfo.trim().isEmpty()) {
            throw new ValidationException("Vehicle information cannot be empty");
        }
        
        customerDAO.addVehicleHistory(customerId, vehicleInfo.trim());
        logger.info("Vehicle history added for customer ID: {}", customerId);
    }

    /**
     * Get total customer count
     */
    public long getCustomerCount() {
        return customerDAO.count();
    }

    /**
     * Validate customer data
     */
    private void validateCustomerData(String name, String email, String phone, boolean isNew) 
            throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Customer name is required");
        }
        
        if (name.trim().length() < 2) {
            throw new ValidationException("Customer name must be at least 2 characters");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is required");
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone number is required");
        }
        
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format");
        }
    }

    /**
     * Custom exception for validation errors
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
