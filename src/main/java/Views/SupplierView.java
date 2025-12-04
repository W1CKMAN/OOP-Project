package Views;

import DAO.Impl.SupplierDAOImpl;
import DAO.SupplierDAO;
import Models.Supplier;
import Utils.ToastNotification;
import Utils.ValidationUtil;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/**
 * Modern Supplier Management View with CRUD operations.
 */
public class SupplierView extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(SupplierView.class);

    // Form fields
    private JTextField supplierIdField;
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JTextField searchField;

    // Table
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;

    // DAO
    private final SupplierDAO supplierDAO;

    // Currently selected supplier
    private Supplier selectedSupplier;

    public SupplierView() {
        this.supplierDAO = new SupplierDAOImpl();
        initializeUI();
        loadSuppliers();
    }

    private void initializeUI() {
        setTitle("Supplier Management - CarCare");
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[350!][grow]", "[grow]"));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Left panel - Form
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, "grow");

        // Right panel - Table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, "grow");

        setContentPane(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[]15[]15[]15[]15[]15[]15[]15[]push[]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Title
        JLabel titleLabel = new JLabel("Supplier Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        panel.add(titleLabel, "wrap, gapbottom 15");

        // Supplier ID (read-only)
        panel.add(createLabel("Supplier ID"), "wrap");
        supplierIdField = createTextField("Auto-generated");
        supplierIdField.setEditable(false);
        supplierIdField.setBackground(new Color(241, 245, 249));
        panel.add(supplierIdField, "growx, wrap");

        // Name
        panel.add(createLabel("Company Name *"), "wrap");
        nameField = createTextField("Enter company name");
        panel.add(nameField, "growx, wrap");

        // Contact Person
        panel.add(createLabel("Contact Person"), "wrap");
        contactPersonField = createTextField("Enter contact person name");
        panel.add(contactPersonField, "growx, wrap");

        // Phone
        panel.add(createLabel("Phone *"), "wrap");
        phoneField = createTextField("Enter phone number");
        panel.add(phoneField, "growx, wrap");

        // Email
        panel.add(createLabel("Email"), "wrap");
        emailField = createTextField("Enter email address");
        panel.add(emailField, "growx, wrap");

        // Address
        panel.add(createLabel("Address"), "wrap");
        addressField = createTextField("Enter full address");
        panel.add(addressField, "growx, wrap");

        // Category
        panel.add(createLabel("Category"), "wrap");
        categoryCombo = new JComboBox<>(new String[]{"Parts", "Tools", "Consumables", "Electronics", "Accessories", "Other"});
        categoryCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        panel.add(categoryCombo, "growx, wrap");

        // Status
        panel.add(createLabel("Status"), "wrap");
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Blacklisted"});
        statusCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        panel.add(statusCombo, "growx, wrap");

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[]10[]"));
        buttonsPanel.setOpaque(false);

        addButton = createButton("Add Supplier", new Color(16, 185, 129));
        updateButton = createButton("Update", new Color(59, 130, 246));
        deleteButton = createButton("Delete", new Color(239, 68, 68));
        clearButton = createButton("Clear", new Color(100, 116, 139));

        buttonsPanel.add(addButton, "grow");
        buttonsPanel.add(updateButton, "grow, wrap");
        buttonsPanel.add(deleteButton, "grow");
        buttonsPanel.add(clearButton, "grow");

        panel.add(buttonsPanel, "growx");

        // Add action listeners
        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        clearButton.addActionListener(e -> clearForm());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[]15[grow]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Header with search
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 0", "[]push[][]", "[]"));
        headerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Suppliers List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(tableTitle);

        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Search suppliers...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        headerPanel.add(searchField);

        refreshButton = new JButton("🔄 Refresh");
        refreshButton.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        refreshButton.addActionListener(e -> loadSuppliers());
        headerPanel.add(refreshButton);

        panel.add(headerPanel, "growx, wrap");

        // Table
        String[] columns = {"ID", "Name", "Contact Person", "Phone", "Email", "Category", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(40);
        supplierTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        supplierTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        supplierTable.getTableHeader().setBackground(new Color(241, 245, 249));
        supplierTable.getTableHeader().setForeground(new Color(30, 41, 59));
        supplierTable.setSelectionBackground(new Color(219, 234, 254));
        supplierTable.setSelectionForeground(new Color(30, 41, 59));
        supplierTable.setShowGrid(false);
        supplierTable.setIntercellSpacing(new Dimension(0, 0));
        supplierTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false");

        // Column widths
        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        supplierTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        supplierTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        supplierTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Table row sorter
        sorter = new TableRowSorter<>(tableModel);
        supplierTable.setRowSorter(sorter);

        // Selection listener
        supplierTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = supplierTable.getSelectedRow();
                if (row >= 0) {
                    row = supplierTable.convertRowIndexToModel(row);
                    populateForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, "grow");

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        field.setPreferredSize(new Dimension(0, 38));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(0, 40));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        return button;
    }

    private void loadSuppliers() {
        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Supplier> doInBackground() {
                return supplierDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> suppliers = get();
                    tableModel.setRowCount(0);
                    for (Supplier supplier : suppliers) {
                        tableModel.addRow(new Object[]{
                            supplier.getSupplierId(),
                            supplier.getName(),
                            supplier.getContactPerson(),
                            supplier.getPhone(),
                            supplier.getEmail(),
                            supplier.getCategory(),
                            supplier.getStatus()
                        });
                    }
                    logger.info("Loaded {} suppliers", suppliers.size());
                } catch (Exception e) {
                    logger.error("Error loading suppliers", e);
                    ToastNotification.show(SupplierView.this, "Error loading suppliers", ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void populateForm(int modelRow) {
        try {
            int id = (int) tableModel.getValueAt(modelRow, 0);
            Optional<Supplier> supplierOpt = supplierDAO.findById(id);
            
            supplierOpt.ifPresent(supplier -> {
                selectedSupplier = supplier;
                supplierIdField.setText(String.valueOf(supplier.getSupplierId()));
                nameField.setText(supplier.getName());
                contactPersonField.setText(supplier.getContactPerson() != null ? supplier.getContactPerson() : "");
                phoneField.setText(supplier.getPhone());
                emailField.setText(supplier.getEmail() != null ? supplier.getEmail() : "");
                addressField.setText(supplier.getAddress() != null ? supplier.getAddress() : "");
                categoryCombo.setSelectedItem(supplier.getCategory());
                statusCombo.setSelectedItem(supplier.getStatus());
            });
        } catch (Exception e) {
            logger.error("Error populating form", e);
        }
    }

    private void addSupplier() {
        if (!validateForm()) return;

        Supplier supplier = new Supplier();
        supplier.setName(nameField.getText().trim());
        supplier.setContactPerson(contactPersonField.getText().trim());
        supplier.setPhone(phoneField.getText().trim());
        supplier.setEmail(emailField.getText().trim());
        supplier.setAddress(addressField.getText().trim());
        supplier.setCategory((String) categoryCombo.getSelectedItem());
        supplier.setStatus((String) statusCombo.getSelectedItem());

        SwingWorker<Supplier, Void> worker = new SwingWorker<>() {
            @Override
            protected Supplier doInBackground() {
                return supplierDAO.save(supplier);
            }

            @Override
            protected void done() {
                try {
                    Supplier saved = get();
                    if (saved != null && saved.getSupplierId() > 0) {
                        ToastNotification.show(SupplierView.this, "Supplier added successfully!", ToastNotification.Type.SUCCESS);
                        clearForm();
                        loadSuppliers();
                    } else {
                        ToastNotification.show(SupplierView.this, "Failed to add supplier", ToastNotification.Type.ERROR);
                    }
                } catch (Exception e) {
                    logger.error("Error adding supplier", e);
                    ToastNotification.show(SupplierView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void updateSupplier() {
        if (selectedSupplier == null) {
            ToastNotification.show(this, "Please select a supplier to update", ToastNotification.Type.WARNING);
            return;
        }

        if (!validateForm()) return;

        selectedSupplier.setName(nameField.getText().trim());
        selectedSupplier.setContactPerson(contactPersonField.getText().trim());
        selectedSupplier.setPhone(phoneField.getText().trim());
        selectedSupplier.setEmail(emailField.getText().trim());
        selectedSupplier.setAddress(addressField.getText().trim());
        selectedSupplier.setCategory((String) categoryCombo.getSelectedItem());
        selectedSupplier.setStatus((String) statusCombo.getSelectedItem());

        SwingWorker<Supplier, Void> worker = new SwingWorker<>() {
            @Override
            protected Supplier doInBackground() {
                return supplierDAO.update(selectedSupplier);
            }

            @Override
            protected void done() {
                try {
                    get();
                    ToastNotification.show(SupplierView.this, "Supplier updated successfully!", ToastNotification.Type.SUCCESS);
                    clearForm();
                    loadSuppliers();
                } catch (Exception e) {
                    logger.error("Error updating supplier", e);
                    ToastNotification.show(SupplierView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void deleteSupplier() {
        if (selectedSupplier == null) {
            ToastNotification.show(this, "Please select a supplier to delete", ToastNotification.Type.WARNING);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete supplier: " + selectedSupplier.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return supplierDAO.deleteById(selectedSupplier.getSupplierId());
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            ToastNotification.show(SupplierView.this, "Supplier deleted successfully!", ToastNotification.Type.SUCCESS);
                            clearForm();
                            loadSuppliers();
                        } else {
                            ToastNotification.show(SupplierView.this, "Failed to delete supplier", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("Error deleting supplier", e);
                        ToastNotification.show(SupplierView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            };
            worker.execute();
        }
    }

    private void clearForm() {
        selectedSupplier = null;
        supplierIdField.setText("Auto-generated");
        nameField.setText("");
        contactPersonField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        categoryCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        supplierTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate name
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            ToastNotification.show(this, "Company name is required", ToastNotification.Type.WARNING);
            nameField.requestFocus();
            return false;
        }

        // Validate phone
        if (!ValidationUtil.isNotEmpty(phoneField.getText())) {
            ToastNotification.show(this, "Phone number is required", ToastNotification.Type.WARNING);
            phoneField.requestFocus();
            return false;
        }

        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            ToastNotification.show(this, "Invalid phone number format", ToastNotification.Type.WARNING);
            phoneField.requestFocus();
            return false;
        }

        // Validate email if provided
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
            ToastNotification.show(this, "Invalid email format", ToastNotification.Type.WARNING);
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}
