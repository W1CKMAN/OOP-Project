package Views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import DatabaseConnection.EmployeeDatabase;
import Models.Employee;


public class EmployeeView extends JDialog{
    private JTextField EmpIdField;
    private JButton searchButton;
    private JTextField EmpNameField;
    private JTextField EmpPhoneField;
    private JTextField EmpEmailField;
    private JTextField EmpPostField;
    private JButton addButton;
    private JButton updateButton;
    private JButton removeButton;
    private JButton clearButton;
    private JTable table1;
    private JPanel EmployeePane;
    private JScrollPane EmpTable;

    public EmployeeView() {
        getComponents();
        setTitle("Employee Manager");
        setSize(900, 500);
        setContentPane(EmployeePane);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addListeners();
        table1.setModel(getAllEmployeesTableModel());
    }
    private DefaultTableModel getAllEmployeesTableModel() {
        String[] columnNames = {"Employee ID", "Employee Name", "Contact Number", "Email", "Position"};
        List<Employee> employees = EmployeeDatabase.getAllEmployees();
        Object[][] data = new Object[employees.size()][columnNames.length];
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            data[i][0] = employee.getEmployeeId();
            data[i][1] = employee.getEmployeeName();
            data[i][2] = employee.getContactNumber();
            data[i][3] = employee.getEmail();
            data[i][4] = employee.getPosition();
        }
        return new DefaultTableModel(data, columnNames);
    }

    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Employee employee = new Employee();
                    employee.setEmployeeName(EmpNameField.getText());
                    employee.setContactNumber(EmpPhoneField.getText());
                    employee.setEmail(EmpEmailField.getText());
                    employee.setPosition(EmpPostField.getText());
                    EmployeeDatabase.saveEmployee(employee);
                    refreshTable();
                }
            });
        updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table1.getSelectedRow();
                    if (selectedRow >= 0) {
                        Employee employee = new Employee();
                        employee.setEmployeeId((Integer) table1.getValueAt(selectedRow, 0));
                        employee.setEmployeeName(EmpNameField.getText());
                        employee.setContactNumber(EmpPhoneField.getText());
                        employee.setEmail(EmpEmailField.getText());
                        employee.setPosition(EmpPostField.getText());
                        EmployeeDatabase.updateEmployee(employee);
                        refreshTable();
                    }
                }
            });
        removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table1.getSelectedRow();
                    if (selectedRow >= 0) {
                        int employeeId = (Integer) table1.getValueAt(selectedRow, 0);
                        EmployeeDatabase.deleteEmployee(employeeId);
                        refreshTable();
                    }
                }
            });
        clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EmpIdField.setText("");
                    EmpNameField.setText("");
                    EmpPhoneField.setText("");
                    EmpEmailField.setText("");
                    EmpPostField.setText("");
                }
            });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int employeeId = Integer.parseInt(EmpIdField.getText());
                Employee employee = EmployeeDatabase.getEmployeeById(employeeId);
                if (employee != null) {
                    EmpNameField.setText(employee.getEmployeeName());
                    EmpPhoneField.setText(employee.getContactNumber());
                    EmpEmailField.setText(employee.getEmail());
                    EmpPostField.setText(employee.getPosition());
                }
            }
        });
    }

        private void refreshTable() {
            DefaultTableModel model = getAllEmployeesTableModel();
            table1.setModel(model);
            model.fireTableDataChanged();
        }

    public static void main(String[] args) {
        EmployeeView employeeView = new EmployeeView();
        employeeView.setVisible(true);
    }
}