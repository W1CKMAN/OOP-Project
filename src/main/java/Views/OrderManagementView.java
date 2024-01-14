package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Models.CustomerOrder;
import Controllers.OrderManagementController;
public class OrderManagementView extends JFrame {
    private JTextField orderIdField;
    private JTextField customerNameField;
    private JTextField vehicleModelField;
    private JTextField statusField;
    private JButton updateButton;
    private JButton removeButton;
    private JButton addButton;


    public OrderManagementView() {
        setTitle("Order Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        addComponents();
        addListeners();
    }

    private void initComponents() {
        orderIdField = new JTextField(10);
        customerNameField = new JTextField(20);
        vehicleModelField = new JTextField(15);
        statusField = new JTextField(10);
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        removeButton = new JButton("Remove");
    }

    private void addComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        mainPanel.add(new JLabel("Order ID:"));
        mainPanel.add(orderIdField);

        mainPanel.add(new JLabel("Customer Name:"));
        mainPanel.add(customerNameField);

        mainPanel.add(new JLabel("Vehicle Model:"));
        mainPanel.add(vehicleModelField);

        mainPanel.add(new JLabel("Status:"));
        mainPanel.add(statusField);

        mainPanel.add(addButton);
        mainPanel.add(updateButton);
        mainPanel.add(removeButton);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        addButton.addActionListener(e -> {
            // Handle add button click
            String customerName = customerNameField.getText();
            String vehicleModel = vehicleModelField.getText();
            String status = statusField.getText();

            CustomerOrder newOrder = new CustomerOrder(0, customerName, vehicleModel, status);
            OrderManagementController.addOrder(newOrder);
        });

        updateButton.addActionListener(e -> {
            // Handle update button click
            int orderId = Integer.parseInt(orderIdField.getText());
            String customerName = customerNameField.getText();
            String vehicleModel = vehicleModelField.getText();
            String status = statusField.getText();

            CustomerOrder updatedOrder = new CustomerOrder(orderId, customerName, vehicleModel, status);
            OrderManagementController.updateOrder(updatedOrder);
        });

        removeButton.addActionListener(e -> {
            // Handle remove button click
            int orderId = Integer.parseInt(orderIdField.getText());
            OrderManagementController.removeOrder(orderId);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OrderManagementView().setVisible(true);
            }
        });
    }
}
