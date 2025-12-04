package DatabaseConnection;

import Config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection pool manager using HikariCP.
 * Provides efficient connection pooling for the application.
 */
public class ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private static HikariDataSource dataSource;
    private static ConnectionPool instance;

    private ConnectionPool() {
        initializeDataSource();
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initializeDataSource() {
        try {
            ConfigManager config = ConfigManager.getInstance();
            
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getDbUrl());
            hikariConfig.setUsername(config.getDbUsername());
            hikariConfig.setPassword(config.getDbPassword());
            hikariConfig.setDriverClassName(config.getDbDriver());
            
            // Pool settings
            hikariConfig.setMaximumPoolSize(config.getDbPoolSize());
            hikariConfig.setMinimumIdle(config.getDbPoolMinIdle());
            hikariConfig.setMaxLifetime(config.getDbPoolMaxLifetime());
            hikariConfig.setConnectionTimeout(config.getDbConnectionTimeout());
            
            // Performance settings
            hikariConfig.setPoolName("CarCareHikariPool");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
            hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(hikariConfig);
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Get a connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Close the connection pool
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down");
        }
    }

    /**
     * Get pool statistics
     */
    public String getPoolStats() {
        if (dataSource != null) {
            return String.format("Active: %d, Idle: %d, Total: %d, Waiting: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections(),
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
        return "Pool not initialized";
    }

    /**
     * Check if the pool is healthy
     */
    public boolean isHealthy() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            logger.error("Database health check failed", e);
            return false;
        }
    }
}
