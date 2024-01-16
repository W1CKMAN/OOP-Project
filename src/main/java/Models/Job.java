package Models;

public class Job {
    private int jobId;
    private int orderId;
    private int employeeId;
    private String jobDescription;
    private String status;

    public Job() {
    }

    public Job(int orderId, int employeeId, String jobDescription, String status) {
        this.orderId = orderId;
        this.employeeId = employeeId;
        this.jobDescription = jobDescription;
        this.status = status;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}