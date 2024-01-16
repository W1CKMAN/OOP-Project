package Controllers;

import DatabaseConnection.JobDatabase;
import Models.Job;

import java.util.List;

public class JobController {
    private static JobDatabase jobDatabase;

    public JobController(JobDatabase jobDatabase) {
        this.jobDatabase = jobDatabase;
    }

    public static void addJob(Job job) {
        // Validation logic if needed
        jobDatabase.saveJob(job);
    }

    public static void updateJob(Job job) {
        // Validation logic if needed
        jobDatabase.updateJob(job);
    }

    public static void removeJob(int jobId) {
        jobDatabase.deleteJob(jobId);
    }

    public List<Job> getAllJobs() {
        return jobDatabase.getAllJobs();
    }
}