package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized configuration manager for the CarCare application.
 * Loads properties from config.properties file and provides
 * type-safe access to configuration values.
 */
public class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Get the singleton instance of ConfigManager
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                LOGGER.severe("Unable to find config.properties");
                loadDefaultProperties();
                return;
            }
            properties.load(input);
            LOGGER.info("Configuration loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration", e);
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        // Fallback defaults
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/oop-chaos");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.pool.size", "10");
        LOGGER.warning("Using default configuration values");
    }

    // Database Configuration
    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public String getDbDriver() {
        return properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public int getDbPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.size", "10"));
    }

    public int getDbPoolMinIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.min.idle", "5"));
    }

    public long getDbPoolMaxLifetime() {
        return Long.parseLong(properties.getProperty("db.pool.max.lifetime", "1800000"));
    }

    public long getDbConnectionTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.connection.timeout", "30000"));
    }

    // Email Configuration
    public String getMailHost() {
        return properties.getProperty("mail.smtp.host");
    }

    public String getMailPort() {
        return properties.getProperty("mail.smtp.port");
    }

    public boolean isMailAuthEnabled() {
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.auth", "true"));
    }

    public boolean isMailStartTlsEnabled() {
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.starttls.enable", "true"));
    }

    public String getMailSslProtocols() {
        return properties.getProperty("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    public String getMailFromEmail() {
        return properties.getProperty("mail.from.email");
    }

    public String getMailFromPassword() {
        return properties.getProperty("mail.from.password");
    }

    // Application Settings
    public String getAppName() {
        return properties.getProperty("app.name", "CarCare Management System");
    }

    public String getAppVersion() {
        return properties.getProperty("app.version", "2.0.0");
    }

    public String getAppTheme() {
        return properties.getProperty("app.theme", "light");
    }

    // Report Settings
    public String getReportOutputPath() {
        return properties.getProperty("report.output.path", "./reports");
    }

    public String getCompanyName() {
        return properties.getProperty("report.company.name", "CarCare Auto Services");
    }

    public String getCompanyAddress() {
        return properties.getProperty("report.company.address", "");
    }

    public String getCompanyPhone() {
        return properties.getProperty("report.company.phone", "");
    }

    /**
     * Get a custom property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a custom property with default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Reload configuration from file
     */
    public void reload() {
        loadProperties();
    }
}
