package Controllers;

import Models.Order;
import DatabaseConnection.DatabaseLayer;

import java.util.List;

@SuppressWarnings("deprecation")
public class OrderManagementController {

    public OrderManagementController() {
        // Default constructor
    }

    public static void addOrder(Order order) {
        // Validation logic if needed
        DatabaseLayer.saveOrder(order);
    }

    public static void updateOrder(Order order) {
        // Validation logic if needed
        DatabaseLayer.updateOrder(order);
    }

    public static void removeOrder(int orderId) {
        DatabaseLayer.deleteOrder(orderId);
    }

    public static List<Order> getAllOrders() {
        return DatabaseLayer.getAllOrders();
    }
}
