package Views;

import Models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm extends JDialog {
    private JTextField tName;
    private JTextField tEmail;
    private JTextField tPhone;
    private JTextField tAddress;
    private JButton registerButton;
    private JPanel registerPanel;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Add a customer");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setVisible(true);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private User registerUser() {
        String name = tName.getText();
        String email = tEmail.getText();
        String phone = tPhone.getText();
        String address = tAddress.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return addUserToDatabase(name, email, phone, address);
    }

    private User addUserToDatabase(String name, String email, String phone, String address) {
        User user = null;
        final String JDBC_URL = "jdbc:mysql://localhost:3306/oop-chaos";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (name, email, phone, address) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);

            int addRows = preparedStatement.executeUpdate();
            if(addRows > 0) {
                user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RegistrationForm myForm = new RegistrationForm(null);
                myForm.setVisible(true);
            }
        });
    }
}