import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CarCareApp extends JFrame {
    private JTextField employeeEmailField;
    private JButton allocateJobButton;

    public CarCareApp() {
        setTitle("Car Care Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        employeeEmailField = new JTextField(20);
        allocateJobButton = new JButton("Allocate Job");

        allocateJobButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String employeeEmail = employeeEmailField.getText();

                // Allocate job and send notification to the employee
                allocateJobAndSendNotification(employeeEmail);

                // You can add additional logic here, such as updating the status in the database, etc.
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(new JLabel("Employee Email:"))
                                        .addComponent(employeeEmailField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(allocateJobButton))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(new JLabel("Employee Email:"))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(employeeEmailField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(allocateJobButton)
                                .addContainerGap())
        );
    }

    private void allocateJobAndSendNotification(String employeeEmail) {
        // Replace the following with your email configuration
        String host = "smtp.example.com";
        String username = "your_username";
        String password = "your_password";
        String from = "your_email@example.com";

        // Set up the email properties
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");

        // Get the Session object
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the from address
            message.setFrom(new InternetAddress(from));

            // Set the to address
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(employeeEmail));

            // Set the subject
            message.setSubject("New Job Allocated");

            // Set the message body
            message.setText("Dear Employee,\nA new job has been allocated to you. Please check the system for details.");

            // Send the message
            Transport.send(message);

            JOptionPane.showMessageDialog(this, "Job allocated successfully!");

        } catch (MessagingException mex) {
            mex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error allocating job: " + mex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CarCareApp().setVisible(true);
            }
        });
    }
}
