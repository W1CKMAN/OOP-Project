package Controllers;

import DatabaseConnection.EmployeeDatabase;
import Models.Employee;

import java.util.List;

public class EmployeeController {

    public EmployeeController() {
        // Default constructor
    }

    public static void addEmployee(Employee employee) {
        // Validation logic if needed
        EmployeeDatabase.saveEmployee(employee);
    }

    public static void updateEmployee(Employee employee) {
        // Validation logic if needed
        EmployeeDatabase.updateEmployee(employee);
    }

    public static void removeEmployee(int employeeId) {
        EmployeeDatabase.deleteEmployee(employeeId);
    }

    public static List<Employee> getAllEmployees() {
        return EmployeeDatabase.getAllEmployees();
    }
}