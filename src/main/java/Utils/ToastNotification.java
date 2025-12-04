package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Toast notification utility for showing non-blocking notifications.
 */
public class ToastNotification extends JWindow {
    
    public enum Type {
        SUCCESS(new Color(16, 185, 129), "✓"),
        ERROR(new Color(239, 68, 68), "✕"),
        WARNING(new Color(245, 158, 11), "⚠"),
        INFO(new Color(59, 130, 246), "ℹ");

        private final Color color;
        private final String icon;

        Type(Color color, String icon) {
            this.color = color;
            this.icon = icon;
        }

        public Color getColor() {
            return color;
        }

        public String getIcon() {
            return icon;
        }
    }

    private static final int TOAST_WIDTH = 350;
    private static final int TOAST_HEIGHT = 60;
    private static final int DISPLAY_TIME = 3000;
    private static final int FADE_TIME = 300;
    private static final int ARC = 15;

    private final Timer fadeTimer;
    private float opacity = 1.0f;

    public ToastNotification(Component parent, String message, Type type) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        // Icon
        JLabel iconLabel = new JLabel(type.getIcon());
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(type.getColor());
        contentPanel.add(iconLabel, BorderLayout.WEST);

        // Message
        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(30, 41, 59));
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Color accent bar
        JPanel accentBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(type.getColor());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(4, TOAST_HEIGHT));
        contentPanel.add(accentBar, BorderLayout.EAST);

        add(contentPanel);

        // Set size and position
        setSize(TOAST_WIDTH, TOAST_HEIGHT);
        
        // Position at bottom-right of parent or screen
        Point location = calculatePosition(parent);
        setLocation(location);

        // Make window rounded
        try {
            setShape(new RoundRectangle2D.Float(0, 0, TOAST_WIDTH, TOAST_HEIGHT, ARC, ARC));
        } catch (Exception e) {
            // Shape not supported on all platforms
        }

        // Fade out timer
        fadeTimer = new Timer(FADE_TIME / 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0) {
                    fadeTimer.stop();
                    dispose();
                } else {
                    setOpacity(opacity);
                }
            }
        });
    }

    private Point calculatePosition(Component parent) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x, y;

        if (parent != null && parent.isVisible()) {
            Point parentLocation = parent.getLocationOnScreen();
            x = parentLocation.x + parent.getWidth() - TOAST_WIDTH - 20;
            y = parentLocation.y + parent.getHeight() - TOAST_HEIGHT - 40;
        } else {
            x = screenSize.width - TOAST_WIDTH - 20;
            y = screenSize.height - TOAST_HEIGHT - 80;
        }

        return new Point(x, y);
    }

    /**
     * Show the toast notification
     */
    public void display() {
        setVisible(true);

        // Start fade out after display time
        Timer displayTimer = new Timer(DISPLAY_TIME, e -> fadeTimer.start());
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    // Static helper methods

    /**
     * Show toast with specified type
     */
    public static void show(Component parent, String message, Type type) {
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification(parent, message, type);
            toast.display();
        });
    }

    /**
     * Show success toast
     */
    public static void success(Component parent, String message) {
        show(parent, message, Type.SUCCESS);
    }

    /**
     * Show error toast
     */
    public static void error(Component parent, String message) {
        show(parent, message, Type.ERROR);
    }

    /**
     * Show warning toast
     */
    public static void warning(Component parent, String message) {
        show(parent, message, Type.WARNING);
    }

    /**
     * Show info toast
     */
    public static void info(Component parent, String message) {
        show(parent, message, Type.INFO);
    }
}
