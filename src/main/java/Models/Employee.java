package Models;

import java.time.LocalDate;

/**
 * Employee entity representing staff members
 */
public class Employee {
    private int employeeId;
    private String employeeName;
    private String contactNumber;
    private String email;
    private String position;
    private Double salary;
    private String status;
    private LocalDate hireDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Employee() {
        this.status = "Active";
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public Employee(String employeeName, String contactNumber, String email, String position) {
        this();
        this.employeeName = employeeName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.position = position;
    }

    public Employee(String employeeName, String contactNumber, String email, String position, Double salary) {
        this(employeeName, contactNumber, email, position);
        this.salary = salary;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}