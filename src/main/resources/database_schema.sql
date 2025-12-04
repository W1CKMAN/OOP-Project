-- CarCare Management System Database Schema
-- Version 2.0.0

-- Drop existing tables if needed (be careful in production!)
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS Jobs;
-- DROP TABLE IF EXISTS Orders;
-- DROP TABLE IF EXISTS Inventory;
-- DROP TABLE IF EXISTS Suppliers;
-- DROP TABLE IF EXISTS Employees;
-- DROP TABLE IF EXISTS Customers;
-- DROP TABLE IF EXISTS Users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- Users table for authentication
CREATE TABLE IF NOT EXISTS Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'MANAGER', 'EMPLOYEE', 'RECEPTIONIST') NOT NULL DEFAULT 'EMPLOYEE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Customers table
CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    address TEXT,
    vehicle_history TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Employees table
CREATE TABLE IF NOT EXISTS Employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    position VARCHAR(50),
    salary DECIMAL(10, 2),
    status ENUM('Active', 'On Leave', 'Terminated') NOT NULL DEFAULT 'Active',
    hire_date DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Suppliers table
CREATE TABLE IF NOT EXISTS Suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    category VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inventory table
CREATE TABLE IF NOT EXISTS Inventory (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    sku VARCHAR(50) UNIQUE,
    category VARCHAR(50),
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(20) DEFAULT 'pcs',
    min_quantity INT NOT NULL DEFAULT 10,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    cost_price DECIMAL(10, 2) DEFAULT 0.00,
    supplier_id INT,
    location VARCHAR(50),
    description TEXT,
    last_restocked TIMESTAMP NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sku (sku),
    INDEX idx_category (category),
    INDEX idx_low_stock (quantity, min_quantity),
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Orders table
CREATE TABLE IF NOT EXISTS Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,
    vehicle_model VARCHAR(100),
    vehicle_number VARCHAR(20) NOT NULL,
    status ENUM('Pending', 'In Progress', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Pending',
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_vehicle_number (vehicle_number),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Jobs table
CREATE TABLE IF NOT EXISTS Jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    employee_id INT NOT NULL,
    job_description TEXT NOT NULL,
    status ENUM('Pending', 'In Progress', 'Completed', 'On Hold') NOT NULL DEFAULT 'Pending',
    estimated_hours DECIMAL(5, 2),
    actual_hours DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_status (status),
    INDEX idx_employee (employee_id),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES Employees(employee_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Invoice table (for future use)
CREATE TABLE IF NOT EXISTS Invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    invoice_number VARCHAR(20) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE,
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('Draft', 'Sent', 'Paid', 'Overdue', 'Cancelled') DEFAULT 'Draft',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default admin user (password: Admin@123)
INSERT INTO Users (username, password_hash, full_name, email, role, active) 
VALUES ('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.urGCNr8Y6Lz5oG', 'System Administrator', 'admin@carcare.com', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE username = username;

-- Sample data for testing
INSERT INTO Employees (employee_name, contact_number, email, position) VALUES
('John Smith', '0771234567', 'john@carcare.com', 'Mechanic'),
('Jane Doe', '0779876543', 'jane@carcare.com', 'Technician'),
('Mike Johnson', '0775551234', 'mike@carcare.com', 'Senior Mechanic')
ON DUPLICATE KEY UPDATE employee_name = employee_name;

INSERT INTO Customers (name, email, phone, address) VALUES
('Amith Perera', 'amith@example.com', '0712345678', 'Colombo'),
('Kumari Silva', 'kumari@example.com', '0723456789', 'Kandy'),
('Ruwan Fernando', 'ruwan@example.com', '0734567890', 'Galle')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO Suppliers (company_name, contact_person, email, phone, category) VALUES
('AutoParts Lanka', 'Kamal', 'sales@autoparts.lk', '0112345678', 'Parts'),
('Paint World', 'Nimal', 'info@paintworld.lk', '0112345679', 'Paint'),
('Tools Plus', 'Sunil', 'sales@toolsplus.lk', '0112345680', 'Tools')
ON DUPLICATE KEY UPDATE company_name = company_name;

INSERT INTO Inventory (name, sku, category, quantity, min_quantity, unit_price, supplier_id) VALUES
('Engine Oil 5W-30', 'OIL-5W30-1L', 'Lubricants', 50, 10, 2500.00, 1),
('Brake Pad Set', 'BRK-PAD-001', 'Brake Parts', 25, 5, 8500.00, 1),
('Air Filter', 'FLT-AIR-UNI', 'Filters', 30, 10, 1200.00, 1),
('Car Paint Black', 'PNT-BLK-1L', 'Paint', 15, 5, 4500.00, 2),
('Socket Set', 'TLS-SKT-001', 'Tools', 10, 2, 12000.00, 3)
ON DUPLICATE KEY UPDATE name = name;
