package DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Models.CustomerOrder;

public class DatabaseLayer {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/oop-chaos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public void saveOrder(CustomerOrder order) {
        String sql = "INSERT INTO Orders (customer_id, order_date, vehicle_model, status) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            preparedStatement.setString(3, order.getVehicleModel());
            preparedStatement.setString(4, order.getStatus());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(CustomerOrder order) {
        String sql = "UPDATE Orders SET customer_id=?, order_date=?, vehicle_model=?, status=? WHERE order_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, order.getCustomerId());
            preparedStatement.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            preparedStatement.setString(3, order.getVehicleModel());
            preparedStatement.setString(4, order.getStatus());
            preparedStatement.setInt(5, order.getOrderId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderId) {
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
                order.setStatus(resultSet.getString("status"));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
