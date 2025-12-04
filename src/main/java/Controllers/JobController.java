package Controllers;

import DatabaseConnection.JobDatabase;
import Models.Job;

import java.util.List;

public class JobController {

    public JobController() {
        // Default constructor
    }

    public static void addJob(Job job) {
        // Validation logic if needed
        JobDatabase.saveJob(job);
    }

    public static void updateJob(Job job) {
        // Validation logic if needed
        JobDatabase.updateJob(job);
    }

    public static void removeJob(int jobId) {
        JobDatabase.deleteJob(jobId);
    }

    public static List<Job> getAllJobs() {
        return JobDatabase.getAllJobs();
    }
}