package Views;

import javax.swing.*;

import Models.CustomerOrder;
import DatabaseConnection.DatabaseLayer;

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
    private JTextField vehicleNumberField;
    private JButton clearButton;


    public OrderManagementView(JFrame parentFrame) {

        setTitle("Order Management");
        setSize(500, 400);
        setContentPane(ManagementPanel);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Add statuses to statusComboBox
        statusComboBox.addItem("Pending");
        statusComboBox.addItem("In Progress");
        statusComboBox.addItem("Completed");
        addListeners();
        setVisible(true);

    }

    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int customerId = Integer.parseInt(customerIdField.getText());
                if (!DatabaseLayer.customerIdExists(customerId)) {
                    JOptionPane.showMessageDialog(null, "Customer id doesn't exist");
                    return;
                }

                String vehicleModel = vehicleModelField.getText();
                String vehicleNumber = vehicleNumberField.getText(); 
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();
                Date orderDate = new Date();
                CustomerOrder newOrder = new CustomerOrder(0, customerId, orderDate, vehicleModel, vehicleNumber, status);  
                int newOrderId = DatabaseLayer.saveOrder(newOrder);
                JOptionPane.showMessageDialog(null, "New order id: " + newOrderId);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int orderId = Integer.parseInt(orderIdField.getText());
                int customerId = Integer.parseInt(customerIdField.getText());
                String vehicleModel = vehicleModelField.getText();
                String vehicleNumber = vehicleNumberField.getText(); 
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();
                Date orderDate = new Date();
                CustomerOrder updatedOrder = new CustomerOrder(orderId, customerId, orderDate, vehicleModel, vehicleNumber, status);
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
                // Clear the text fields
                customerIdField.setText("");
                vehicleModelField.setText("");
                vehicleNumberField.setText("");

                int orderId = Integer.parseInt(orderIdField.getText());
                CustomerOrder searchedOrder = DatabaseLayer.getOrderById(orderId);
                if (searchedOrder != null) {
                    customerIdField.setText(Integer.toString(searchedOrder.getCustomerId()));
                    vehicleModelField.setText(searchedOrder.getVehicleModel());
                    vehicleNumberField.setText(searchedOrder.getVehicleNumber());  // Set the vehicle number
                    statusComboBox.setSelectedItem(searchedOrder.getStatus());
                } else {
                    JOptionPane.showMessageDialog(null, "Order not found");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the text fields
                customerIdField.setText("");
                vehicleModelField.setText("");
                vehicleNumberField.setText("");
                statusComboBox.setSelectedIndex(0);  // Reset the statusComboBox
            }
        });
    }

    public static void main(String[] args) {
        new OrderManagementView(new JFrame());
    }
}