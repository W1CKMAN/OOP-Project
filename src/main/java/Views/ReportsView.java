package Views;

import DAO.Impl.OrderDAOImpl;
import DAO.Impl.JobDAOImpl;
import DAO.Impl.EmployeeDAOImpl;
import DAO.OrderDAO;
import DAO.JobDAO;
import DAO.EmployeeDAO;
import Utils.ExportUtil;
import Utils.ToastNotification;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

/**
 * Reports view with charts and analytics.
 */
public class ReportsView extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(ReportsView.class);
    
    private final OrderDAO orderDAO;
    private final JobDAO jobDAO;
    private final EmployeeDAO employeeDAO;
    
    private JComboBox<String> reportTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;
    private JPanel chartPanel;
    private JButton exportExcelButton;
    private JButton exportPdfButton;
    private JButton refreshButton;

    public ReportsView(Frame parent) {
        super(parent, "Reports & Analytics", true);
        this.orderDAO = new OrderDAOImpl();
        this.jobDAO = new JobDAOImpl();
        this.employeeDAO = new EmployeeDAOImpl();
        initializeUI();
    }

    private void initializeUI() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[]15[grow]"));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Header with controls
        mainPanel.add(createControlPanel(), "growx, wrap");

        // Chart area
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        chartPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(chartPanel, "grow");

        setContentPane(mainPanel);

        // Load initial report
        loadReport();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 15", "[]15[]15[]15[]push[]10[]10[]", "[]"));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        // Report type selector
        JLabel reportLabel = new JLabel("Report Type:");
        reportLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(reportLabel);

        reportTypeCombo = new JComboBox<>(new String[]{
            "Orders Overview",
            "Jobs Status",
            "Monthly Orders Trend",
            "Employee Workload",
            "Order Status Distribution"
        });
        reportTypeCombo.addActionListener(e -> loadReport());
        panel.add(reportTypeCombo);

        // Year selector
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(yearLabel);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearCombo = new JComboBox<>();
        for (int year = currentYear; year >= currentYear - 5; year--) {
            yearCombo.addItem(year);
        }
        yearCombo.addActionListener(e -> loadReport());
        panel.add(yearCombo);

        // Month selector
        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(monthLabel);

        monthCombo = new JComboBox<>(new String[]{
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthCombo.addActionListener(e -> loadReport());
        panel.add(monthCombo);

        // Buttons
        refreshButton = createButton("🔄 Refresh", new Color(59, 130, 246));
        refreshButton.addActionListener(e -> loadReport());
        panel.add(refreshButton);

        exportExcelButton = createButton("📊 Export Excel", new Color(16, 185, 129));
        exportExcelButton.addActionListener(e -> exportToExcel());
        panel.add(exportExcelButton);

        exportPdfButton = createButton("📄 Export PDF", new Color(239, 68, 68));
        exportPdfButton.addActionListener(e -> exportToPdf());
        panel.add(exportPdfButton);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        return button;
    }

    private void loadReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        Integer year = (Integer) yearCombo.getSelectedItem();

        chartPanel.removeAll();

        JFreeChart chart = null;

        switch (reportType) {
            case "Orders Overview":
                chart = createOrdersOverviewChart(year);
                break;
            case "Jobs Status":
                chart = createJobsStatusChart();
                break;
            case "Monthly Orders Trend":
                chart = createMonthlyOrdersTrendChart(year);
                break;
            case "Employee Workload":
                chart = createEmployeeWorkloadChart();
                break;
            case "Order Status Distribution":
                chart = createOrderStatusPieChart();
                break;
        }

        if (chart != null) {
            // Customize chart appearance
            chart.setBackgroundPaint(Color.WHITE);
            chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
            
            ChartPanel cp = new ChartPanel(chart);
            cp.setBackground(Color.WHITE);
            cp.setBorder(null);
            chartPanel.add(cp, BorderLayout.CENTER);
        }

        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private JFreeChart createOrdersOverviewChart(int year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object[]> stats = orderDAO.getMonthlyStats(year);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        // Initialize with zeros
        for (String month : months) {
            dataset.addValue(0, "Total Orders", month);
            dataset.addValue(0, "Completed", month);
        }

        // Fill with actual data
        for (Object[] stat : stats) {
            int monthIndex = (int) stat[0] - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                dataset.setValue((int) stat[1], "Total Orders", months[monthIndex]);
                dataset.setValue((int) stat[2], "Completed", months[monthIndex]);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Orders Overview - " + year,
            "Month",
            "Number of Orders",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        customizeBarChart(chart);
        return chart;
    }

    private JFreeChart createJobsStatusChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int pending = jobDAO.countByStatus("Pending");
        int inProgress = jobDAO.countByStatus("In Progress");
        int completed = jobDAO.countByStatus("Completed");

        dataset.addValue(pending, "Count", "Pending");
        dataset.addValue(inProgress, "Count", "In Progress");
        dataset.addValue(completed, "Count", "Completed");

        JFreeChart chart = ChartFactory.createBarChart(
            "Jobs by Status",
            "Status",
            "Number of Jobs",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        customizeBarChart(chart);
        return chart;
    }

    private JFreeChart createMonthlyOrdersTrendChart(int year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Object[]> stats = orderDAO.getMonthlyStats(year);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        // Initialize with zeros
        for (String month : months) {
            dataset.addValue(0, "Orders", month);
        }

        // Fill with actual data
        for (Object[] stat : stats) {
            int monthIndex = (int) stat[0] - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                dataset.setValue((int) stat[1], "Orders", months[monthIndex]);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
            "Monthly Orders Trend - " + year,
            "Month",
            "Number of Orders",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(59, 130, 246));
        chart.getCategoryPlot().getRenderer().setSeriesStroke(0, new java.awt.BasicStroke(3.0f));

        return chart;
    }

    private JFreeChart createEmployeeWorkloadChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        var employees = employeeDAO.findAll();
        for (var emp : employees) {
            int workload = employeeDAO.getEmployeeWorkload(emp.getEmployeeId());
            dataset.addValue(workload, "Active Jobs", emp.getEmployeeName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Employee Workload",
            "Employee",
            "Number of Active Jobs",
            dataset,
            PlotOrientation.HORIZONTAL,
            false,
            true,
            false
        );

        customizeBarChart(chart);
        return chart;
    }

    private JFreeChart createOrderStatusPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        int pending = orderDAO.countByStatus("Pending");
        int inProgress = orderDAO.countByStatus("In Progress");
        int completed = orderDAO.countByStatus("Completed");

        dataset.setValue("Pending (" + pending + ")", pending);
        dataset.setValue("In Progress (" + inProgress + ")", inProgress);
        dataset.setValue("Completed (" + completed + ")", completed);

        JFreeChart chart = ChartFactory.createPieChart(
            "Order Status Distribution",
            dataset,
            true,
            true,
            false
        );

        @SuppressWarnings("rawtypes")
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Pending (" + pending + ")", new Color(245, 158, 11));
        plot.setSectionPaint("In Progress (" + inProgress + ")", new Color(59, 130, 246));
        plot.setSectionPaint("Completed (" + completed + ")", new Color(16, 185, 129));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        return chart;
    }

    private void customizeBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(226, 232, 240));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(59, 130, 246));
        renderer.setSeriesPaint(1, new Color(16, 185, 129));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);
    }

    private void exportToExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("CarCare_Report.xlsx"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                // Export orders to Excel
                ExportUtil.exportOrdersToExcel(orderDAO.findAll(), filePath);
                ToastNotification.success(this, "Report exported successfully!");
                logger.info("Report exported to: {}", filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to export report", e);
            ToastNotification.error(this, "Failed to export report: " + e.getMessage());
        }
    }

    private void exportToPdf() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("CarCare_Report.pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                ExportUtil.exportReportToPdf(filePath, "CarCare Report", "Generated on " + LocalDate.now());
                ToastNotification.success(this, "PDF exported successfully!");
                logger.info("PDF exported to: {}", filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to export PDF", e);
            ToastNotification.error(this, "Failed to export PDF: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReportsView(null).setVisible(true);
        });
    }
}
