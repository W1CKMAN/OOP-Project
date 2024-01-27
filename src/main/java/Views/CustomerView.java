package Views;

import javax.swing.*;
import java.awt.*;

public class CustomerView extends JDialog {
    private JTextField tName;
    private JTextField tEmail;
    private JTextField tPhone;
    private JTextField tAddress;
    private JButton registerButton;
    private JPanel registerPanel;
    private JTextField searchByIDTextField;
    private JButton searchButton;
    private JButton updateButton;
    private JButton removeButton;
    private JButton clearButton;
    private JTable table1;
    private JScrollPane CustomerTable;

    public CustomerView() {
        setTitle("Add a customer");
        setMinimumSize(new Dimension(900, 500));
        setContentPane(registerPanel);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    // Getters for text fields and register button
    public JTextField getTName() {
        return this.tName;
    }

    public JTextField getTEmail() {
        return this.tEmail;
    }

    public JTextField getTPhone() {
        return this.tPhone;
    }

    public JTextField getTAddress() {
        return this.tAddress;
    }

    public JButton getRegisterButton() {
        return this.registerButton;
    }
    public static void main(String[] args) {
        CustomerView view = new CustomerView();
        view.setVisible(true);
    }
}