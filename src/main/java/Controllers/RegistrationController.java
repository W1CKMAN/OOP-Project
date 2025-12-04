package Controllers;

import Models.Customer;
import Views.CustomerView;
import DatabaseConnection.DatabaseLayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @deprecated Use CustomerDAO and modern CustomerView instead
 */
@Deprecated
public class RegistrationController {
    private CustomerView form;

    @Deprecated
    public RegistrationController(CustomerView form, DatabaseLayer dbLayer) {
        this.form = form;

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

        Customer customer = DatabaseLayer.addUserToDatabase(name, email, phone, address);
        if (customer != null) {
            JOptionPane.showMessageDialog(form, "Customer registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(form, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}