package Utils;

import Config.ConfigManager;
import Models.Order;
import Models.Employee;
import Models.Job;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class for exporting data to Excel and PDF formats.
 */
public class ExportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtil.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final ConfigManager config = ConfigManager.getInstance();

    /**
     * Export orders to Excel file
     */
    public static void exportOrdersToExcel(List<Order> orders, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Orders");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Order ID", "Customer ID", "Order Date", "Vehicle Model", "Vehicle Number", "Status"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderId());
                row.createCell(1).setCellValue(order.getCustomerId());
                
                Cell dateCell = row.createCell(2);
                if (order.getOrderDate() != null) {
                    dateCell.setCellValue(order.getOrderDate());
                    dateCell.setCellStyle(dateStyle);
                }
                
                row.createCell(3).setCellValue(order.getVehicleModel());
                row.createCell(4).setCellValue(order.getVehicleNumber());
                row.createCell(5).setCellValue(order.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            logger.info("Orders exported to Excel: {}", filePath);
        }
    }

    /**
     * Export employees to Excel file
     */
    public static void exportEmployeesToExcel(List<Employee> employees, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Employee ID", "Name", "Contact Number", "Email", "Position"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getEmployeeId());
                row.createCell(1).setCellValue(emp.getEmployeeName());
                row.createCell(2).setCellValue(emp.getContactNumber());
                row.createCell(3).setCellValue(emp.getEmail());
                row.createCell(4).setCellValue(emp.getPosition());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            logger.info("Employees exported to Excel: {}", filePath);
        }
    }

    /**
     * Export jobs to Excel file
     */
    public static void exportJobsToExcel(List<Job> jobs, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Jobs");

            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Job ID", "Order ID", "Employee ID", "Description", "Status"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Job job : jobs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(job.getJobId());
                row.createCell(1).setCellValue(job.getOrderId());
                row.createCell(2).setCellValue(job.getEmployeeId());
                row.createCell(3).setCellValue(job.getJobDescription());
                row.createCell(4).setCellValue(job.getStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            logger.info("Jobs exported to Excel: {}", filePath);
        }
    }

    /**
     * Export report to PDF
     */
    public static void exportReportToPdf(String filePath, String title, String subtitle) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add company header
            addCompanyHeader(document);

            // Add title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 24, com.itextpdf.text.Font.BOLD
            );
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(10);
            document.add(titlePara);

            // Add subtitle
            com.itextpdf.text.Font subtitleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC
            );
            Paragraph subtitlePara = new Paragraph(subtitle, subtitleFont);
            subtitlePara.setAlignment(Element.ALIGN_CENTER);
            subtitlePara.setSpacingAfter(30);
            document.add(subtitlePara);

            // Add separator
            document.add(new Paragraph(" "));
            LineSeparator separator = new LineSeparator();
            separator.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(separator);
            document.add(new Paragraph(" "));

            // Add generation info
            com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10
            );
            Paragraph infoPara = new Paragraph(
                "Report generated on: " + dateFormat.format(new Date()),
                infoFont
            );
            infoPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(infoPara);

            logger.info("PDF report exported: {}", filePath);
        } finally {
            document.close();
        }
    }

    /**
     * Export orders to PDF with table
     */
    public static void exportOrdersToPdf(List<Order> orders, String filePath) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addCompanyHeader(document);

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD
            );
            Paragraph title = new Paragraph("Orders Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Create table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 1, 1.5f, 2, 1.5f, 1.5f});

            // Header cells
            addTableHeader(table, "Order ID", "Customer ID", "Date", "Vehicle Model", "Vehicle Number", "Status");

            // Data cells
            for (Order order : orders) {
                table.addCell(String.valueOf(order.getOrderId()));
                table.addCell(String.valueOf(order.getCustomerId()));
                table.addCell(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "");
                table.addCell(order.getVehicleModel());
                table.addCell(order.getVehicleNumber());
                table.addCell(order.getStatus());
            }

            document.add(table);

            // Footer
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Total Orders: " + orders.size());
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            logger.info("Orders PDF exported: {}", filePath);
        } finally {
            document.close();
        }
    }

    private static void addCompanyHeader(Document document) throws DocumentException {
        com.itextpdf.text.Font companyFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD
        );
        Paragraph company = new Paragraph(config.getCompanyName(), companyFont);
        company.setAlignment(Element.ALIGN_CENTER);
        document.add(company);

        com.itextpdf.text.Font addressFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10
        );
        Paragraph address = new Paragraph(config.getCompanyAddress() + " | " + config.getCompanyPhone(), addressFont);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(20);
        document.add(address);
    }

    private static void addTableHeader(PdfPTable table, String... headers) {
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE
        );
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(59, 130, 246));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }
}
