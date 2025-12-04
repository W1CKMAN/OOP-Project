package Views;

import Main.Main;
import Services.AuthService;
import Models.User;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern dashboard with sidebar navigation and statistics widgets.
 */
public class CarCareDashboard extends JFrame {
    
    // Navigation buttons
    private JButton orderManagerButton;
    private JButton employeeManagerButton;
    private JButton customerDetailsManagerButton;
    private JButton supplierManagerButton;
    private JButton inventoryManagerButton;
    private JButton jobsManagerButton;
    private JButton reportsButton;
    private JButton settingsButton;
    private JButton logoutButton;
    private JToggleButton themeToggle;
    
    // Main content panels
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JLabel welcomeLabel;
    private JLabel roleLabel;
    
    // Dashboard statistics cards
    private JLabel totalOrdersLabel;
    private JLabel pendingJobsLabel;
    private JLabel activeEmployeesLabel;
    private JLabel completedTodayLabel;

    private final AuthService authService;
    private final User currentUser;

    public CarCareDashboard() {
        this.authService = AuthService.getInstance();
        this.currentUser = authService.getCurrentUser();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("CarCare Management System - Dashboard");
        setSize(1400, 850);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout with sidebar
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create components
        createSidebar();
        createHeader();
        createContent();

        // Assemble layout
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(headerPanel, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        
        // Apply role-based visibility
        applyRoleBasedAccess();
    }

    private void createSidebar() {
        sidebarPanel = new JPanel(new MigLayout("fill, insets 0", "[220!]", "[]0[]0[]0[]0[]0[]0[]0[]push[]0[]"));
        sidebarPanel.setBackground(new Color(30, 41, 59));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));

        // Logo section
        JPanel logoPanel = new JPanel(new MigLayout("fill, insets 20", "[center]", "[]"));
        logoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🚗 CarCare");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel, "wrap");
        
        JLabel versionLabel = new JLabel("v2.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(148, 163, 184));
        logoPanel.add(versionLabel);
        
        sidebarPanel.add(logoPanel, "growx, wrap, gapbottom 20");

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sidebarPanel.add(sep, "growx, wrap, gapbottom 10");

        // Navigation buttons
        orderManagerButton = createNavButton("📋 Orders", "Manage customer orders");
        jobsManagerButton = createNavButton("🔧 Jobs", "Manage repair jobs");
        customerDetailsManagerButton = createNavButton("👥 Customers", "Manage customers");
        employeeManagerButton = createNavButton("👨‍🔧 Employees", "Manage staff");
        supplierManagerButton = createNavButton("🏭 Suppliers", "Manage suppliers");
        inventoryManagerButton = createNavButton("📦 Inventory", "Stock management");
        reportsButton = createNavButton("📊 Reports", "View analytics");
        
        sidebarPanel.add(orderManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(jobsManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(customerDetailsManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(employeeManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(supplierManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(inventoryManagerButton, "growx, h 45!, wrap");
        sidebarPanel.add(reportsButton, "growx, h 45!, wrap");

        // Bottom section
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(51, 65, 85));
        sidebarPanel.add(sep2, "growx, wrap, gaptop 10");

        // Theme toggle
        themeToggle = new JToggleButton("🌙 Dark Mode");
        themeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        themeToggle.setForeground(new Color(148, 163, 184));
        themeToggle.setBackground(new Color(30, 41, 59));
        themeToggle.setBorderPainted(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeToggle.setHorizontalAlignment(SwingConstants.LEFT);
        themeToggle.addActionListener(e -> {
            Main.toggleTheme();
            themeToggle.setText(themeToggle.isSelected() ? "☀️ Light Mode" : "🌙 Dark Mode");
        });
        sidebarPanel.add(themeToggle, "growx, h 40!, wrap, gaptop 10");

        settingsButton = createNavButton("⚙️ Settings", "Application settings");
        sidebarPanel.add(settingsButton, "growx, h 45!, wrap");

        logoutButton = createNavButton("🚪 Logout", "Sign out");
        logoutButton.addActionListener(e -> performLogout());
        sidebarPanel.add(logoutButton, "growx, h 45!, wrap, gapbottom 20");
    }

    private JButton createNavButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(203, 213, 225));
        button.setBackground(new Color(30, 41, 59));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(51, 65, 85));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(30, 41, 59));
            }
        });

        return button;
    }

    private void createHeader() {
        headerPanel = new JPanel(new MigLayout("fill, insets 15 25 15 25", "[]push[]", "[]"));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new MigLayout("insets 0", "[]", "[]0[]"));
        welcomePanel.setOpaque(false);

        String greeting = getGreeting();
        welcomeLabel = new JLabel(greeting + ", " + (currentUser != null ? currentUser.getFullName() : "User"));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(30, 41, 59));
        welcomePanel.add(welcomeLabel, "wrap");

        roleLabel = new JLabel(currentUser != null ? currentUser.getRole().getDisplayName() : "");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(100, 116, 139));
        welcomePanel.add(roleLabel);

        headerPanel.add(welcomePanel);

        // Right side - Quick actions
        JPanel actionsPanel = new JPanel(new MigLayout("insets 0", "[]10[]10[]", "[]"));
        actionsPanel.setOpaque(false);

        JButton refreshButton = createHeaderButton("🔄", "Refresh data");
        JButton notificationsButton = createHeaderButton("🔔", "Notifications");
        JButton profileButton = createHeaderButton("👤", "Profile");

        actionsPanel.add(refreshButton);
        actionsPanel.add(notificationsButton);
        actionsPanel.add(profileButton);

        headerPanel.add(actionsPanel);
    }

    private JButton createHeaderButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(40, 40));
        button.setBackground(new Color(241, 245, 249));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        return button;
    }

    private void createContent() {
        contentPanel = new JPanel(new MigLayout("fill, insets 25", "[grow]", "[]25[grow]"));
        contentPanel.setBackground(new Color(248, 250, 252));

        // Statistics cards row
        JPanel statsPanel = new JPanel(new MigLayout("fill, insets 0", "[25%][25%][25%][25%]", "[]"));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("Total Orders", "0", "📋", new Color(59, 130, 246)), "grow");
        statsPanel.add(createStatCard("Pending Jobs", "0", "⏳", new Color(245, 158, 11)), "grow");
        statsPanel.add(createStatCard("Active Staff", "0", "👨‍🔧", new Color(16, 185, 129)), "grow");
        statsPanel.add(createStatCard("Completed Today", "0", "✅", new Color(139, 92, 246)), "grow");

        contentPanel.add(statsPanel, "growx, wrap");

        // Main content area with quick actions
        JPanel quickActionsPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel quickActionsTitle = new JLabel("Quick Actions");
        quickActionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        quickActionsTitle.setForeground(new Color(30, 41, 59));
        quickActionsPanel.add(quickActionsTitle, "wrap");

        // Quick action buttons
        JPanel buttonsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow][grow]", "[grow][grow]"));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createQuickActionCard("➕ New Order", "Create a new customer order", new Color(59, 130, 246)), "grow");
        buttonsPanel.add(createQuickActionCard("🔧 Assign Job", "Assign job to employee", new Color(16, 185, 129)), "grow");
        buttonsPanel.add(createQuickActionCard("📧 Send Notification", "Notify customers", new Color(245, 158, 11)), "grow, wrap");
        buttonsPanel.add(createQuickActionCard("📊 View Reports", "Monthly analytics", new Color(139, 92, 246)), "grow");
        buttonsPanel.add(createQuickActionCard("📦 Check Inventory", "Stock levels", new Color(236, 72, 153)), "grow");
        buttonsPanel.add(createQuickActionCard("👥 Add Customer", "Register new customer", new Color(20, 184, 166)), "grow");

        quickActionsPanel.add(buttonsPanel, "grow");
        contentPanel.add(quickActionsPanel, "grow");
    }

    private JPanel createStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("fill, insets 20", "[]push[]", "[]10[]"));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Left side
        JPanel textPanel = new JPanel(new MigLayout("insets 0", "[]", "[]5[]"));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 116, 139));
        textPanel.add(titleLabel, "wrap");

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(30, 41, 59));
        textPanel.add(valueLabel);

        card.add(textPanel);

        // Right side - Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(accentColor);
        card.add(iconLabel);

        // Store reference for updates
        if (title.equals("Total Orders")) totalOrdersLabel = valueLabel;
        else if (title.equals("Pending Jobs")) pendingJobsLabel = valueLabel;
        else if (title.equals("Active Staff")) activeEmployeesLabel = valueLabel;
        else if (title.equals("Completed Today")) completedTodayLabel = valueLabel;

        return card;
    }

    private JPanel createQuickActionCard(String title, String description, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("fill, insets 20", "[]", "[]10[]"));
        card.setBackground(new Color(248, 250, 252));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        card.add(titleLabel, "wrap");

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 116, 139));
        card.add(descLabel);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(accentColor);
                titleLabel.setForeground(Color.WHITE);
                descLabel.setForeground(new Color(255, 255, 255, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(248, 250, 252));
                titleLabel.setForeground(accentColor);
                descLabel.setForeground(new Color(100, 116, 139));
            }
        });

        return card;
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good Morning";
        else if (hour < 17) return "Good Afternoon";
        else return "Good Evening";
    }

    private void applyRoleBasedAccess() {
        if (currentUser == null) return;

        // Hide admin-only features for non-admins
        if (!currentUser.canManageUsers()) {
            settingsButton.setVisible(false);
        }

        if (!currentUser.canViewReports()) {
            reportsButton.setVisible(false);
        }

        if (!currentUser.canManageEmployees()) {
            employeeManagerButton.setEnabled(false);
            employeeManagerButton.setToolTipText("You don't have permission to manage employees");
        }

        if (!currentUser.canManageSuppliers()) {
            supplierManagerButton.setEnabled(false);
            supplierManagerButton.setToolTipText("You don't have permission to manage suppliers");
        }

        if (!currentUser.canManageInventory()) {
            inventoryManagerButton.setEnabled(false);
            inventoryManagerButton.setToolTipText("You don't have permission to manage inventory");
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            // Show login screen again
            SwingUtilities.invokeLater(() -> {
                loginView login = new loginView();
                login.setOnLoginSuccess(() -> {
                    CarCareDashboard dashboard = new CarCareDashboard();
                    new Controllers.CarCareDashboardController(dashboard);
                });
                login.setVisible(true);
            });
        }
    }

    // Update statistics
    public void updateStatistics(int totalOrders, int pendingJobs, int activeEmployees, int completedToday) {
        if (totalOrdersLabel != null) totalOrdersLabel.setText(String.valueOf(totalOrders));
        if (pendingJobsLabel != null) pendingJobsLabel.setText(String.valueOf(pendingJobs));
        if (activeEmployeesLabel != null) activeEmployeesLabel.setText(String.valueOf(activeEmployees));
        if (completedTodayLabel != null) completedTodayLabel.setText(String.valueOf(completedToday));
    }

    // Button listener methods
    public void addOrderManagerButtonListener(ActionListener listener) {
        orderManagerButton.addActionListener(listener);
    }

    public void addEmployeeManagerButtonListener(ActionListener listener) {
        employeeManagerButton.addActionListener(listener);
    }

    public void addcustomerDetailsManagerButtonListener(ActionListener listener) {
        customerDetailsManagerButton.addActionListener(listener);
    }

    public void addSupplierManagerButtonListener(ActionListener listener) {
        supplierManagerButton.addActionListener(listener);
    }

    public void addInventoryManagerButtonListener(ActionListener listener) {
        inventoryManagerButton.addActionListener(listener);
    }

    public void addJobsManagerButtonListener(ActionListener listener) {
        jobsManagerButton.addActionListener(listener);
    }

    public void addReportsButtonListener(ActionListener listener) {
        reportsButton.addActionListener(listener);
    }
}