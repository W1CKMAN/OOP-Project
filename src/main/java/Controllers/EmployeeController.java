package Controllers;

import DatabaseConnection.EmployeeDatabase;
import Models.Employee;

import java.util.List;

public class EmployeeController {
    private static EmployeeDatabase employeeDatabase;

    public EmployeeController(EmployeeDatabase employeeDatabase) {
        this.employeeDatabase = employeeDatabase;
    }

    public static void addEmployee(Employee employee) {
        // Validation logic if needed
        employeeDatabase.saveEmployee(employee);
    }

    public static void updateEmployee(Employee employee) {
        // Validation logic if needed
        employeeDatabase.updateEmployee(employee);
    }

    public static void removeEmployee(int employeeId) {
        employeeDatabase.deleteEmployee(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeDatabase.getAllEmployees();
    }
}