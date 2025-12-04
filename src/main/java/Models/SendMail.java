package Models;

import Config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Email service for sending notifications.
 * Uses configuration from config.properties for SMTP settings.
 */
public class SendMail {
    private static final Logger logger = LoggerFactory.getLogger(SendMail.class);
    private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(3);
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Send email synchronously
     */
    public static boolean send(String to, String subject, String text) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", String.valueOf(config.isMailAuthEnabled()));
            prop.put("mail.smtp.starttls.enable", String.valueOf(config.isMailStartTlsEnabled()));
            prop.put("mail.smtp.host", config.getMailHost());
            prop.put("mail.smtp.port", config.getMailPort());
            prop.put("mail.smtp.ssl.protocols", config.getMailSslProtocols());

            String username = config.getMailFromEmail();
            String password = config.getMailFromPassword();

            Session session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            logger.info("Email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}", to, e);
            return false;
        }
    }

    /**
     * Send email asynchronously
     */
    public static void sendAsync(String to, String subject, String text) {
        emailExecutor.submit(() -> send(to, subject, text));
    }

    /**
     * Send email asynchronously with callback
     */
    public static void sendAsync(String to, String subject, String text, EmailCallback callback) {
        emailExecutor.submit(() -> {
            boolean success = send(to, subject, text);
            if (callback != null) {
                callback.onComplete(success);
            }
        });
    }

    /**
     * Send HTML email
     */
    public static boolean sendHtml(String to, String subject, String htmlContent) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", String.valueOf(config.isMailAuthEnabled()));
            prop.put("mail.smtp.starttls.enable", String.valueOf(config.isMailStartTlsEnabled()));
            prop.put("mail.smtp.host", config.getMailHost());
            prop.put("mail.smtp.port", config.getMailPort());
            prop.put("mail.smtp.ssl.protocols", config.getMailSslProtocols());

            String username = config.getMailFromEmail();
            String password = config.getMailFromPassword();

            Session session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("HTML email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to: {}", to, e);
            return false;
        }
    }

    /**
     * Send order completion notification
     */
    public static void sendOrderCompletionNotification(String customerEmail, String customerName, String vehicleNumber) {
        String subject = "Your Vehicle is Ready for Collection - CarCare";
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #337ab7;">Your Vehicle is Ready!</h2>
                <p>Dear %s,</p>
                <p>Great news! Your vehicle <strong>(%s)</strong> has been serviced and is now ready for collection.</p>
                <p>Please visit our service center at your earliest convenience to collect your vehicle.</p>
                <p><strong>Business Hours:</strong><br>
                Monday - Friday: 8:00 AM - 6:00 PM<br>
                Saturday: 9:00 AM - 4:00 PM</p>
                <p>Thank you for choosing CarCare!</p>
                <hr style="border: 1px solid #eee;">
                <p style="color: #666; font-size: 12px;">
                    %s<br>
                    %s<br>
                    %s
                </p>
            </body>
            </html>
            """,
            customerName,
            vehicleNumber,
            config.getCompanyName(),
            config.getCompanyAddress(),
            config.getCompanyPhone()
        );

        sendAsync(customerEmail, subject, htmlContent);
    }

    /**
     * Send job assignment notification to employee
     */
    public static void sendJobAssignmentNotification(String employeeEmail, String employeeName, String jobDescription, String vehicleInfo) {
        String subject = "New Job Assignment - CarCare";
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #5cb85c;">New Job Assigned</h2>
                <p>Dear %s,</p>
                <p>You have been assigned a new job:</p>
                <div style="background: #f5f5f5; padding: 15px; border-radius: 5px; margin: 10px 0;">
                    <strong>Vehicle:</strong> %s<br>
                    <strong>Description:</strong> %s
                </div>
                <p>Please check the system for more details and update the job status as you progress.</p>
                <p>Best regards,<br>CarCare Management</p>
            </body>
            </html>
            """,
            employeeName,
            vehicleInfo,
            jobDescription
        );

        sendAsync(employeeEmail, subject, htmlContent);
    }

    /**
     * Callback interface for async email operations
     */
    public interface EmailCallback {
        void onComplete(boolean success);
    }

    /**
     * Shutdown the email executor service
     */
    public static void shutdown() {
        emailExecutor.shutdown();
    }
}