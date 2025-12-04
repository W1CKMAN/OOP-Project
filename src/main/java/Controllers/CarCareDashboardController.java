package Controllers;

import DAO.Impl.OrderDAOImpl;
import DAO.Impl.JobDAOImpl;
import DAO.Impl.EmployeeDAOImpl;
import DAO.OrderDAO;
import DAO.JobDAO;
import DAO.EmployeeDAO;
import Views.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Controller for the main dashboard.
 * Handles navigation and statistics updates.
 */
public class CarCareDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(CarCareDashboardController.class);
    
    private final CarCareDashboard dashboard;
    private final OrderDAO orderDAO;
    private final JobDAO jobDAO;
    private final EmployeeDAO employeeDAO;

    public CarCareDashboardController(CarCareDashboard dashboard) {
        this.dashboard = dashboard;
        this.orderDAO = new OrderDAOImpl();
        this.jobDAO = new JobDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();

        initializeListeners();
        loadDashboardStatistics();
        
        // Show the dashboard
        dashboard.setVisible(true);
        
        logger.info("Dashboard controller initialized");
    }

    private void initializeListeners() {
        // Orders Manager
        dashboard.addOrderManagerButtonListener(e -> {
            logger.info("Opening Order Management");
            new OrderManagementView().setVisible(true);
        });

        // Jobs Manager
        dashboard.addJobsManagerButtonListener(e -> {
            logger.info("Opening Jobs Manager");
            new JobsView().setVisible(true);
        });

        // Customer Manager
        dashboard.addcustomerDetailsManagerButtonListener(e -> {
            logger.info("Opening Customer Manager");
            new CustomerView().setVisible(true);
        });

        // Supplier Manager
        dashboard.addSupplierManagerButtonListener(e -> {
            logger.info("Opening Supplier Manager");
            new SupplierView().setVisible(true);
        });

        // Inventory Manager
        dashboard.addInventoryManagerButtonListener(e -> {
            logger.info("Opening Inventory Manager");
            new InventoryView().setVisible(true);
        });

        // Employee Manager
        dashboard.addEmployeeManagerButtonListener(e -> {
            logger.info("Opening Employee Manager");
            new EmployeeView().setVisible(true);
        });

        // Reports
        dashboard.addReportsButtonListener(e -> {
            logger.info("Opening Reports View");
            new ReportsView(dashboard).setVisible(true);
        });
    }

    /**
     * Load dashboard statistics from database
     */
    private void loadDashboardStatistics() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() {
                try {
                    int totalOrders = (int) orderDAO.count();
                    int pendingJobs = jobDAO.countByStatus("Pending") + jobDAO.countByStatus("In Progress");
                    int activeEmployees = (int) employeeDAO.count();
                    int completedToday = orderDAO.countByStatus("Completed");
                    
                    return new int[]{totalOrders, pendingJobs, activeEmployees, completedToday};
                } catch (Exception e) {
                    logger.error("Error loading dashboard statistics", e);
                    return new int[]{0, 0, 0, 0};
                }
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    dashboard.updateStatistics(stats[0], stats[1], stats[2], stats[3]);
                    logger.info("Dashboard statistics updated");
                } catch (Exception e) {
                    logger.error("Error updating dashboard", e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Refresh dashboard statistics
     */
    public void refreshStatistics() {
        loadDashboardStatistics();
    }
}