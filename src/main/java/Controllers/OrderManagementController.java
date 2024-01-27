package Controllers;

import Models.Order;
import DatabaseConnection.DatabaseLayer;

import java.util.List;

public class OrderManagementController {
    private static DatabaseLayer databaseLayer;

    public OrderManagementController(DatabaseLayer databaseLayer) {
        this.databaseLayer = databaseLayer;
    }

    public static void addOrder(Order order) {
        // Validation logic if needed
        databaseLayer.saveOrder(order);
    }

    public static void updateOrder(Order order) {
        // Validation logic if needed
        databaseLayer.updateOrder(order);
    }

    public static void removeOrder(int orderId) {
        databaseLayer.deleteOrder(orderId);
    }

    public List<Order> getAllOrders() {
        return databaseLayer.getAllOrders();
    }
}
