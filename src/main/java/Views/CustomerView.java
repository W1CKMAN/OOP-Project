package Views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import DAO.Impl.CustomerDAOImpl;
import DAO.CustomerDAO;
import Models.Customer;
import Utils.ToastNotification;
import Utils.ValidationUtil;
import net.miginfocom.swing.MigLayout;

/**
 * Modern Customer Management View with FlatLaf styling
 */
public class CustomerView extends JDialog {
    
    // DAO
    private final CustomerDAO customerDAO;
    
    // UI Components
    private JTextField searchField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextArea vehicleHistoryArea;
    private JComboBox<String> statusComboBox;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Currently selected customer
    private Customer selectedCustomer;

    public CustomerView() {
        this.customerDAO = new CustomerDAOImpl();
        initializeUI();
        loadCustomers();
    }

    private void initializeUI() {
        setTitle("Customer Management");
        setSize(1150, 750);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container with modern layout
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        mainPanel.setBackground(new Color(248, 250, 252));
        
        // Header section
        mainPanel.add(createHeaderPanel(), "growx, wrap");
        
        // Content section - split between form and table
        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 0", "[380!][grow]", "[grow]"));
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), "growy");
        contentPanel.add(createTablePanel(), "grow");
        
        mainPanel.add(contentPanel, "grow");
        
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 0 0 15 0", "[][grow][][]", "[]"));
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, "");
        
        // Spacer
        headerPanel.add(new JLabel(), "growx");
        
        // Customer count badge
        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(new Color(59, 130, 246));
        try {
            long count = customerDAO.count();
            countLabel.setText(count + " Customers");
        } catch (Exception e) {
            countLabel.setText("0 Customers");
        }
        headerPanel.add(countLabel, "");
        
        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search customers...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        headerPanel.add(searchField, "gapleft 15");
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formCard = new JPanel(new MigLayout("fillx, wrap 1, insets 20", "[grow]", ""));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Form title
        JLabel formTitle = new JLabel("Customer Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(30, 41, 59));
        formCard.add(formTitle, "wrap, gapbottom 15");
        
        // Name field
        formCard.add(createLabel("Full Name *"), "wrap, gaptop 5");
        nameField = createTextField("Enter customer name");
        formCard.add(nameField, "growx, wrap");
        
        // Email field
        formCard.add(createLabel("Email Address *"), "wrap, gaptop 10");
        emailField = createTextField("Enter email address");
        formCard.add(emailField, "growx, wrap");
        
        // Phone field
        formCard.add(createLabel("Phone Number *"), "wrap, gaptop 10");
        phoneField = createTextField("Enter phone number");
        formCard.add(phoneField, "growx, wrap");
        
        // Address field
        formCard.add(createLabel("Address"), "wrap, gaptop 10");
        addressArea = new JTextArea(2, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        formCard.add(addressScroll, "growx, wrap");
        
        // Vehicle History (read-only display)
        formCard.add(createLabel("Vehicle History"), "wrap, gaptop 10");
        vehicleHistoryArea = new JTextArea(3, 20);
        vehicleHistoryArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vehicleHistoryArea.setEditable(false);
        vehicleHistoryArea.setBackground(new Color(248, 250, 252));
        JScrollPane historyScroll = new JScrollPane(vehicleHistoryArea);
        historyScroll.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        formCard.add(historyScroll, "growx, wrap");
        
        // Status
        formCard.add(createLabel("Status"), "wrap, gaptop 10");
        statusComboBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(statusComboBox, "growx, wrap, gapbottom 20");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[][]"));
        buttonsPanel.setOpaque(false);
        
        addButton = createButton("Add Customer", new Color(34, 197, 94));
        updateButton = createButton("Update", new Color(59, 130, 246));
        deleteButton = createButton("Delete", new Color(239, 68, 68));
        clearButton = createButton("Clear", new Color(107, 114, 128));
        
        buttonsPanel.add(addButton, "grow");
        buttonsPanel.add(updateButton, "grow, wrap");
        buttonsPanel.add(deleteButton, "grow");
        buttonsPanel.add(clearButton, "grow");
        
        formCard.add(buttonsPanel, "growx, gaptop 10");
        
        // Add action listeners
        addActionListeners();
        
        return formCard;
    }

    private JPanel createTablePanel() {
        JPanel tableCard = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Table title
        JLabel tableTitle = new JLabel("Customer List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(30, 41, 59));
        tableCard.add(tableTitle, "wrap, gapbottom 10");
        
        // Create table
        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setRowHeight(40);
        customerTable.setShowHorizontalLines(true);
        customerTable.setGridColor(new Color(241, 245, 249));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.getTableHeader().setBackground(new Color(248, 250, 252));
        customerTable.getTableHeader().setForeground(new Color(71, 85, 105));
        customerTable.setSelectionBackground(new Color(224, 242, 254));
        customerTable.setSelectionForeground(new Color(30, 41, 59));
        
        // Set column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(180);  // Email
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Phone
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(200);  // Address
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(70);   // Status
        
        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(rowSorter);
        
        // Selection listener
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableCard.add(scrollPane, "grow");
        
        return tableCard;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(71, 85, 105));
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    private void addActionListeners() {
        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearForm());
    }

    private void addCustomer() {
        if (!validateForm()) return;
        
        // Check for duplicate email
        String email = emailField.getText().trim();
        if (customerDAO.emailExists(email)) {
            ToastNotification.show(this, "Email already registered", ToastNotification.Type.WARNING);
            emailField.requestFocus();
            return;
        }
        
        try {
            Customer customer = new Customer();
            customer.setName(nameField.getText().trim());
            customer.setEmail(email);
            customer.setPhone(phoneField.getText().trim());
            customer.setAddress(addressArea.getText().trim());
            customer.setActive("Active".equals(statusComboBox.getSelectedItem()));
            
            customerDAO.save(customer);
            ToastNotification.show(this, "Customer added successfully!", ToastNotification.Type.SUCCESS);
            loadCustomers();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error adding customer: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void updateCustomer() {
        if (selectedCustomer == null) {
            ToastNotification.show(this, "Please select a customer to update", ToastNotification.Type.WARNING);
            return;
        }
        
        if (!validateForm()) return;
        
        // Check for duplicate email (excluding current customer)
        String email = emailField.getText().trim();
        if (!email.equals(selectedCustomer.getEmail()) && customerDAO.emailExists(email)) {
            ToastNotification.show(this, "Email already registered", ToastNotification.Type.WARNING);
            emailField.requestFocus();
            return;
        }
        
        try {
            selectedCustomer.setName(nameField.getText().trim());
            selectedCustomer.setEmail(email);
            selectedCustomer.setPhone(phoneField.getText().trim());
            selectedCustomer.setAddress(addressArea.getText().trim());
            selectedCustomer.setActive("Active".equals(statusComboBox.getSelectedItem()));
            
            customerDAO.update(selectedCustomer);
            ToastNotification.show(this, "Customer updated successfully!", ToastNotification.Type.SUCCESS);
            loadCustomers();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error updating customer: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void deleteCustomer() {
        if (selectedCustomer == null) {
            ToastNotification.show(this, "Please select a customer to delete", ToastNotification.Type.WARNING);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate customer: " + selectedCustomer.getName() + "?\n" +
            "(This will mark the customer as inactive)",
            "Confirm Deactivate",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerDAO.delete(selectedCustomer.getCustomerId());
                ToastNotification.show(this, "Customer deactivated successfully!", ToastNotification.Type.SUCCESS);
                loadCustomers();
                clearForm();
            } catch (Exception ex) {
                ToastNotification.show(this, "Error deactivating customer: " + ex.getMessage(), ToastNotification.Type.ERROR);
            }
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Customer name is required", ToastNotification.Type.WARNING);
            nameField.requestFocus();
            return false;
        }
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            ToastNotification.show(this, "Email is required", ToastNotification.Type.WARNING);
            emailField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            ToastNotification.show(this, "Invalid email format", ToastNotification.Type.WARNING);
            emailField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Phone number is required", ToastNotification.Type.WARNING);
            phoneField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        
        try {
            List<Customer> customers = customerDAO.findAll();
            for (Customer cust : customers) {
                tableModel.addRow(new Object[]{
                    cust.getCustomerId(),
                    cust.getName(),
                    cust.getEmail(),
                    cust.getPhone(),
                    cust.getAddress() != null ? cust.getAddress() : "-",
                    cust.isActive() ? "Active" : "Inactive"
                });
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading customers: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void populateFormFromTable(int viewRow) {
        int modelRow = customerTable.convertRowIndexToModel(viewRow);
        
        int custId = (Integer) tableModel.getValueAt(modelRow, 0);
        selectedCustomer = customerDAO.findById(custId).orElse(null);
        
        if (selectedCustomer != null) {
            nameField.setText(selectedCustomer.getName());
            emailField.setText(selectedCustomer.getEmail());
            phoneField.setText(selectedCustomer.getPhone());
            addressArea.setText(selectedCustomer.getAddress() != null ? selectedCustomer.getAddress() : "");
            vehicleHistoryArea.setText(selectedCustomer.getVehicleHistory() != null ? 
                selectedCustomer.getVehicleHistory() : "No vehicle history");
            statusComboBox.setSelectedItem(selectedCustomer.isActive() ? "Active" : "Inactive");
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        vehicleHistoryArea.setText("");
        statusComboBox.setSelectedIndex(0);
        selectedCustomer = null;
        customerTable.clearSelection();
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    // Legacy getters for backward compatibility with RegistrationController
    public JTextField getTName() {
        return this.nameField;
    }

    public JTextField getTEmail() {
        return this.emailField;
    }

    public JTextField getTPhone() {
        return this.phoneField;
    }

    public JTextField getTAddress() {
        // Return a JTextField wrapper for the address area
        JTextField addressField = new JTextField();
        addressField.setText(addressArea.getText());
        return addressField;
    }

    public JButton getRegisterButton() {
        return this.addButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
            CustomerView view = new CustomerView();
            view.setVisible(true);
        });
    }
}