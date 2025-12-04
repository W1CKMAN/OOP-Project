package Views;

import Services.AuthService;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern login view with FlatLaf styling.
 */
public class loginView extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(loginView.class);
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JCheckBox rememberMeCheckbox;
    private final AuthService authService;
    private Runnable onLoginSuccess;

    public loginView() {
        this.authService = AuthService.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("CarCare - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 40", "[grow]", "[]30[]10[]10[]20[]10[]"));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Logo/Title section
        JLabel logoLabel = new JLabel("🚗", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        mainPanel.add(logoLabel, "align center, wrap");

        JLabel titleLabel = new JLabel("CarCare", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));
        mainPanel.add(titleLabel, "align center, wrap");

        JLabel subtitleLabel = new JLabel("Vehicle Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        mainPanel.add(subtitleLabel, "align center, wrap 30");

        // Login form panel
        JPanel formPanel = new JPanel(new MigLayout("fillx, insets 25", "[grow]", "[]10[]15[]10[]15[]10[]"));
        formPanel.setBackground(Color.WHITE);
        formPanel.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 15; " +
            "background: #ffffff; " +
            "[light]border: 1, 1, 1, 1, #e0e0e0, 1, 15"
        );

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(73, 80, 87));
        formPanel.add(usernameLabel, "wrap");

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        usernameField.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 8; " +
            "borderWidth: 1; " +
            "focusWidth: 2; " +
            "focusColor: #4dabf7"
        );
        formPanel.add(usernameField, "growx, h 40!, wrap");

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(73, 80, 87));
        formPanel.add(passwordLabel, "wrap");

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordField.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 8; " +
            "borderWidth: 1; " +
            "focusWidth: 2; " +
            "focusColor: #4dabf7; " +
            "showRevealButton: true"
        );
        formPanel.add(passwordField, "growx, h 40!, wrap");

        // Remember me checkbox
        rememberMeCheckbox = new JCheckBox("Remember me");
        rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckbox.setForeground(new Color(108, 117, 125));
        rememberMeCheckbox.setBackground(Color.WHITE);
        formPanel.add(rememberMeCheckbox, "wrap 15");

        // Login button
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(51, 122, 183));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 8; " +
            "borderWidth: 0; " +
            "focusWidth: 0; " +
            "hoverBackground: #2b7ac7; " +
            "pressedBackground: #245a8c"
        );
        formPanel.add(loginButton, "growx, h 42!, wrap");

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 53, 69));
        formPanel.add(statusLabel, "growx, wrap");

        mainPanel.add(formPanel, "growx, wrap");

        // Footer
        JLabel footerLabel = new JLabel("© 2024 CarCare Management System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(173, 181, 189));
        mainPanel.add(footerLabel, "align center, gaptop 20");

        setContentPane(mainPanel);

        // Add listeners
        setupListeners();
    }

    private void setupListeners() {
        // Login button action
        loginButton.addActionListener(this::performLogin);

        // Enter key on password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin(null);
                }
            }
        });

        // Enter key on username field moves to password
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        statusLabel.setText(" ");

        // Perform login in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        logger.info("Login successful for user: {}", username);
                        dispose();
                        if (onLoginSuccess != null) {
                            onLoginSuccess.run();
                        }
                    } else {
                        showError("Invalid username or password");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    logger.error("Login error", ex);
                    showError("An error occurred. Please try again.");
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(220, 53, 69));
        
        // Shake animation
        Timer timer = new Timer(50, null);
        final int[] count = {0};
        final int originalX = getX();
        timer.addActionListener(evt -> {
            count[0]++;
            if (count[0] <= 6) {
                setLocation(originalX + (count[0] % 2 == 0 ? 5 : -5), getY());
            } else {
                setLocation(originalX, getY());
                timer.stop();
            }
        });
        timer.start();
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new loginView().setVisible(true);
        });
    }
}