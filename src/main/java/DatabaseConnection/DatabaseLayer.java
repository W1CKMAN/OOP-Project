package DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Models.CustomerOrder;

public class DatabaseLayer {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/oop-chaos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static int saveOrder(CustomerOrder order) {
        String sql = "INSERT INTO Orders (customer_id, order_date, vehicle_model, vehicle_number, status) VALUES (?, ?, ?, ?, ?)";
        int orderId = 0;

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            preparedStatement.setString(3, order.getVehicleModel());
            preparedStatement.setString(4, order.getVehicleNumber());  // Set the vehicle number
            preparedStatement.setString(5, order.getStatus());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    public static void updateOrder(CustomerOrder order) {
        String sql = "UPDATE Orders SET customer_id=?, vehicle_model=?, vehicle_number=?, status=? WHERE order_id=?";

        try (
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setString(2, order.getVehicleModel());
            preparedStatement.setString(3, order.getVehicleNumber());  // Set the vehicle number
            preparedStatement.setString(4, order.getStatus());
            preparedStatement.setInt(5, order.getOrderId());  // Change the index to 5

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOrder(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, orderId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CustomerOrder> getAllOrders() {
        List<CustomerOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                CustomerOrder order = new CustomerOrder();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setCustomerId(resultSet.getInt("customer_id"));
                order.setOrderDate(resultSet.getDate("order_date"));
                order.setVehicleModel(resultSet.getString("vehicle_model"));
                order.setVehicleNumber(resultSet.getString("vehicle_number"));
                order.setStatus(resultSet.getString("status"));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static CustomerOrder getOrderById(int orderId) {
        CustomerOrder order = null;
        String sql = "SELECT * FROM Orders WHERE order_id = ?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                order = new CustomerOrder();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setCustomerId(resultSet.getInt("customer_id"));
                order.setOrderDate(resultSet.getDate("order_date"));
                order.setVehicleModel(resultSet.getString("vehicle_model"));
                order.setVehicleNumber(resultSet.getString("vehicle_number"));
                order.setStatus(resultSet.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
    public static boolean customerIdExists(int customerId) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE customer_id = ?";
        boolean exists = false;

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }
}
