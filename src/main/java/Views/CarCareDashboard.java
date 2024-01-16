package Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CarCareDashboard extends JFrame {
    private JButton orderManagerButton;
    private JButton employeeManagerButton;
    private JButton customerDetailsManagerButton;
    private JButton supplierManagerButton;
    private JButton inventoryManagerButton;
    private JButton jobsManagerButton;
    private JPanel DashPanel;

    public CarCareDashboard() {
        getComponents();
        setTitle("Car Care Dashboard");
        setMinimumSize(new Dimension(500, 500));
        setContentPane(DashPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
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