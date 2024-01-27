package DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Models.Employee;

public class EmployeeDatabase {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/oop-chaos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static int saveEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employee_name, contact_number, email, position) VALUES (?, ?, ?, ?)";
        int employeeId = 0;

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, employee.getEmployeeName());
            preparedStatement.setString(2, employee.getContactNumber());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPosition());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employeeId = generatedKeys.getInt(1);
                    employee.setEmployeeId(employeeId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeeId;
    }

    public static void updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET employee_name=?, contact_number=?, email=?, position=? WHERE employee_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, employee.getEmployeeName());
            preparedStatement.setString(2, employee.getContactNumber());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPosition());
            preparedStatement.setInt(5, employee.getEmployeeId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteEmployee(int employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, employeeId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setEmployeeId(resultSet.getInt("employee_id"));
                employee.setEmployeeName(resultSet.getString("employee_name"));
                employee.setContactNumber(resultSet.getString("contact_number"));
                employee.setEmail(resultSet.getString("email"));
                employee.setPosition(resultSet.getString("position"));

                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public static Employee getEmployeeById(int employeeId) {
        Employee employee = null;
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, employeeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                employee = new Employee();
                employee.setEmployeeId(resultSet.getInt("employee_id"));
                employee.setEmployeeName(resultSet.getString("employee_name"));
                employee.setContactNumber(resultSet.getString("contact_number"));
                employee.setEmail(resultSet.getString("email"));
                employee.setPosition(resultSet.getString("position"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employee;
    }
    
    public static List<Integer> getAllEmployeeIds() {
        List<Integer> employeeIds = new ArrayList<>();
        String sql = "SELECT employee_id FROM employees";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                employeeIds.add(resultSet.getInt("employee_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeeIds;
    }
    public static String getEmployeeEmailById(int employeeId) {
        String email = null;
        String sql = "SELECT email FROM employees WHERE employee_id = ?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, employeeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return email;
    }
}