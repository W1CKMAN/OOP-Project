package Views;

import javax.swing.*;

import Models.CustomerOrder;
import DatabaseConnection.DatabaseLayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Date;

public class OrderManagementView extends JDialog {
    private JTextField orderIdField;
    private JTextField customerIdField;
    private JTextField vehicleModelField;
    private JButton updateButton;
    private JButton removeButton;
    private JButton addButton;
    private JButton searchButton;
    private JComboBox statusComboBox;
    private JPanel ManagementPanel;


    public OrderManagementView(JFrame parentFrame) {

        setTitle("Order Management");
        setSize(400, 300);
        setContentPane(ManagementPanel);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addListeners();
        setVisible(true);
    }

    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int customerId = Integer.parseInt(customerIdField.getText());
                String vehicleModel = vehicleModelField.getText();
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();  // Get the selected status

                // Get the current date
                Date orderDate = new Date();

                // Pass a placeholder value for orderId
                CustomerOrder newOrder = new CustomerOrder(0, customerId, orderDate, vehicleModel, status);
                DatabaseLayer.saveOrder(newOrder);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int orderId = Integer.parseInt(orderIdField.getText());
                int customerId = Integer.parseInt(customerIdField.getText());
                String vehicleModel = vehicleModelField.getText();
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();  // Get the selected status

                // Get the current date
                Date orderDate = new Date();

                CustomerOrder updatedOrder = new CustomerOrder(orderId, customerId, orderDate, vehicleModel, status);
                DatabaseLayer.updateOrder(updatedOrder);
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int orderId = Integer.parseInt(orderIdField.getText());
                DatabaseLayer.deleteOrder(orderId);
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int orderId = Integer.parseInt(orderIdField.getText());
                CustomerOrder searchedOrder = DatabaseLayer.getOrderById(orderId);
                if (searchedOrder != null) {
                    customerIdField.setText(Integer.toString(searchedOrder.getCustomerId()));
                    vehicleModelField.setText(searchedOrder.getVehicleModel());

                    // Select the appropriate status in the statusComboBox
                    statusComboBox.setSelectedItem(searchedOrder.getStatus());
                } else {
                    JOptionPane.showMessageDialog(null, "Order not found");
                }
            }
        });
    }

    public static void main(String[] args) {
        new OrderManagementView(new JFrame());
    }
    

}
