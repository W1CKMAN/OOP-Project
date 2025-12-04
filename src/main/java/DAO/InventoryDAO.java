package DAO;

import Models.Inventory;
import java.util.List;

/**
 * DAO interface for Inventory operations.
 */
public interface InventoryDAO extends GenericDAO<Inventory, Integer> {
    
    /**
     * Find inventory items by category
     */
    List<Inventory> findByCategory(String category);
    
    /**
     * Find inventory item by SKU
     */
    Inventory findBySku(String sku);
    
    /**
     * Find items with low stock
     */
    List<Inventory> findLowStockItems();
    
    /**
     * Find items with low stock (alias)
     */
    default List<Inventory> findLowStock() {
        return findLowStockItems();
    }
    
    /**
     * Find items out of stock
     */
    List<Inventory> findOutOfStockItems();
    
    /**
     * Find items by supplier
     */
    List<Inventory> findBySupplierId(int supplierId);
    
    /**
     * Update stock quantity
     */
    boolean updateQuantity(int productId, int quantity);
    
    /**
     * Add stock
     */
    boolean addStock(int productId, int quantityToAdd);
    
    /**
     * Reduce stock
     */
    boolean reduceStock(int productId, int quantityToReduce);
    
    /**
     * Search inventory by keyword
     */
    List<Inventory> search(String keyword);
    
    /**
     * Get all categories
     */
    List<String> getAllCategories();
    
    /**
     * Get total inventory value
     */
    double getTotalInventoryValue();
}
