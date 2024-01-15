package Models;


import java.util.Date;

public class CustomerOrder {
    private int orderId;
    private int customerId;
    private Date orderDate;
    private String vehicleModel;
    private String status;

    public CustomerOrder() {
    }

    public CustomerOrder(int orderId, int customerId, Date orderDate, String vehicleModel, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.vehicleModel = vehicleModel;
        this.status = status;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}