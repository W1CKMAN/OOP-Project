package Models;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Inventory model for managing stock items.
 */
public class Inventory {
    private int productId;
    private String name;
    private String sku; // Stock Keeping Unit
    private String category;
    private int quantity;
    private String unit; // pcs, liters, kg, etc.
    private int minQuantity; // Minimum stock level for alerts
    private BigDecimal unitPriceBD;
    private double unitPrice;
    private double costPrice;
    private int supplierId;
    private String supplierName; // For display purposes
    private String location; // Warehouse location
    private String description;
    private Date lastRestocked;
    private boolean active;

    public Inventory() {
        this.active = true;
        this.minQuantity = 10;
        this.unit = "pcs";
        this.unitPriceBD = BigDecimal.ZERO;
    }

    public Inventory(String name, String sku, String category, int quantity, double unitPrice) {
        this();
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unitPriceBD = BigDecimal.valueOf(unitPrice);
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    // Alias for productId
    public int getItemId() {
        return productId;
    }
    
    public void setItemId(int itemId) {
        this.productId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // Alias for name
    public String getProductName() {
        return name;
    }
    
    public void setProductName(String productName) {
        this.name = productName;
    }

    // Keep old method for backward compatibility
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }
    
    // Alias for minQuantity
    public int getReorderLevel() {
        return minQuantity;
    }
    
    public void setReorderLevel(int reorderLevel) {
        this.minQuantity = reorderLevel;
    }

    public double getUnitPrice() {
        return unitPriceBD != null ? unitPriceBD.doubleValue() : unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.unitPriceBD = BigDecimal.valueOf(unitPrice);
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPriceBD = unitPrice;
        this.unitPrice = unitPrice != null ? unitPrice.doubleValue() : 0;
    }

    // Keep old method for backward compatibility
    public double getPrice() {
        return getUnitPrice();
    }

    public void setPrice(double price) {
        this.setUnitPrice(price);
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(Date lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if stock is low (below minimum)
     */
    public boolean isLowStock() {
        return quantity <= minQuantity;
    }

    /**
     * Check if out of stock
     */
    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    /**
     * Calculate total value of stock
     */
    public double getTotalValue() {
        return quantity * getUnitPrice();
    }

    /**
     * Calculate profit margin
     */
    public double getProfitMargin() {
        if (costPrice > 0) {
            return ((getUnitPrice() - costPrice) / costPrice) * 100;
        }
        return 0;
    }

    @Override
    public String toString() {
        return name + " (SKU: " + sku + ") - Qty: " + quantity;
    }
}
