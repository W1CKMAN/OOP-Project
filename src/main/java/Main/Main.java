package Main;

import Config.ConfigManager;
import Controllers.CarCareDashboardController;
import DatabaseConnection.ConnectionPool;
import Services.AuthService;
import Views.CarCareDashboard;
import Views.loginView;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for CarCare Management System.
 * Initializes the application with modern UI theme and authentication.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Initialize configuration
        ConfigManager config = ConfigManager.getInstance();
        logger.info("Starting {} v{}", config.getAppName(), config.getAppVersion());

        // Set up FlatLaf Look and Feel
        setupLookAndFeel(config);

        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize connection pool
                ConnectionPool.getInstance();
                logger.info("Database connection pool initialized");

                // Ensure default admin exists
                AuthService.getInstance().ensureDefaultAdminExists();

                // Show login screen
                showLoginScreen();

            } catch (Exception e) {
                logger.error("Failed to start application", e);
                showErrorDialog("Failed to start application: " + e.getMessage());
                System.exit(1);
            }
        });

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application...");
            ConnectionPool.getInstance().shutdown();
        }));
    }

    private static void setupLookAndFeel(ConfigManager config) {
        try {
            // Set system properties for better rendering
            System.setProperty("flatlaf.useWindowDecorations", "true");
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            System.setProperty("flatlaf.animation", "true");

            // Apply theme based on config
            String theme = config.getAppTheme();
            if ("dark".equalsIgnoreCase(theme)) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            // Set default font
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

            // Customize UI defaults
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.rowHeight", 35);
            UIManager.put("TabbedPane.showTabSeparators", true);

            logger.info("FlatLaf theme applied: {}", theme);
        } catch (Exception e) {
            logger.error("Failed to set look and feel", e);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                logger.error("Failed to set system look and feel", ex);
            }
        }
    }

    private static void showLoginScreen() {
        loginView login = new loginView();
        login.setOnLoginSuccess(() -> {
            // Show main dashboard after successful login
            SwingUtilities.invokeLater(() -> {
                CarCareDashboard dashboard = new CarCareDashboard();
                new CarCareDashboardController(dashboard);
                logger.info("Dashboard opened for user: {}", 
                    AuthService.getInstance().getCurrentUser().getUsername());
            });
        });
        login.setVisible(true);
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Application Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Toggle between light and dark theme
     */
    public static void toggleTheme() {
        try {
            if (UIManager.getLookAndFeel() instanceof FlatDarkLaf) {
                FlatLightLaf.setup();
            } else {
                FlatDarkLaf.setup();
            }
            // Update all windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            logger.info("Theme toggled");
        } catch (Exception e) {
            logger.error("Failed to toggle theme", e);
        }
    }
}