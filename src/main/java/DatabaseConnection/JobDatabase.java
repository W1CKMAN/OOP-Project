package DatabaseConnection;

import Models.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDatabase {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/oop-chaos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static int saveJob(Job job) {
        String sql = "INSERT INTO jobs (order_id, employee_id, job_description, status) VALUES (?, ?, ?, ?)";
        int jobId = 0;

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setInt(1, job.getOrderId());
            preparedStatement.setInt(2, job.getEmployeeId());
            preparedStatement.setString(3, job.getJobDescription());
            preparedStatement.setString(4, job.getStatus());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    jobId = generatedKeys.getInt(1);
                    job.setJobId(jobId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobId;
    }

    public static void updateJob(Job job) {
        String sql = "UPDATE jobs SET order_id=?, employee_id=?, job_description=?, status=? WHERE job_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, job.getOrderId());
            preparedStatement.setInt(2, job.getEmployeeId());
            preparedStatement.setString(3, job.getJobDescription());
            preparedStatement.setString(4, job.getStatus());
            preparedStatement.setInt(5, job.getJobId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteJob(int jobId) {
        String sql = "DELETE FROM jobs WHERE job_id=?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, jobId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                Job job = new Job();
                job.setJobId(resultSet.getInt("job_id"));
                job.setOrderId(resultSet.getInt("order_id"));
                job.setEmployeeId(resultSet.getInt("employee_id"));
                job.setJobDescription(resultSet.getString("job_description"));
                job.setStatus(resultSet.getString("status"));

                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public static Job getJobById(int jobId) {
        Job job = null;
        String sql = "SELECT * FROM jobs WHERE job_id = ?";

        try (
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, jobId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                job = new Job();
                job.setJobId(resultSet.getInt("job_id"));
                job.setOrderId(resultSet.getInt("order_id"));
                job.setEmployeeId(resultSet.getInt("employee_id"));
                job.setJobDescription(resultSet.getString("job_description"));
                job.setStatus(resultSet.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return job;
    }

}