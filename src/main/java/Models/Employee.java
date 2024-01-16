package Models;

public class Employee {
    private int employeeId;
    private String employeeName;
    private String contactNumber;
    private String email;
    private String position;

    public Employee() {
    }

    public Employee(String employeeName, String contactNumber, String email, String position) {
        this.employeeName = employeeName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.position = position;
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
}