package Views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import DAO.Impl.OrderDAOImpl;
import DAO.Impl.CustomerDAOImpl;
import DAO.OrderDAO;
import DAO.CustomerDAO;
import Models.Order;
import Models.Customer;
import Models.SendMail;
import Utils.ToastNotification;
import net.miginfocom.swing.MigLayout;

/**
 * Modern Order Management View with FlatLaf styling
 */
public class OrderManagementView extends JDialog {
    
    // DAOs
    private final OrderDAO orderDAO;
    private final CustomerDAO customerDAO;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<CustomerComboItem> customerComboBox;
    private JTextField vehicleModelField;
    private JTextField vehicleNumberField;
    private JTextField vehicleMakeField;
    private JTextField vehicleYearField;
    private JTextArea notesArea;
    private JComboBox<String> statusComboBox;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Statistics labels
    private JLabel pendingCountLabel;
    private JLabel inProgressCountLabel;
    private JLabel completedCountLabel;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Currently selected order
    private Order selectedOrder;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Inner class for customer combo items
    private static class CustomerComboItem {
        private final int customerId;
        private final String name;
        private final String email;
        
        public CustomerComboItem(int customerId, String name, String email) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
        }
        
        public int getCustomerId() { return customerId; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() { return name + " (ID: " + customerId + ")"; }
    }

    public OrderManagementView() {
        this.orderDAO = new OrderDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
        initializeUI();
        loadCustomers();
        loadOrders();
        updateStatistics();
    }

    private void initializeUI() {
        setTitle("Order Management");
        setSize(1200, 800);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][][][grow]"));
        mainPanel.setBackground(new Color(248, 250, 252));
        
        // Header section
        mainPanel.add(createHeaderPanel(), "growx, wrap");
        
        // Statistics cards
        mainPanel.add(createStatisticsPanel(), "growx, wrap, gaptop 10");
        
        // Content section - split between form and table
        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 0, gaptop 15", "[400!][grow]", "[grow]"));
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), "growy");
        contentPanel.add(createTablePanel(), "grow");
        
        mainPanel.add(contentPanel, "grow");
        
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 0", "[][grow][]", "[]"));
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, "");
        
        // Spacer
        headerPanel.add(new JLabel(), "growx");
        
        // Search field
        searchField = new JTextField(22);
        searchField.putClientProperty("JTextField.placeholderText", "Search orders...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        headerPanel.add(searchField, "");
        
        return headerPanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow][grow][grow]", "[]"));
        statsPanel.setOpaque(false);
        
        statsPanel.add(createStatCard("Pending", "0", new Color(251, 191, 36)), "grow");
        statsPanel.add(createStatCard("In Progress", "0", new Color(59, 130, 246)), "grow");
        statsPanel.add(createStatCard("Completed", "0", new Color(34, 197, 94)), "grow");
        statsPanel.add(createStatCard("Total Orders", "0", new Color(139, 92, 246)), "grow");
        
        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("fill, insets 15", "[grow]", "[][]"));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor)
        ));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(107, 114, 128));
        card.add(titleLbl, "wrap");
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(new Color(30, 41, 59));
        card.add(valueLbl, "");
        
        // Store references for updates
        if (title.equals("Pending")) pendingCountLabel = valueLbl;
        else if (title.equals("In Progress")) inProgressCountLabel = valueLbl;
        else if (title.equals("Completed")) completedCountLabel = valueLbl;
        
        return card;
    }

    private JPanel createFormPanel() {
        JPanel formCard = new JPanel(new MigLayout("fillx, wrap 1, insets 20", "[grow]", ""));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Form title
        JLabel formTitle = new JLabel("Order Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(30, 41, 59));
        formCard.add(formTitle, "wrap, gapbottom 15");
        
        // Customer selection
        formCard.add(createLabel("Select Customer *"), "wrap, gaptop 5");
        customerComboBox = new JComboBox<>();
        customerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(customerComboBox, "growx, wrap");
        
        // Vehicle info section
        JLabel vehicleTitle = new JLabel("Vehicle Information");
        vehicleTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vehicleTitle.setForeground(new Color(71, 85, 105));
        formCard.add(vehicleTitle, "wrap, gaptop 15");
        
        // Vehicle Make
        formCard.add(createLabel("Vehicle Make"), "wrap, gaptop 5");
        vehicleMakeField = createTextField("e.g., Toyota, Honda");
        formCard.add(vehicleMakeField, "growx, wrap");
        
        // Vehicle Model
        formCard.add(createLabel("Vehicle Model *"), "wrap, gaptop 10");
        vehicleModelField = createTextField("e.g., Camry, Civic");
        formCard.add(vehicleModelField, "growx, wrap");
        
        // Vehicle Year
        formCard.add(createLabel("Year"), "wrap, gaptop 10");
        vehicleYearField = createTextField("e.g., 2022");
        formCard.add(vehicleYearField, "growx, wrap");
        
        // Vehicle Number (License Plate)
        formCard.add(createLabel("License Plate *"), "wrap, gaptop 10");
        vehicleNumberField = createTextField("e.g., ABC-1234");
        formCard.add(vehicleNumberField, "growx, wrap");
        
        // Notes
        formCard.add(createLabel("Service Notes"), "wrap, gaptop 10");
        notesArea = new JTextArea(2, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        formCard.add(notesScroll, "growx, wrap");
        
        // Status
        formCard.add(createLabel("Status *"), "wrap, gaptop 10");
        statusComboBox = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed", "Cancelled"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(statusComboBox, "growx, wrap, gapbottom 20");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[][]"));
        buttonsPanel.setOpaque(false);
        
        addButton = createButton("Create Order", new Color(34, 197, 94));
        updateButton = createButton("Update", new Color(59, 130, 246));
        deleteButton = createButton("Cancel Order", new Color(239, 68, 68));
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
        
        // Header with title and filter
        JPanel tableHeader = new JPanel(new MigLayout("fill, insets 0", "[][grow][]", "[]"));
        tableHeader.setOpaque(false);
        
        JLabel tableTitle = new JLabel("Recent Orders");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(30, 41, 59));
        tableHeader.add(tableTitle, "");
        
        tableHeader.add(new JLabel(), "growx");
        
        // Status filter
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Status", "Pending", "In Progress", "Completed", "Cancelled"});
        statusFilter.addActionListener(e -> filterByStatus((String) statusFilter.getSelectedItem()));
        tableHeader.add(statusFilter, "");
        
        tableCard.add(tableHeader, "wrap, gapbottom 10");
        
        // Create table
        String[] columns = {"ID", "Customer", "Vehicle", "Plate", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderTable.setRowHeight(45);
        orderTable.setShowHorizontalLines(true);
        orderTable.setGridColor(new Color(241, 245, 249));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderTable.getTableHeader().setBackground(new Color(248, 250, 252));
        orderTable.getTableHeader().setForeground(new Color(71, 85, 105));
        orderTable.setSelectionBackground(new Color(224, 242, 254));
        orderTable.setSelectionForeground(new Color(30, 41, 59));
        
        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Customer
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Vehicle
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Plate
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Date
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Status
        
        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        orderTable.setRowSorter(rowSorter);
        
        // Selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(orderTable);
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
        addButton.addActionListener(e -> createOrder());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> cancelOrder());
        clearButton.addActionListener(e -> clearForm());
    }

    private void loadCustomers() {
        customerComboBox.removeAllItems();
        try {
            List<Customer> customers = customerDAO.findAllActive();
            for (Customer cust : customers) {
                customerComboBox.addItem(new CustomerComboItem(
                    cust.getCustomerId(),
                    cust.getName(),
                    cust.getEmail()
                ));
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading customers: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void createOrder() {
        if (!validateForm()) return;
        
        try {
            CustomerComboItem selectedCustomer = (CustomerComboItem) customerComboBox.getSelectedItem();
            if (selectedCustomer == null) {
                ToastNotification.show(this, "Please select a customer", ToastNotification.Type.WARNING);
                return;
            }
            
            Order order = new Order();
            order.setCustomerId(selectedCustomer.getCustomerId());
            order.setVehicleModel(vehicleModelField.getText().trim());
            order.setVehicleNumber(vehicleNumberField.getText().trim());
            order.setStatus((String) statusComboBox.getSelectedItem());
            order.setOrderDate(new Date());
            
            orderDAO.save(order);
            ToastNotification.show(this, "Order created successfully! ID: " + order.getOrderId(), ToastNotification.Type.SUCCESS);
            
            loadOrders();
            updateStatistics();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error creating order: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void updateOrder() {
        if (selectedOrder == null) {
            ToastNotification.show(this, "Please select an order to update", ToastNotification.Type.WARNING);
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            CustomerComboItem selectedCustomer = (CustomerComboItem) customerComboBox.getSelectedItem();
            String oldStatus = selectedOrder.getStatus();
            String newStatus = (String) statusComboBox.getSelectedItem();
            
            selectedOrder.setCustomerId(selectedCustomer.getCustomerId());
            selectedOrder.setVehicleModel(vehicleModelField.getText().trim());
            selectedOrder.setVehicleNumber(vehicleNumberField.getText().trim());
            selectedOrder.setStatus(newStatus);
            
            orderDAO.update(selectedOrder);
            ToastNotification.show(this, "Order updated successfully!", ToastNotification.Type.SUCCESS);
            
            // Send email if status changed to Completed
            if ("Completed".equals(newStatus) && !newStatus.equals(oldStatus)) {
                sendCompletionEmail(selectedCustomer);
            }
            
            loadOrders();
            updateStatistics();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error updating order: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void cancelOrder() {
        if (selectedOrder == null) {
            ToastNotification.show(this, "Please select an order to cancel", ToastNotification.Type.WARNING);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel order #" + selectedOrder.getOrderId() + "?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                selectedOrder.setStatus("Cancelled");
                orderDAO.update(selectedOrder);
                ToastNotification.show(this, "Order cancelled successfully!", ToastNotification.Type.SUCCESS);
                loadOrders();
                updateStatistics();
                clearForm();
            } catch (Exception ex) {
                ToastNotification.show(this, "Error cancelling order: " + ex.getMessage(), ToastNotification.Type.ERROR);
            }
        }
    }

    private boolean validateForm() {
        if (customerComboBox.getSelectedItem() == null) {
            ToastNotification.show(this, "Please select a customer", ToastNotification.Type.WARNING);
            return false;
        }
        
        if (vehicleModelField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Vehicle model is required", ToastNotification.Type.WARNING);
            vehicleModelField.requestFocus();
            return false;
        }
        
        if (vehicleNumberField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "License plate is required", ToastNotification.Type.WARNING);
            vehicleNumberField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        
        try {
            List<Order> orders = orderDAO.findAll();
            for (Order order : orders) {
                // Get customer name
                String customerName = "Unknown";
                try {
                    Customer cust = customerDAO.findById(order.getCustomerId()).orElse(null);
                    if (cust != null) {
                        customerName = cust.getName();
                    }
                } catch (Exception ignored) {}
                
                tableModel.addRow(new Object[]{
                    order.getOrderId(),
                    customerName,
                    order.getVehicleModel(),
                    order.getVehicleNumber(),
                    order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "-",
                    order.getStatus()
                });
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading orders: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void updateStatistics() {
        try {
            List<Order> orders = orderDAO.findAll();
            long pending = orders.stream().filter(o -> "Pending".equals(o.getStatus())).count();
            long inProgress = orders.stream().filter(o -> "In Progress".equals(o.getStatus())).count();
            long completed = orders.stream().filter(o -> "Completed".equals(o.getStatus())).count();
            
            if (pendingCountLabel != null) pendingCountLabel.setText(String.valueOf(pending));
            if (inProgressCountLabel != null) inProgressCountLabel.setText(String.valueOf(inProgress));
            if (completedCountLabel != null) completedCountLabel.setText(String.valueOf(completed));
        } catch (Exception ignored) {}
    }

    private void populateFormFromTable(int viewRow) {
        int modelRow = orderTable.convertRowIndexToModel(viewRow);
        
        int orderId = (Integer) tableModel.getValueAt(modelRow, 0);
        selectedOrder = orderDAO.findById(orderId).orElse(null);
        
        if (selectedOrder != null) {
            // Select customer in combo
            for (int i = 0; i < customerComboBox.getItemCount(); i++) {
                CustomerComboItem item = customerComboBox.getItemAt(i);
                if (item.getCustomerId() == selectedOrder.getCustomerId()) {
                    customerComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            vehicleModelField.setText(selectedOrder.getVehicleModel());
            vehicleNumberField.setText(selectedOrder.getVehicleNumber());
            vehicleMakeField.setText(""); // Not stored in current model
            vehicleYearField.setText(""); // Not stored in current model
            notesArea.setText(""); // Not stored in current model
            statusComboBox.setSelectedItem(selectedOrder.getStatus());
        }
    }

    private void clearForm() {
        if (customerComboBox.getItemCount() > 0) {
            customerComboBox.setSelectedIndex(0);
        }
        vehicleMakeField.setText("");
        vehicleModelField.setText("");
        vehicleYearField.setText("");
        vehicleNumberField.setText("");
        notesArea.setText("");
        statusComboBox.setSelectedIndex(0);
        selectedOrder = null;
        orderTable.clearSelection();
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void filterByStatus(String status) {
        if ("All Status".equals(status)) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("^" + status + "$", 5));
        }
    }

    private void sendCompletionEmail(CustomerComboItem customer) {
        try {
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                String message = "Dear " + customer.toString().split(" \\(")[0] + ",\n\n" +
                    "Your vehicle service order has been completed!\n\n" +
                    "Order Details:\n" +
                    "- Order ID: " + selectedOrder.getOrderId() + "\n" +
                    "- Vehicle: " + selectedOrder.getVehicleModel() + "\n" +
                    "- License Plate: " + selectedOrder.getVehicleNumber() + "\n\n" +
                    "Your vehicle is ready for pickup.\n\n" +
                    "Thank you for choosing CarCare!\n\n" +
                    "Best regards,\n" +
                    "CarCare Management System";
                
                // Run email sending in background thread
                new Thread(() -> {
                    SendMail.send(customer.getEmail(), "Order Completed - Vehicle Ready for Pickup", message);
                }).start();
                
                ToastNotification.show(this, "Completion email sent to customer", ToastNotification.Type.INFO);
            }
        } catch (Exception e) {
            System.err.println("Failed to send completion email: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OrderManagementView view = new OrderManagementView();
            view.setVisible(true);
        });
    }
}