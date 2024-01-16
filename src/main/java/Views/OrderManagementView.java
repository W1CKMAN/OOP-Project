package Views;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Models.Order;
import DatabaseConnection.DatabaseLayer;
import Models.SendMail;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Date;
import java.util.List;

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
    private JTable table1;
    private JButton reloadButton;

    public OrderManagementView() {
        getComponents();
        setTitle("Order Manager");
        setSize(900, 400);
        setContentPane(ManagementPanel);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Add statuses to statusComboBox
        statusComboBox.addItem("Pending");
        statusComboBox.addItem("In Progress");
        statusComboBox.addItem("Completed");
        addListeners();
        table1.setModel(getAllOrdersTableModel());
        try {
            Image img = ImageIO.read(new File("C:\\Users\\helit\\IdeaProjects\\OOP-Project\\src\\main\\java\\Views\\Images\\img.png"));
            Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImg);
            reloadButton.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private DefaultTableModel getAllOrdersTableModel() {
        String[] columnNames = {"Order ID", "Customer ID", "Order Date", "Vehicle Model", "Vehicle Number", "Status"};
        List<Order> orders = DatabaseLayer.getAllOrders();
        Object[][] data = new Object[orders.size()][columnNames.length];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            data[i][0] = order.getOrderId();
            data[i][1] = order.getCustomerId();
            data[i][2] = order.getOrderDate();
            data[i][3] = order.getVehicleModel();
            data[i][4] = order.getVehicleNumber();
            data[i][5] = order.getStatus();
        }
        return new DefaultTableModel(data, columnNames);
    }
    
    private void addListeners() {
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table1.setModel(getAllOrdersTableModel());
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (customerIdField.getText().isEmpty() || vehicleModelField.getText().isEmpty() || vehicleNumberField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled");
                    return;
                }
                int customerId = Integer.parseInt(customerIdField.getText());
                if (!DatabaseLayer.customerIdExists(customerId)) {
                    JOptionPane.showMessageDialog(null, "Customer id doesn't exist");
                    return;
                }

                String vehicleModel = vehicleModelField.getText();
                String vehicleNumber = vehicleNumberField.getText(); 
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();
                Date orderDate = new Date();
                Order newOrder = new Order(0, customerId, orderDate, vehicleModel, vehicleNumber, status);
                int newOrderId = DatabaseLayer.saveOrder(newOrder);
                JOptionPane.showMessageDialog(null, "New order id: " + newOrderId);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (orderIdField.getText().isEmpty() || customerIdField.getText().isEmpty() || vehicleModelField.getText().isEmpty() || vehicleNumberField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled");
                    return;
                }
                if (Objects.equals(statusComboBox.getSelectedItem(), "Completed")) {
                    String email = DatabaseLayer.getCustomerEmail(Integer.parseInt(customerIdField.getText()));
                    if (email != null) {
                        SendMail.send(email, "Order Completed", "Your order has been completed.");
                    }
                }
                int orderId = Integer.parseInt(orderIdField.getText());
                int customerId = Integer.parseInt(customerIdField.getText());
                String vehicleModel = vehicleModelField.getText();
                String vehicleNumber = vehicleNumberField.getText(); 
                String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();
                Date orderDate = new Date();
                Order updatedOrder = new Order(orderId, customerId, orderDate, vehicleModel, vehicleNumber, status);
                DatabaseLayer.updateOrder(updatedOrder);
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (orderIdField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Order ID field must be filled");
                    return;
                }
                int orderId = Integer.parseInt(orderIdField.getText());
                DatabaseLayer.deleteOrder(orderId);
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (orderIdField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Order ID field must be filled");
                    return;
                }
                // Clear the text fields
                customerIdField.setText("");
                vehicleModelField.setText("");
                vehicleNumberField.setText("");

                int orderId = Integer.parseInt(orderIdField.getText());
                Order searchedOrder = DatabaseLayer.getOrderById(orderId);
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
                statusComboBox.setSelectedIndex(0); 
            }
        });
    }

    public static void main(String[] args) {
        OrderManagementView view = new OrderManagementView();
        view.setVisible(true);  // Call setVisible directly on the OrderManagementView instance
    }
}