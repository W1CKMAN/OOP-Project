import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Part {
    private String name;
    private int quantity;

    public Part(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

public class InventoryManagementApp extends JFrame {

    private List<Part> parts;

    private JTextField nameField;
    private JTextField quantityField;
    private DefaultListModel<String> listModel;

    public InventoryManagementApp() {
        parts = new ArrayList<>();

        // UI components
        nameField = new JTextField(20);
        quantityField = new JTextField(5);
        listModel = new DefaultListModel<>();
        JList<String> partsList = new JList<>(listModel);
        JButton addButton = new JButton("Add Part");
        JButton removeButton = new JButton("Remove Part");

        // Set layout
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Add components to the frame
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Quantity:"));
        add(quantityField);
        add(addButton);
        add(removeButton);
        add(new JLabel("Inventory:"));
        add(new JScrollPane(partsList));

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPart();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePart(partsList.getSelectedIndex());
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addPart() {
        String name = nameField.getText();
        String quantityStr = quantityField.getText();

        if (!name.isEmpty() && !quantityStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                Part newPart = new Part(name, quantity);
                parts.add(newPart);
                listModel.addElement(newPart.getName() + " - " + newPart.getQuantity());
                clearFields();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Name and Quantity cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removePart(int selectedIndex) {
        if (selectedIndex != -1) {
            parts.remove(selectedIndex);
            listModel.remove(selectedIndex);
        }
    }

    private void clearFields() {
        nameField.setText("");
        quantityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InventoryManagementApp();
            }
        });
    }
}
