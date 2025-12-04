<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Maven-3.9+-purple?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>
</p>

<h1 align="center">🚗 CarCare Management System</h1>

<p align="center">
  <strong>A comprehensive vehicle service management solution for automotive repair shops</strong>
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-screenshots">Screenshots</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-installation">Installation</a> •
  <a href="#-usage">Usage</a> •
  <a href="#-architecture">Architecture</a>
</p>

---

## 📋 Overview

**CarCare** is a modern, full-featured desktop application designed to streamline operations for vehicle service centers. Built with Java Swing and enhanced with contemporary UI components, it provides an intuitive interface for managing customers, orders, jobs, employees, inventory, and suppliers.

Whether you're running a small garage or a large automotive service center, CarCare helps you:
- 📊 Track service orders from intake to completion
- 👥 Manage customer relationships and vehicle history
- 👷 Assign and monitor jobs for technicians
- 📦 Keep inventory levels optimized
- 📧 Send automated email notifications
- 📈 Generate insightful reports

---

## ✨ Features

### 🎯 Core Functionality

| Module | Description |
|--------|-------------|
| **Dashboard** | Real-time statistics, quick actions, and system overview |
| **Order Management** | Create, track, and manage service orders with status workflow |
| **Customer Management** | Customer profiles, contact info, and vehicle service history |
| **Employee Management** | Staff records, job assignments, and workload tracking |
| **Job Management** | Task assignment, progress tracking, and completion notifications |
| **Inventory Management** | Stock levels, low-stock alerts, and supplier integration |
| **Supplier Management** | Vendor information and contact management |
| **Reports** | Visual charts, PDF/Excel exports, and analytics |

### 🎨 Modern UI/UX
- **FlatLaf** - Modern, flat look and feel
- **Responsive layouts** with MigLayout
- **Toast notifications** for user feedback
- **Search & filtering** on all data tables
- **Statistics cards** with real-time data
- **Dark/Light theme** support

### 🔐 Security
- **Role-based access control** (Admin, Manager, Employee, Receptionist)
- **BCrypt password hashing**
- **Secure session management**

### 📧 Notifications
- **Automated email notifications**
  - Order confirmations
  - Status updates
  - Job assignments
  - Vehicle ready for pickup
- **HTML email templates**
- **Async email sending**

### 📊 Reporting
- **Interactive charts** with JFreeChart
- **PDF export** with iText
- **Excel export** with Apache POI
- **Daily/Monthly summaries**

---

## 🖼️ Screenshots

<p align="center">
  <i>Coming soon - Screenshots of the application interface</i>
</p>

<!--
<p align="center">
  <img src="screenshots/dashboard.png" width="45%" alt="Dashboard"/>
  <img src="screenshots/orders.png" width="45%" alt="Orders"/>
</p>
<p align="center">
  <img src="screenshots/customers.png" width="45%" alt="Customers"/>
  <img src="screenshots/reports.png" width="45%" alt="Reports"/>
</p>
-->

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 21** | Core programming language |
| **JDBC** | Database connectivity |
| **HikariCP** | High-performance connection pooling |
| **BCrypt** | Password encryption |
| **Jakarta Mail** | Email functionality |
| **SLF4J + Logback** | Logging framework |

### Frontend
| Technology | Purpose |
|------------|---------|
| **Java Swing** | GUI framework |
| **FlatLaf 3.4** | Modern look and feel |
| **MigLayout** | Flexible layout manager |
| **JFreeChart** | Charts and graphs |

### Database
| Technology | Purpose |
|------------|---------|
| **MySQL 8.0** | Relational database |

### Build & Tools
| Technology | Purpose |
|------------|---------|
| **Maven** | Build automation |
| **iText PDF** | PDF generation |
| **Apache POI** | Excel export |

---

## 📦 Installation

### Prerequisites

- **Java 21** or higher ([Download](https://adoptium.net/))
- **MySQL 8.0** or higher ([Download](https://dev.mysql.com/downloads/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/W1CKMAN/OOP-Project.git
   cd OOP-Project
   ```

2. **Create the database**
   ```bash
   mysql -u root -p < src/main/resources/database_schema.sql
   ```

3. **Configure the application**
   
   Edit `src/main/resources/config.properties`:
   ```properties
   # Database Configuration
   db.url=jdbc:mysql://localhost:3306/oop-chaos
   db.username=root
   db.password=your_password
   
   # Email Configuration (Gmail example)
   mail.smtp.host=smtp.gmail.com
   mail.smtp.port=587
   mail.from.email=your-email@gmail.com
   mail.from.password=your-app-password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn exec:java -Dexec.mainClass="Main.Main"
   ```

---

## 🚀 Usage

### Default Login
After initial setup, use these credentials:
- **Username:** `admin`
- **Password:** `admin123`

> ⚠️ **Important:** Change the default password after first login!

### Quick Start Guide

1. **Add Employees** - Set up your technicians and staff
2. **Add Suppliers** - Register your parts suppliers
3. **Add Inventory** - Stock your inventory items
4. **Register Customers** - Add customer information
5. **Create Orders** - Start accepting service orders
6. **Assign Jobs** - Delegate work to employees
7. **Track Progress** - Monitor job status and completion

---

## 🏗️ Architecture

```
src/main/java/
├── Config/                 # Configuration management
│   └── ConfigManager.java
├── Controllers/            # MVC Controllers
│   ├── CarCareDashboardController.java
│   ├── EmployeeController.java
│   └── ...
├── DAO/                    # Data Access Objects
│   ├── GenericDAO.java
│   ├── CustomerDAO.java
│   ├── Impl/
│   │   ├── CustomerDAOImpl.java
│   │   └── ...
│   └── ...
├── DatabaseConnection/     # Database utilities
│   ├── ConnectionPool.java
│   └── ...
├── Models/                 # Entity classes
│   ├── Customer.java
│   ├── Employee.java
│   ├── Order.java
│   └── ...
├── Services/               # Business logic
│   ├── AuthService.java
│   ├── CustomerService.java
│   └── EmailService.java
├── Utils/                  # Utility classes
│   ├── ValidationUtil.java
│   ├── ToastNotification.java
│   └── ...
├── Views/                  # Swing UI components
│   ├── CarCareDashboard.java
│   ├── CustomerView.java
│   └── ...
└── Main/
    └── Main.java           # Application entry point
```

### Design Patterns Used
- **MVC** (Model-View-Controller)
- **DAO** (Data Access Object)
- **Singleton** (ConfigManager, ConnectionPool)
- **Service Layer** (Business logic separation)
- **Repository Pattern** (Data abstraction)

---

## 📁 Project Structure

```
OOP-Project/
├── 📁 src/
│   └── 📁 main/
│       ├── 📁 java/           # Source code
│       └── 📁 resources/      # Configuration files
│           ├── config.properties
│           └── database_schema.sql
├── 📁 target/                 # Build output
├── 📄 pom.xml                 # Maven configuration
└── 📄 README.md
```

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow Java naming conventions
- Write meaningful commit messages
- Add comments for complex logic
- Update documentation as needed

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Helitha Guruge** ([@iamhelitha](https://github.com/iamhelitha))

### 🤝 Contributors

Thanks to all the amazing people who contributed to this project:

<table>
  <tr>
    <td align="center"><a href="https://github.com/iamhelitha"><img src="https://github.com/iamhelitha.png" width="80px;" alt=""/><br /><sub><b>Helitha Guruge</b></sub></a><br />💻 Lead Developer</td>
    <td align="center"><a href="https://github.com/SA23098350"><img src="https://github.com/SA23098350.png" width="80px;" alt=""/><br /><sub><b>SA23098350</b></sub></a><br />💻</td>
    <td align="center"><a href="https://github.com/Ravidesilva89"><img src="https://github.com/Ravidesilva89.png" width="80px;" alt=""/><br /><sub><b>Ravidesilva89</b></sub></a><br />💻</td>
    <td align="center"><a href="https://github.com/NipunCreations"><img src="https://github.com/NipunCreations.png" width="80px;" alt=""/><br /><sub><b>Nipun Fernando</b></sub></a><br />💻</td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/W1CKMAN"><img src="https://github.com/W1CKMAN.png" width="80px;" alt=""/><br /><sub><b>W1CKMAN</b></sub></a><br />💻</td>
    <td align="center"><a href="https://github.com/Nihidunimthaka"><img src="https://github.com/Nihidunimthaka.png" width="80px;" alt=""/><br /><sub><b>Nihidunimthaka</b></sub></a><br />💻</td>
    <td align="center"><a href="https://github.com/dulan8"><img src="https://github.com/dulan8.png" width="80px;" alt=""/><br /><sub><b>dulan8</b></sub></a><br />💻</td>
    <td align="center"><a href="https://github.com/senuja2003"><img src="https://github.com/senuja2003.png" width="80px;" alt=""/><br /><sub><b>senuja2003</b></sub></a><br />💻</td>
  </tr>
</table>

---

## 🙏 Acknowledgments

- [FlatLaf](https://www.formdev.com/flatlaf/) - Modern Swing look and feel
- [MigLayout](http://www.miglayout.com/) - Flexible layout manager
- [JFreeChart](https://www.jfree.org/jfreechart/) - Chart library
- [HikariCP](https://github.com/brettwooldridge/HikariCP) - JDBC connection pool

---

<p align="center">
  Made with ❤️ for the automotive service industry
</p>

<p align="center">
  ⭐ Star this repo if you find it helpful!
</p>
