package Controllers;

import Models.User;
import Views.CustomerView;
import DatabaseConnection.DatabaseLayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationController {
    private CustomerView form;
    private DatabaseLayer dbLayer;

    public RegistrationController(CustomerView form, DatabaseLayer dbLayer) {
        this.form = form;
        this.dbLayer = dbLayer;

        this.form.getRegisterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = form.getTName().getText();
        String email = form.getTEmail().getText();
        String phone = form.getTPhone().getText();
        String address = form.getTAddress().getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(form, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = DatabaseLayer.addUserToDatabase(name, email, phone, address);
        if (user != null) {
            JOptionPane.showMessageDialog(form, "User registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(form, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}