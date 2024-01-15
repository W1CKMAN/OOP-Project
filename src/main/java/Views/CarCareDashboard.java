package Views;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CarCareDashboard extends JFrame {
    private JButton orderManagerButton;
    private JButton employeeManagerButton;
    private JButton customerDetailsManagerButton;
    private JButton supplierManagerButton;
    private JButton inventoryManagerButton;
    private JButton jobsManagerButton;

    public CarCareDashboard() {
        // Initialize your buttons here
        orderManagerButton = new JButton("Order Manager");
        employeeManagerButton = new JButton("Employee Manager");
        customerDetailsManagerButton = new JButton("Customer Details Manager");
        supplierManagerButton = new JButton("Supplier Manager");
        inventoryManagerButton = new JButton("Inventory Manager");
        jobsManagerButton = new JButton("Jobs Manager");

        // Add buttons to layout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(orderManagerButton);
        add(employeeManagerButton);
        add(customerDetailsManagerButton);
        add(supplierManagerButton);
        add(inventoryManagerButton);
        add(jobsManagerButton);

        // Set frame properties
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addOrderManagerButtonListener(ActionListener listenForButton) {
        orderManagerButton.addActionListener(listenForButton);
    }

    public void addEmployeeManagerButtonListener(ActionListener listenForButton) {
        employeeManagerButton.addActionListener(listenForButton);
    }

    public void addcustomerDetailsManagerButtonListener(ActionListener listenForButton) {
        customerDetailsManagerButton.addActionListener(listenForButton);
    }

    public void addSupplierManagerButtonListener(ActionListener listenForButton) {
        supplierManagerButton.addActionListener(listenForButton);
    }

    public void addInventoryManagerButtonListener(ActionListener listenForButton) {
        inventoryManagerButton.addActionListener(listenForButton);
    }

    public void addJobsManagerButtonListener(ActionListener listenForButton) {
        jobsManagerButton.addActionListener(listenForButton);
    }
}