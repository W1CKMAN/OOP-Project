package DAO;

import Models.Order;
import java.util.Date;
import java.util.List;

/**
 * DAO interface for Order operations.
 */
public interface OrderDAO extends GenericDAO<Order, Integer> {
    
    /**
     * Find orders by customer ID
     */
    List<Order> findByCustomerId(int customerId);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Find orders by date range
     */
    List<Order> findByDateRange(Date startDate, Date endDate);
    
    /**
     * Find orders by vehicle number
     */
    List<Order> findByVehicleNumber(String vehicleNumber);
    
    /**
     * Count orders by status
     */
    int countByStatus(String status);
    
    /**
     * Get total revenue for a date range
     */
    double getTotalRevenue(Date startDate, Date endDate);
    
    /**
     * Get monthly order statistics
     */
    List<Object[]> getMonthlyStats(int year);
    
    /**
     * Search orders by keyword
     */
    List<Order> search(String keyword);
}
