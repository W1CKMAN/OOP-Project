package Models;

import java.util.Date;

/**
 * Supplier model for managing supplier information.
 */
public class Supplier {
    private int supplierId;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String category; // Parts, Paint, Tools, etc.
    private String status; // Active, Inactive, Blacklisted
    private boolean active;
    private Date createdAt;
    private String notes;

    public Supplier() {
        this.active = true;
        this.status = "Active";
        this.createdAt = new Date();
    }

    public Supplier(String companyName, String contactPerson, String email, String phone, String address, String category) {
        this();
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.category = category;
    }

    // Getters and Setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    // Alias for companyName
    public String getName() {
        return companyName;
    }
    
    public void setName(String name) {
        this.companyName = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.status = active ? "Active" : "Inactive";
    }
    
    public String getStatus() {
        return status != null ? status : (active ? "Active" : "Inactive");
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.active = "Active".equalsIgnoreCase(status);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return companyName + " (" + category + ")";
    }
}