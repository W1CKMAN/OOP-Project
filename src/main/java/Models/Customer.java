package Models;

/**
 * Customer model for managing customer information.
 */
public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String vehicleHistory;
    private boolean active;

    public Customer() {
        this.active = true;
    }

    public Customer(String name, String email, String phone, String address) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public String getVehicleHistory() {
        return vehicleHistory;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setVehicleHistory(String vehicleHistory) {
        this.vehicleHistory = vehicleHistory;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
