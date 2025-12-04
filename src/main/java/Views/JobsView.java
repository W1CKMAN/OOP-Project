package Views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import DAO.Impl.JobDAOImpl;
import DAO.Impl.EmployeeDAOImpl;
import DAO.Impl.OrderDAOImpl;
import DAO.JobDAO;
import DAO.EmployeeDAO;
import DAO.OrderDAO;
import Models.Job;
import Models.Employee;
import Models.Order;
import Models.SendMail;
import Utils.ToastNotification;
import net.miginfocom.swing.MigLayout;

/**
 * Modern Job Management View with FlatLaf styling
 */
public class JobsView extends JDialog {
    
    // DAOs
    private final JobDAO jobDAO;
    private final EmployeeDAO employeeDAO;
    private final OrderDAO orderDAO;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<OrderComboItem> orderComboBox;
    private JComboBox<EmployeeComboItem> employeeComboBox;
    private JTextArea descriptionArea;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> priorityComboBox;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Currently selected job
    private Job selectedJob;

    // Inner classes for combo box items
    private static class OrderComboItem {
        private final int orderId;
        private final String display;
        
        public OrderComboItem(int orderId, String display) {
            this.orderId = orderId;
            this.display = display;
        }
        
        public int getOrderId() { return orderId; }
        
        @Override
        public String toString() { return display; }
    }
    
    private static class EmployeeComboItem {
        private final int employeeId;
        private final String name;
        private final String email;
        
        public EmployeeComboItem(int employeeId, String name, String email) {
            this.employeeId = employeeId;
            this.name = name;
            this.email = email;
        }
        
        public int getEmployeeId() { return employeeId; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() { return name + " (ID: " + employeeId + ")"; }
    }

    public JobsView() {
        this.jobDAO = new JobDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        this.orderDAO = new OrderDAOImpl();
        initializeUI();
        loadComboBoxData();
        loadJobs();
    }

    private void initializeUI() {
        setTitle("Job Management");
        setSize(1100, 700);
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
        JLabel titleLabel = new JLabel("Job Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, "");
        
        // Spacer
        headerPanel.add(new JLabel(), "growx");
        
        // Status filter
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Status", "Pending", "In Progress", "Completed"});
        statusFilter.addActionListener(e -> filterByStatus((String) statusFilter.getSelectedItem()));
        headerPanel.add(statusFilter, "");
        
        // Search field
        searchField = new JTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search jobs...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        headerPanel.add(searchField, "");
        
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
        JLabel formTitle = new JLabel("Job Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(30, 41, 59));
        formCard.add(formTitle, "wrap, gapbottom 15");
        
        // Order selection
        formCard.add(createLabel("Select Order *"), "wrap, gaptop 5");
        orderComboBox = new JComboBox<>();
        orderComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(orderComboBox, "growx, wrap");
        
        // Employee selection
        formCard.add(createLabel("Assign Employee *"), "wrap, gaptop 10");
        employeeComboBox = new JComboBox<>();
        employeeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(employeeComboBox, "growx, wrap");
        
        // Description
        formCard.add(createLabel("Job Description *"), "wrap, gaptop 10");
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        formCard.add(descScroll, "growx, wrap");
        
        // Status
        formCard.add(createLabel("Status"), "wrap, gaptop 10");
        statusComboBox = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(statusComboBox, "growx, wrap");
        
        // Priority
        formCard.add(createLabel("Priority"), "wrap, gaptop 10");
        priorityComboBox = new JComboBox<>(new String[]{"Low", "Normal", "High", "Urgent"});
        priorityComboBox.setSelectedIndex(1); // Default to Normal
        priorityComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(priorityComboBox, "growx, wrap, gapbottom 20");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[][]"));
        buttonsPanel.setOpaque(false);
        
        addButton = createButton("Add Job", new Color(34, 197, 94));
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
        JLabel tableTitle = new JLabel("Active Jobs");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(30, 41, 59));
        tableCard.add(tableTitle, "wrap, gapbottom 10");
        
        // Create table
        String[] columns = {"ID", "Order ID", "Employee", "Description", "Status", "Priority"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jobTable = new JTable(tableModel);
        jobTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jobTable.setRowHeight(45);
        jobTable.setShowHorizontalLines(true);
        jobTable.setGridColor(new Color(241, 245, 249));
        jobTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        jobTable.getTableHeader().setBackground(new Color(248, 250, 252));
        jobTable.getTableHeader().setForeground(new Color(71, 85, 105));
        jobTable.setSelectionBackground(new Color(224, 242, 254));
        jobTable.setSelectionForeground(new Color(30, 41, 59));
        
        // Set column widths
        jobTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        jobTable.getColumnModel().getColumn(1).setPreferredWidth(70);   // Order ID
        jobTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Employee
        jobTable.getColumnModel().getColumn(3).setPreferredWidth(200);  // Description
        jobTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Status
        jobTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Priority
        
        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        jobTable.setRowSorter(rowSorter);
        
        // Selection listener
        jobTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jobTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(jobTable);
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
        addButton.addActionListener(e -> addJob());
        updateButton.addActionListener(e -> updateJob());
        deleteButton.addActionListener(e -> deleteJob());
        clearButton.addActionListener(e -> clearForm());
    }

    private void loadComboBoxData() {
        // Load orders
        orderComboBox.removeAllItems();
        try {
            List<Order> orders = orderDAO.findAll();
            for (Order order : orders) {
                String display = "Order #" + order.getOrderId() + " - " + order.getStatus();
                orderComboBox.addItem(new OrderComboItem(order.getOrderId(), display));
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading orders: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
        
        // Load employees
        employeeComboBox.removeAllItems();
        try {
            List<Employee> employees = employeeDAO.findAll();
            for (Employee emp : employees) {
                if (!"Terminated".equals(emp.getStatus())) {
                    employeeComboBox.addItem(new EmployeeComboItem(
                        emp.getEmployeeId(), 
                        emp.getEmployeeName(),
                        emp.getEmail()
                    ));
                }
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading employees: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void addJob() {
        if (!validateForm()) return;
        
        try {
            OrderComboItem selectedOrder = (OrderComboItem) orderComboBox.getSelectedItem();
            EmployeeComboItem selectedEmployee = (EmployeeComboItem) employeeComboBox.getSelectedItem();
            
            if (selectedOrder == null || selectedEmployee == null) {
                ToastNotification.show(this, "Please select order and employee", ToastNotification.Type.WARNING);
                return;
            }
            
            Job job = new Job();
            job.setOrderId(selectedOrder.getOrderId());
            job.setEmployeeId(selectedEmployee.getEmployeeId());
            job.setJobDescription(descriptionArea.getText().trim());
            job.setStatus((String) statusComboBox.getSelectedItem());
            
            jobDAO.save(job);
            ToastNotification.show(this, "Job created successfully!", ToastNotification.Type.SUCCESS);
            
            // Send email notification to employee
            sendJobNotification(selectedEmployee, job, "New Job Assigned");
            
            loadJobs();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error creating job: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void updateJob() {
        if (selectedJob == null) {
            ToastNotification.show(this, "Please select a job to update", ToastNotification.Type.WARNING);
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            OrderComboItem selectedOrder = (OrderComboItem) orderComboBox.getSelectedItem();
            EmployeeComboItem selectedEmployee = (EmployeeComboItem) employeeComboBox.getSelectedItem();
            
            selectedJob.setOrderId(selectedOrder.getOrderId());
            selectedJob.setEmployeeId(selectedEmployee.getEmployeeId());
            selectedJob.setJobDescription(descriptionArea.getText().trim());
            selectedJob.setStatus((String) statusComboBox.getSelectedItem());
            
            jobDAO.update(selectedJob);
            ToastNotification.show(this, "Job updated successfully!", ToastNotification.Type.SUCCESS);
            
            loadJobs();
            clearForm();
        } catch (Exception ex) {
            ToastNotification.show(this, "Error updating job: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void deleteJob() {
        if (selectedJob == null) {
            ToastNotification.show(this, "Please select a job to delete", ToastNotification.Type.WARNING);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this job?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                jobDAO.deleteById(selectedJob.getJobId());
                ToastNotification.show(this, "Job deleted successfully!", ToastNotification.Type.SUCCESS);
                loadJobs();
                clearForm();
            } catch (Exception ex) {
                ToastNotification.show(this, "Error deleting job: " + ex.getMessage(), ToastNotification.Type.ERROR);
            }
        }
    }

    private boolean validateForm() {
        if (orderComboBox.getSelectedItem() == null) {
            ToastNotification.show(this, "Please select an order", ToastNotification.Type.WARNING);
            return false;
        }
        
        if (employeeComboBox.getSelectedItem() == null) {
            ToastNotification.show(this, "Please select an employee", ToastNotification.Type.WARNING);
            return false;
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Job description is required", ToastNotification.Type.WARNING);
            descriptionArea.requestFocus();
            return false;
        }
        
        return true;
    }

    private void loadJobs() {
        tableModel.setRowCount(0);
        
        try {
            List<Job> jobs = jobDAO.findAll();
            for (Job job : jobs) {
                // Get employee name
                String employeeName = "-";
                try {
                    Employee emp = employeeDAO.findById(job.getEmployeeId()).orElse(null);
                    if (emp != null) {
                        employeeName = emp.getEmployeeName();
                    }
                } catch (Exception ignored) {}
                
                tableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getOrderId(),
                    employeeName,
                    job.getJobDescription(),
                    job.getStatus(),
                    "Normal" // Default priority if not stored
                });
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading jobs: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void populateFormFromTable(int viewRow) {
        int modelRow = jobTable.convertRowIndexToModel(viewRow);
        
        int jobId = (Integer) tableModel.getValueAt(modelRow, 0);
        selectedJob = jobDAO.findById(jobId).orElse(null);
        
        if (selectedJob != null) {
            // Select order in combo
            for (int i = 0; i < orderComboBox.getItemCount(); i++) {
                OrderComboItem item = orderComboBox.getItemAt(i);
                if (item.getOrderId() == selectedJob.getOrderId()) {
                    orderComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Select employee in combo
            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                EmployeeComboItem item = employeeComboBox.getItemAt(i);
                if (item.getEmployeeId() == selectedJob.getEmployeeId()) {
                    employeeComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            descriptionArea.setText(selectedJob.getJobDescription());
            statusComboBox.setSelectedItem(selectedJob.getStatus());
        }
    }

    private void clearForm() {
        if (orderComboBox.getItemCount() > 0) {
            orderComboBox.setSelectedIndex(0);
        }
        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }
        descriptionArea.setText("");
        statusComboBox.setSelectedIndex(0);
        priorityComboBox.setSelectedIndex(1);
        selectedJob = null;
        jobTable.clearSelection();
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
            rowSorter.setRowFilter(RowFilter.regexFilter("^" + status + "$", 4));
        }
    }

    private void sendJobNotification(EmployeeComboItem employee, Job job, String subject) {
        try {
            if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
                String message = "Dear " + employee.toString().split(" \\(")[0] + ",\n\n" +
                    "You have been assigned a new job.\n\n" +
                    "Job Details:\n" +
                    "- Order ID: " + job.getOrderId() + "\n" +
                    "- Description: " + job.getJobDescription() + "\n" +
                    "- Status: " + job.getStatus() + "\n\n" +
                    "Please log in to the system for more details.\n\n" +
                    "Best regards,\n" +
                    "CarCare Management System";
                
                // Run email sending in background thread
                new Thread(() -> {
                    SendMail.send(employee.getEmail(), subject, message);
                }).start();
            }
        } catch (Exception e) {
            // Log but don't fail the job creation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JobsView view = new JobsView();
            view.setVisible(true);
        });
    }
}

