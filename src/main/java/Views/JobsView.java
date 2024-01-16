package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Models.Job;
import Models.EmployeeItem;
import DatabaseConnection.EmployeeDatabase;
import DatabaseConnection.JobDatabase;
import Models.SendMail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class JobsView extends JDialog{
    private JTextField textField1;
    private JButton searchButton;
    private JTextField orderIDTextField;
    private JComboBox<Integer> empSelect;
    private JTextField jobDescriptionTextField;
    private JComboBox JobStatusSelect;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton addButton;
    private JButton clearButton;
    private JTable table1;
    private JPanel JobPane;

    public JobsView() {
        getComponents();
        setTitle("Job Manager");
        setSize(900, 500);
        setContentPane(JobPane);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Add statuses to statusComboBox
        JobStatusSelect.addItem("Pending");
        JobStatusSelect.addItem("In Progress");
        JobStatusSelect.addItem("Completed");
        // Add employees to empSelect
        List<Integer> employeeIds = EmployeeDatabase.getAllEmployeeIds();
        for (Integer employeeId : employeeIds) {
            empSelect.addItem(employeeId);
        }
        addListeners();
        table1.setModel(getAllJobsTableModel());
    }


    private DefaultTableModel getAllJobsTableModel() {
        String[] columnNames = {"Job ID", "Order ID", "Employee ID", "Job Description", "Status"};
        List<Job> jobs = JobDatabase.getAllJobs();
        Object[][] data = new Object[jobs.size()][columnNames.length];
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            data[i][0] = job.getJobId();
            data[i][1] = job.getOrderId();
            data[i][2] = job.getEmployeeId();
            data[i][3] = job.getJobDescription();
            data[i][4] = job.getStatus();
        }
        return new DefaultTableModel(data, columnNames);
    }

    private void addListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int jobId = Integer.parseInt(textField1.getText());
                Job job = JobDatabase.getJobById(jobId);
                if (job != null) {
                    orderIDTextField.setText(String.valueOf(job.getOrderId()));
                    for (int i = 0; i < empSelect.getItemCount(); i++) {
                        Integer employeeId = (Integer) empSelect.getItemAt(i);
                        if (employeeId == job.getEmployeeId()) {
                            empSelect.setSelectedIndex(i);
                            break;
                        }
                    }
                    jobDescriptionTextField.setText(job.getJobDescription());
                    JobStatusSelect.setSelectedItem(job.getStatus());
                } else {
                    JOptionPane.showMessageDialog(null, "No job found with the provided ID");
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Job job = new Job();
                job.setOrderId(Integer.parseInt(orderIDTextField.getText()));
                Integer selectedEmployeeId = (Integer) empSelect.getSelectedItem();
                job.setEmployeeId(selectedEmployeeId);
                job.setJobDescription(jobDescriptionTextField.getText());
                job.setStatus((String)JobStatusSelect.getSelectedItem());
                JobDatabase.saveJob(job);
                table1.setModel(getAllJobsTableModel());

                // Get the employee's email address
                String employeeEmail = EmployeeDatabase.getEmployeeEmailById(selectedEmployeeId);
                // Send the email
                String subject = "New Job Assigned";
                String messageText = "You have been assigned a new job. Description: " + job.getJobDescription();
                SendMail.send(employeeEmail, subject, messageText);
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    Job job = new Job();
                    job.setJobId((Integer)table1.getValueAt(selectedRow, 0));
                    job.setOrderId(Integer.parseInt(orderIDTextField.getText()));
                    Integer selectedEmployeeId = (Integer) empSelect.getSelectedItem();
                    job.setEmployeeId(selectedEmployeeId);
                    job.setJobDescription(jobDescriptionTextField.getText());
                    job.setStatus((String)JobStatusSelect.getSelectedItem());
                    JobDatabase.updateJob(job);
                    table1.setModel(getAllJobsTableModel());
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int jobId = (Integer)table1.getValueAt(selectedRow, 0);
                    JobDatabase.deleteJob(jobId);
                    table1.setModel(getAllJobsTableModel());
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setText("");
                orderIDTextField.setText("");
                empSelect.setSelectedIndex(0);
                jobDescriptionTextField.setText("");
                JobStatusSelect.setSelectedIndex(0);
            }
        });
    }

    public static void main(String[] args) {
        JobsView view = new JobsView();
        view.setVisible(true);
    }
}

