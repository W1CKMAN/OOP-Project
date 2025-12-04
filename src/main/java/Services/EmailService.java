package Services;

import Models.SendMail;
import Config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email notification service providing pre-built templates and async email handling.
 * Wraps SendMail functionality with business-specific templates.
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final ConfigManager config = ConfigManager.getInstance();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
    
    private static EmailService instance;
    
    private EmailService() {}
    
    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    // ==================== Customer Notifications ====================

    /**
     * Send welcome email to new customer
     */
    public void sendWelcomeEmail(String customerEmail, String customerName) {
        String subject = "Welcome to CarCare!";
        String html = buildHtmlEmail(
            "Welcome to CarCare!",
            "#4CAF50",
            String.format("""
                <p>Dear %s,</p>
                <p>Thank you for registering with CarCare! We're excited to have you as a valued customer.</p>
                <p>With your CarCare account, you can:</p>
                <ul>
                    <li>Track your vehicle service history</li>
                    <li>Receive timely service reminders</li>
                    <li>Get exclusive offers and discounts</li>
                </ul>
                <p>If you have any questions, feel free to contact us!</p>
                """, customerName)
        );
        
        SendMail.sendAsync(customerEmail, subject, html, success -> {
            if (success) {
                logger.info("Welcome email sent to: {}", customerEmail);
            } else {
                logger.warn("Failed to send welcome email to: {}", customerEmail);
            }
        });
    }

    /**
     * Send order confirmation email
     */
    public void sendOrderConfirmation(String customerEmail, String customerName, 
                                       int orderId, String vehicleModel, String vehicleNumber) {
        String subject = "Order Confirmation #" + orderId + " - CarCare";
        String html = buildHtmlEmail(
            "Order Confirmed!",
            "#2196F3",
            String.format("""
                <p>Dear %s,</p>
                <p>Your service order has been confirmed. Here are the details:</p>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 15px 0;">
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0;"><strong>Order ID:</strong></td>
                            <td style="padding: 8px 0;">#%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0;"><strong>Vehicle:</strong></td>
                            <td style="padding: 8px 0;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0;"><strong>License Plate:</strong></td>
                            <td style="padding: 8px 0;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0;"><strong>Date:</strong></td>
                            <td style="padding: 8px 0;">%s</td>
                        </tr>
                    </table>
                </div>
                <p>We'll notify you once your vehicle is ready for pickup.</p>
                """, customerName, orderId, vehicleModel, vehicleNumber, 
                LocalDateTime.now().format(dateFormatter))
        );
        
        SendMail.sendAsync(customerEmail, subject, html);
    }

    /**
     * Send order status update email
     */
    public void sendOrderStatusUpdate(String customerEmail, String customerName,
                                       int orderId, String status, String vehicleNumber) {
        String statusColor = switch (status) {
            case "In Progress" -> "#FF9800";
            case "Completed" -> "#4CAF50";
            case "Cancelled" -> "#f44336";
            default -> "#2196F3";
        };
        
        String subject = "Order #" + orderId + " Status Update - CarCare";
        String html = buildHtmlEmail(
            "Order Status Update",
            statusColor,
            String.format("""
                <p>Dear %s,</p>
                <p>Your order status has been updated:</p>
                <div style="text-align: center; margin: 20px 0;">
                    <span style="background: %s; color: white; padding: 10px 25px; border-radius: 20px; font-weight: bold;">
                        %s
                    </span>
                </div>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 15px 0;">
                    <p><strong>Order ID:</strong> #%d</p>
                    <p><strong>Vehicle:</strong> %s</p>
                </div>
                %s
                """, customerName, statusColor, status.toUpperCase(), orderId, vehicleNumber,
                status.equals("Completed") ? 
                    "<p style='color: #4CAF50;'><strong>Your vehicle is ready for pickup!</strong></p>" : "")
        );
        
        SendMail.sendAsync(customerEmail, subject, html);
    }

    /**
     * Send vehicle ready for pickup notification
     */
    public void sendVehicleReadyNotification(String customerEmail, String customerName, String vehicleNumber) {
        SendMail.sendOrderCompletionNotification(customerEmail, customerName, vehicleNumber);
    }

    // ==================== Employee Notifications ====================

    /**
     * Send job assignment notification
     */
    public void sendJobAssignment(String employeeEmail, String employeeName,
                                   String jobDescription, String vehicleInfo, int orderId) {
        String subject = "New Job Assignment - Order #" + orderId;
        String html = buildHtmlEmail(
            "New Job Assigned",
            "#673AB7",
            String.format("""
                <p>Dear %s,</p>
                <p>You have been assigned a new job. Please review the details below:</p>
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 15px 0;">
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0;"><strong>Order ID:</strong></td>
                            <td style="padding: 8px 0;">#%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0;"><strong>Vehicle:</strong></td>
                            <td style="padding: 8px 0;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0;"><strong>Description:</strong></td>
                            <td style="padding: 8px 0;">%s</td>
                        </tr>
                    </table>
                </div>
                <p>Please log in to the system to view full details and update the job status as you progress.</p>
                """, employeeName, orderId, vehicleInfo, jobDescription)
        );
        
        SendMail.sendAsync(employeeEmail, subject, html);
    }

    /**
     * Send job completion reminder
     */
    public void sendJobReminder(String employeeEmail, String employeeName, int jobId, String jobDescription) {
        String subject = "Job Reminder - #" + jobId;
        String html = buildHtmlEmail(
            "Job Reminder",
            "#FF9800",
            String.format("""
                <p>Dear %s,</p>
                <p>This is a friendly reminder about your pending job:</p>
                <div style="background: #fff3e0; padding: 15px; border-radius: 8px; margin: 15px 0; border-left: 4px solid #FF9800;">
                    <p><strong>Job ID:</strong> #%d</p>
                    <p><strong>Description:</strong> %s</p>
                </div>
                <p>Please update the job status once completed.</p>
                """, employeeName, jobId, jobDescription)
        );
        
        SendMail.sendAsync(employeeEmail, subject, html);
    }

    // ==================== System Notifications ====================

    /**
     * Send low stock alert
     */
    public void sendLowStockAlert(String adminEmail, String itemName, int currentQuantity, int threshold) {
        String subject = "⚠️ Low Stock Alert: " + itemName;
        String html = buildHtmlEmail(
            "Low Stock Alert",
            "#f44336",
            String.format("""
                <p>Attention!</p>
                <p>The following inventory item is running low:</p>
                <div style="background: #ffebee; padding: 15px; border-radius: 8px; margin: 15px 0; border-left: 4px solid #f44336;">
                    <p><strong>Item:</strong> %s</p>
                    <p><strong>Current Quantity:</strong> %d</p>
                    <p><strong>Threshold:</strong> %d</p>
                </div>
                <p>Please restock soon to avoid service delays.</p>
                """, itemName, currentQuantity, threshold)
        );
        
        SendMail.sendAsync(adminEmail, subject, html);
    }

    /**
     * Send daily summary report
     */
    public void sendDailySummary(String adminEmail, int ordersCreated, int ordersCompleted, 
                                  int pendingJobs, double revenue) {
        String subject = "Daily Summary - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        String html = buildHtmlEmail(
            "Daily Summary Report",
            "#009688",
            String.format("""
                <p>Here's your daily summary for %s:</p>
                <div style="display: flex; flex-wrap: wrap; gap: 15px; margin: 20px 0;">
                    <div style="background: #e3f2fd; padding: 20px; border-radius: 8px; flex: 1; min-width: 150px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold; color: #1976D2;">%d</div>
                        <div style="color: #666;">Orders Created</div>
                    </div>
                    <div style="background: #e8f5e9; padding: 20px; border-radius: 8px; flex: 1; min-width: 150px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold; color: #388E3C;">%d</div>
                        <div style="color: #666;">Orders Completed</div>
                    </div>
                    <div style="background: #fff3e0; padding: 20px; border-radius: 8px; flex: 1; min-width: 150px; text-align: center;">
                        <div style="font-size: 32px; font-weight: bold; color: #F57C00;">%d</div>
                        <div style="color: #666;">Pending Jobs</div>
                    </div>
                </div>
                <p>Log in to the system for detailed reports.</p>
                """, LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                ordersCreated, ordersCompleted, pendingJobs)
        );
        
        SendMail.sendAsync(adminEmail, subject, html);
    }

    // ==================== Helper Methods ====================

    /**
     * Build HTML email with consistent styling
     */
    private String buildHtmlEmail(String title, String accentColor, String content) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background: white;">
                    <!-- Header -->
                    <div style="background: %s; padding: 30px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 24px;">%s</h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        %s
                    </div>
                    
                    <!-- Footer -->
                    <div style="background: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0 0 10px 0; font-weight: bold; color: #333;">%s</p>
                        <p style="margin: 0; color: #666; font-size: 14px;">%s</p>
                        <p style="margin: 5px 0 0 0; color: #666; font-size: 14px;">%s</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            accentColor,
            title,
            content,
            config.getCompanyName(),
            config.getCompanyAddress(),
            config.getCompanyPhone()
        );
    }

    /**
     * Shutdown email service
     */
    public void shutdown() {
        SendMail.shutdown();
    }
}
