package Views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import DAO.Impl.EmployeeDAOImpl;
import DAO.EmployeeDAO;
import Models.Employee;
import Utils.ToastNotification;
import Utils.ValidationUtil;
import net.miginfocom.swing.MigLayout;

/**
 * Modern Employee Management View with FlatLaf styling
 */
public class EmployeeView extends JDialog {
    
    // DAO
    private final EmployeeDAO employeeDAO;
    
    // UI Components
    private JTextField searchField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField positionField;
    private JTextField salaryField;
    private JComboBox<String> statusComboBox;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    
    // Currently selected employee
    private Employee selectedEmployee;

    public EmployeeView() {
        this.employeeDAO = new EmployeeDAOImpl();
        initializeUI();
        loadEmployees();
    }

    private void initializeUI() {
        setTitle("Employee Management");
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
        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 0", "[350!][grow]", "[grow]"));
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), "growy");
        contentPanel.add(createTablePanel(), "grow");
        
        mainPanel.add(contentPanel, "grow");
        
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 0 0 15 0", "[][grow][]", "[]"));
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, "");
        
        // Spacer
        headerPanel.add(new JLabel(), "growx");
        
        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search employees...");
        searchField.putClientProperty("JTextField.leadingIcon", 
            UIManager.getIcon("TextField.searchIcon"));
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
        JLabel formTitle = new JLabel("Employee Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(30, 41, 59));
        formCard.add(formTitle, "wrap, gapbottom 15");
        
        // Name field
        formCard.add(createLabel("Full Name *"), "wrap, gaptop 5");
        nameField = createTextField("Enter employee name");
        formCard.add(nameField, "growx, wrap");
        
        // Email field
        formCard.add(createLabel("Email Address *"), "wrap, gaptop 10");
        emailField = createTextField("Enter email address");
        formCard.add(emailField, "growx, wrap");
        
        // Phone field
        formCard.add(createLabel("Phone Number *"), "wrap, gaptop 10");
        phoneField = createTextField("Enter phone number");
        formCard.add(phoneField, "growx, wrap");
        
        // Position field
        formCard.add(createLabel("Position *"), "wrap, gaptop 10");
        positionField = createTextField("Enter position/role");
        formCard.add(positionField, "growx, wrap");
        
        // Salary field
        formCard.add(createLabel("Salary"), "wrap, gaptop 10");
        salaryField = createTextField("Enter salary amount");
        formCard.add(salaryField, "growx, wrap");
        
        // Status combo
        formCard.add(createLabel("Status"), "wrap, gaptop 10");
        statusComboBox = new JComboBox<>(new String[]{"Active", "On Leave", "Terminated"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(statusComboBox, "growx, wrap, gapbottom 20");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[][]"));
        buttonsPanel.setOpaque(false);
        
        addButton = createButton("Add Employee", new Color(34, 197, 94));
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
        
        // Table title with count
        JLabel tableTitle = new JLabel("Employee List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(30, 41, 59));
        tableCard.add(tableTitle, "wrap, gapbottom 10");
        
        // Create table
        String[] columns = {"ID", "Name", "Email", "Phone", "Position", "Salary", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.setRowHeight(40);
        employeeTable.setShowHorizontalLines(true);
        employeeTable.setGridColor(new Color(241, 245, 249));
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        employeeTable.getTableHeader().setBackground(new Color(248, 250, 252));
        employeeTable.getTableHeader().setForeground(new Color(71, 85, 105));
        employeeTable.setSelectionBackground(new Color(224, 242, 254));
        employeeTable.setSelectionForeground(new Color(30, 41, 59));
        
        // Set column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Position
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Salary
        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        
        // Row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(rowSorter);
        
        // Selection listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
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
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());
    }

    private void addEmployee() {
        if (!validateForm()) return;
        
        try {
            Employee employee = new Employee();
            employee.setEmployeeName(nameField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setContactNumber(phoneField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            
            String salaryText = salaryField.getText().trim();
            if (!salaryText.isEmpty()) {
                employee.setSalary(Double.parseDouble(salaryText));
            }
            
            employee.setStatus((String) statusComboBox.getSelectedItem());
            
            employeeDAO.save(employee);
            ToastNotification.show(this, "Employee added successfully!", ToastNotification.Type.SUCCESS);
            loadEmployees();
            clearForm();
        } catch (NumberFormatException ex) {
            ToastNotification.show(this, "Invalid salary format", ToastNotification.Type.ERROR);
        } catch (Exception ex) {
            ToastNotification.show(this, "Error adding employee: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void updateEmployee() {
        if (selectedEmployee == null) {
            ToastNotification.show(this, "Please select an employee to update", ToastNotification.Type.WARNING);
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            selectedEmployee.setEmployeeName(nameField.getText().trim());
            selectedEmployee.setEmail(emailField.getText().trim());
            selectedEmployee.setContactNumber(phoneField.getText().trim());
            selectedEmployee.setPosition(positionField.getText().trim());
            
            String salaryText = salaryField.getText().trim();
            if (!salaryText.isEmpty()) {
                selectedEmployee.setSalary(Double.parseDouble(salaryText));
            }
            
            selectedEmployee.setStatus((String) statusComboBox.getSelectedItem());
            
            employeeDAO.update(selectedEmployee);
            ToastNotification.show(this, "Employee updated successfully!", ToastNotification.Type.SUCCESS);
            loadEmployees();
            clearForm();
        } catch (NumberFormatException ex) {
            ToastNotification.show(this, "Invalid salary format", ToastNotification.Type.ERROR);
        } catch (Exception ex) {
            ToastNotification.show(this, "Error updating employee: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void deleteEmployee() {
        if (selectedEmployee == null) {
            ToastNotification.show(this, "Please select an employee to delete", ToastNotification.Type.WARNING);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee: " + selectedEmployee.getEmployeeName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeDAO.deleteById(selectedEmployee.getEmployeeId());
                ToastNotification.show(this, "Employee deleted successfully!", ToastNotification.Type.SUCCESS);
                loadEmployees();
                clearForm();
            } catch (Exception ex) {
                ToastNotification.show(this, "Error deleting employee: " + ex.getMessage(), ToastNotification.Type.ERROR);
            }
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Employee name is required", ToastNotification.Type.WARNING);
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
        
        if (positionField.getText().trim().isEmpty()) {
            ToastNotification.show(this, "Position is required", ToastNotification.Type.WARNING);
            positionField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        
        try {
            List<Employee> employees = employeeDAO.findAll();
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getEmployeeName(),
                    emp.getEmail(),
                    emp.getContactNumber(),
                    emp.getPosition(),
                    emp.getSalary() != null ? String.format("$%.2f", emp.getSalary()) : "-",
                    emp.getStatus() != null ? emp.getStatus() : "Active"
                });
            }
        } catch (Exception ex) {
            ToastNotification.show(this, "Error loading employees: " + ex.getMessage(), ToastNotification.Type.ERROR);
        }
    }

    private void populateFormFromTable(int viewRow) {
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        
        int empId = (Integer) tableModel.getValueAt(modelRow, 0);
        selectedEmployee = employeeDAO.findById(empId).orElse(null);
        
        if (selectedEmployee != null) {
            nameField.setText(selectedEmployee.getEmployeeName());
            emailField.setText(selectedEmployee.getEmail());
            phoneField.setText(selectedEmployee.getContactNumber());
            positionField.setText(selectedEmployee.getPosition());
            salaryField.setText(selectedEmployee.getSalary() != null ? 
                String.valueOf(selectedEmployee.getSalary()) : "");
            statusComboBox.setSelectedItem(selectedEmployee.getStatus() != null ? 
                selectedEmployee.getStatus() : "Active");
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        positionField.setText("");
        salaryField.setText("");
        statusComboBox.setSelectedIndex(0);
        selectedEmployee = null;
        employeeTable.clearSelection();
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
            EmployeeView view = new EmployeeView();
            view.setVisible(true);
        });
    }
}