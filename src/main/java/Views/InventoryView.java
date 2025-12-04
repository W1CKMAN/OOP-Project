package Views;

import DAO.Impl.InventoryDAOImpl;
import DAO.Impl.SupplierDAOImpl;
import DAO.InventoryDAO;
import DAO.SupplierDAO;
import Models.Inventory;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Modern Inventory Management View with CRUD operations.
 */
public class InventoryView extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(InventoryView.class);

    // Form fields
    private JTextField itemIdField;
    private JTextField productNameField;
    private JTextField skuField;
    private JComboBox<String> categoryCombo;
    private JTextField quantityField;
    private JTextField unitField;
    private JTextField unitPriceField;
    private JTextField reorderLevelField;
    private JComboBox<String> supplierCombo;
    private JTextArea descriptionArea;
    private JTextField searchField;

    // Table
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton lowStockButton;

    // DAOs
    private final InventoryDAO inventoryDAO;
    private final SupplierDAO supplierDAO;

    // Supplier mapping
    private Map<String, Integer> supplierMap = new HashMap<>();

    // Currently selected item
    private Inventory selectedItem;

    public InventoryView() {
        this.inventoryDAO = new InventoryDAOImpl();
        this.supplierDAO = new SupplierDAOImpl();
        initializeUI();
        loadSuppliers();
        loadInventory();
    }

    private void initializeUI() {
        setTitle("Inventory Management - CarCare");
        setSize(1300, 750);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[380!][grow]", "[grow]"));
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
        JPanel panel = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[]10[]10[]10[]10[]10[]10[]10[]10[]push[]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Title
        JLabel titleLabel = new JLabel("Item Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        panel.add(titleLabel, "wrap, gapbottom 10");

        // Item ID (read-only)
        panel.add(createLabel("Item ID"), "wrap");
        itemIdField = createTextField("Auto-generated");
        itemIdField.setEditable(false);
        itemIdField.setBackground(new Color(241, 245, 249));
        panel.add(itemIdField, "growx, wrap");

        // Product Name
        panel.add(createLabel("Product Name *"), "wrap");
        productNameField = createTextField("Enter product name");
        panel.add(productNameField, "growx, wrap");

        // SKU
        panel.add(createLabel("SKU / Part Number"), "wrap");
        skuField = createTextField("Enter SKU or part number");
        panel.add(skuField, "growx, wrap");

        // Two-column layout for category and supplier
        JPanel twoColPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[][]"));
        twoColPanel.setOpaque(false);

        // Category
        twoColPanel.add(createLabel("Category"), "wrap 0, span 1");
        twoColPanel.add(createLabel("Supplier"), "wrap");
        
        categoryCombo = new JComboBox<>(new String[]{"Parts", "Tools", "Consumables", "Electronics", "Accessories", "Lubricants", "Filters", "Other"});
        categoryCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        twoColPanel.add(categoryCombo, "growx");

        supplierCombo = new JComboBox<>();
        supplierCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        twoColPanel.add(supplierCombo, "growx");

        panel.add(twoColPanel, "growx, wrap");

        // Two-column layout for quantity and unit
        JPanel qtyUnitPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[][]"));
        qtyUnitPanel.setOpaque(false);

        qtyUnitPanel.add(createLabel("Quantity *"), "wrap 0, span 1");
        qtyUnitPanel.add(createLabel("Unit"), "wrap");

        quantityField = createTextField("0");
        qtyUnitPanel.add(quantityField, "growx");

        unitField = createTextField("pcs, liters, etc.");
        qtyUnitPanel.add(unitField, "growx");

        panel.add(qtyUnitPanel, "growx, wrap");

        // Two-column layout for price and reorder level
        JPanel priceReorderPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[][]"));
        priceReorderPanel.setOpaque(false);

        priceReorderPanel.add(createLabel("Unit Price ($)"), "wrap 0, span 1");
        priceReorderPanel.add(createLabel("Reorder Level"), "wrap");

        unitPriceField = createTextField("0.00");
        priceReorderPanel.add(unitPriceField, "growx");

        reorderLevelField = createTextField("Minimum stock alert");
        priceReorderPanel.add(reorderLevelField, "growx");

        panel.add(priceReorderPanel, "growx, wrap");

        // Description
        panel.add(createLabel("Description"), "wrap");
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        panel.add(descScrollPane, "growx, h 70!, wrap");

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow]", "[]10[]"));
        buttonsPanel.setOpaque(false);

        addButton = createButton("Add Item", new Color(16, 185, 129));
        updateButton = createButton("Update", new Color(59, 130, 246));
        deleteButton = createButton("Delete", new Color(239, 68, 68));
        clearButton = createButton("Clear", new Color(100, 116, 139));

        buttonsPanel.add(addButton, "grow");
        buttonsPanel.add(updateButton, "grow, wrap");
        buttonsPanel.add(deleteButton, "grow");
        buttonsPanel.add(clearButton, "grow");

        panel.add(buttonsPanel, "growx");

        // Add action listeners
        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
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
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 0", "[]push[][][]", "[]"));
        headerPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Inventory Items");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(tableTitle);

        searchField = new JTextField(18);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍 Search items...");
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        headerPanel.add(searchField);

        lowStockButton = new JButton("⚠️ Low Stock");
        lowStockButton.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        lowStockButton.setBackground(new Color(245, 158, 11));
        lowStockButton.setForeground(Color.WHITE);
        lowStockButton.addActionListener(e -> showLowStockItems());
        headerPanel.add(lowStockButton);

        refreshButton = new JButton("🔄 Refresh");
        refreshButton.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        refreshButton.addActionListener(e -> loadInventory());
        headerPanel.add(refreshButton);

        panel.add(headerPanel, "growx, wrap");

        // Table
        String[] columns = {"ID", "Name", "SKU", "Category", "Qty", "Unit", "Price", "Reorder Lvl", "Supplier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(40);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        inventoryTable.getTableHeader().setBackground(new Color(241, 245, 249));
        inventoryTable.getTableHeader().setForeground(new Color(30, 41, 59));
        inventoryTable.setSelectionBackground(new Color(219, 234, 254));
        inventoryTable.setSelectionForeground(new Color(30, 41, 59));
        inventoryTable.setShowGrid(false);
        inventoryTable.setIntercellSpacing(new Dimension(0, 0));
        inventoryTable.putClientProperty(FlatClientProperties.STYLE, "showHorizontalLines: true; showVerticalLines: false");

        // Column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Table row sorter
        sorter = new TableRowSorter<>(tableModel);
        inventoryTable.setRowSorter(sorter);

        // Selection listener
        inventoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = inventoryTable.getSelectedRow();
                if (row >= 0) {
                    row = inventoryTable.convertRowIndexToModel(row);
                    populateForm(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
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
        field.setPreferredSize(new Dimension(0, 36));
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
                return supplierDAO.findByStatus("Active");
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> suppliers = get();
                    supplierCombo.removeAllItems();
                    supplierCombo.addItem("-- Select Supplier --");
                    supplierMap.clear();
                    
                    for (Supplier supplier : suppliers) {
                        String displayName = supplier.getName();
                        supplierCombo.addItem(displayName);
                        supplierMap.put(displayName, supplier.getSupplierId());
                    }
                    logger.info("Loaded {} active suppliers", suppliers.size());
                } catch (Exception e) {
                    logger.error("Error loading suppliers", e);
                }
            }
        };
        worker.execute();
    }

    private void loadInventory() {
        SwingWorker<List<Inventory>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Inventory> doInBackground() {
                return inventoryDAO.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Inventory> items = get();
                    tableModel.setRowCount(0);
                    for (Inventory item : items) {
                        tableModel.addRow(new Object[]{
                            item.getItemId(),
                            item.getProductName(),
                            item.getSku(),
                            item.getCategory(),
                            item.getQuantity(),
                            item.getUnit(),
                            String.format("$%.2f", item.getUnitPrice()),
                            item.getReorderLevel(),
                            item.getSupplierName()
                        });
                    }
                    logger.info("Loaded {} inventory items", items.size());
                } catch (Exception e) {
                    logger.error("Error loading inventory", e);
                    ToastNotification.show(InventoryView.this, "Error loading inventory", ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void showLowStockItems() {
        SwingWorker<List<Inventory>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Inventory> doInBackground() {
                return inventoryDAO.findLowStock();
            }

            @Override
            protected void done() {
                try {
                    List<Inventory> items = get();
                    tableModel.setRowCount(0);
                    for (Inventory item : items) {
                        tableModel.addRow(new Object[]{
                            item.getItemId(),
                            item.getProductName(),
                            item.getSku(),
                            item.getCategory(),
                            item.getQuantity(),
                            item.getUnit(),
                            String.format("$%.2f", item.getUnitPrice()),
                            item.getReorderLevel(),
                            item.getSupplierName()
                        });
                    }
                    
                    if (items.isEmpty()) {
                        ToastNotification.show(InventoryView.this, "No low stock items found!", ToastNotification.Type.SUCCESS);
                    } else {
                        ToastNotification.show(InventoryView.this, items.size() + " items need restocking", ToastNotification.Type.WARNING);
                    }
                } catch (Exception e) {
                    logger.error("Error loading low stock items", e);
                }
            }
        };
        worker.execute();
    }

    private void populateForm(int modelRow) {
        try {
            int id = (int) tableModel.getValueAt(modelRow, 0);
            Optional<Inventory> itemOpt = inventoryDAO.findById(id);
            
            itemOpt.ifPresent(item -> {
                selectedItem = item;
                itemIdField.setText(String.valueOf(item.getItemId()));
                productNameField.setText(item.getProductName());
                skuField.setText(item.getSku() != null ? item.getSku() : "");
                categoryCombo.setSelectedItem(item.getCategory());
                quantityField.setText(String.valueOf(item.getQuantity()));
                unitField.setText(item.getUnit() != null ? item.getUnit() : "");
                unitPriceField.setText(String.format("%.2f", item.getUnitPrice()));
                reorderLevelField.setText(String.valueOf(item.getReorderLevel()));
                descriptionArea.setText(item.getDescription() != null ? item.getDescription() : "");
                
                // Set supplier
                if (item.getSupplierName() != null) {
                    supplierCombo.setSelectedItem(item.getSupplierName());
                } else {
                    supplierCombo.setSelectedIndex(0);
                }
            });
        } catch (Exception e) {
            logger.error("Error populating form", e);
        }
    }

    private void addItem() {
        if (!validateForm()) return;

        Inventory item = new Inventory();
        item.setProductName(productNameField.getText().trim());
        item.setSku(skuField.getText().trim());
        item.setCategory((String) categoryCombo.getSelectedItem());
        item.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        item.setUnit(unitField.getText().trim());
        item.setUnitPrice(new BigDecimal(unitPriceField.getText().trim()));
        item.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim().isEmpty() ? "0" : reorderLevelField.getText().trim()));
        item.setDescription(descriptionArea.getText().trim());
        
        String selectedSupplier = (String) supplierCombo.getSelectedItem();
        if (supplierMap.containsKey(selectedSupplier)) {
            item.setSupplierId(supplierMap.get(selectedSupplier));
        }

        SwingWorker<Inventory, Void> worker = new SwingWorker<>() {
            @Override
            protected Inventory doInBackground() {
                return inventoryDAO.save(item);
            }

            @Override
            protected void done() {
                try {
                    Inventory saved = get();
                    if (saved != null && saved.getItemId() > 0) {
                        ToastNotification.show(InventoryView.this, "Item added successfully!", ToastNotification.Type.SUCCESS);
                        clearForm();
                        loadInventory();
                    } else {
                        ToastNotification.show(InventoryView.this, "Failed to add item", ToastNotification.Type.ERROR);
                    }
                } catch (Exception e) {
                    logger.error("Error adding item", e);
                    ToastNotification.show(InventoryView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void updateItem() {
        if (selectedItem == null) {
            ToastNotification.show(this, "Please select an item to update", ToastNotification.Type.WARNING);
            return;
        }

        if (!validateForm()) return;

        selectedItem.setProductName(productNameField.getText().trim());
        selectedItem.setSku(skuField.getText().trim());
        selectedItem.setCategory((String) categoryCombo.getSelectedItem());
        selectedItem.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        selectedItem.setUnit(unitField.getText().trim());
        selectedItem.setUnitPrice(new BigDecimal(unitPriceField.getText().trim()));
        selectedItem.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim().isEmpty() ? "0" : reorderLevelField.getText().trim()));
        selectedItem.setDescription(descriptionArea.getText().trim());
        
        String selectedSupplier = (String) supplierCombo.getSelectedItem();
        if (supplierMap.containsKey(selectedSupplier)) {
            selectedItem.setSupplierId(supplierMap.get(selectedSupplier));
        }

        SwingWorker<Inventory, Void> worker = new SwingWorker<>() {
            @Override
            protected Inventory doInBackground() {
                return inventoryDAO.update(selectedItem);
            }

            @Override
            protected void done() {
                try {
                    get();
                    ToastNotification.show(InventoryView.this, "Item updated successfully!", ToastNotification.Type.SUCCESS);
                    clearForm();
                    loadInventory();
                } catch (Exception e) {
                    logger.error("Error updating item", e);
                    ToastNotification.show(InventoryView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void deleteItem() {
        if (selectedItem == null) {
            ToastNotification.show(this, "Please select an item to delete", ToastNotification.Type.WARNING);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete item: " + selectedItem.getProductName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return inventoryDAO.deleteById(selectedItem.getItemId());
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            ToastNotification.show(InventoryView.this, "Item deleted successfully!", ToastNotification.Type.SUCCESS);
                            clearForm();
                            loadInventory();
                        } else {
                            ToastNotification.show(InventoryView.this, "Failed to delete item", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("Error deleting item", e);
                        ToastNotification.show(InventoryView.this, "Error: " + e.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            };
            worker.execute();
        }
    }

    private void clearForm() {
        selectedItem = null;
        itemIdField.setText("Auto-generated");
        productNameField.setText("");
        skuField.setText("");
        categoryCombo.setSelectedIndex(0);
        quantityField.setText("");
        unitField.setText("");
        unitPriceField.setText("");
        reorderLevelField.setText("");
        descriptionArea.setText("");
        supplierCombo.setSelectedIndex(0);
        inventoryTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate product name
        if (!ValidationUtil.isNotEmpty(productNameField.getText())) {
            ToastNotification.show(this, "Product name is required", ToastNotification.Type.WARNING);
            productNameField.requestFocus();
            return false;
        }

        // Validate quantity
        if (!ValidationUtil.isNotEmpty(quantityField.getText())) {
            ToastNotification.show(this, "Quantity is required", ToastNotification.Type.WARNING);
            quantityField.requestFocus();
            return false;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty < 0) {
                ToastNotification.show(this, "Quantity cannot be negative", ToastNotification.Type.WARNING);
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            ToastNotification.show(this, "Invalid quantity format", ToastNotification.Type.WARNING);
            quantityField.requestFocus();
            return false;
        }

        // Validate price if provided
        String priceText = unitPriceField.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    ToastNotification.show(this, "Price cannot be negative", ToastNotification.Type.WARNING);
                    unitPriceField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                ToastNotification.show(this, "Invalid price format", ToastNotification.Type.WARNING);
                unitPriceField.requestFocus();
                return false;
            }
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
