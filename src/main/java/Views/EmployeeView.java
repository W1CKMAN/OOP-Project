import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeView {
    private JPanel Main;
    private JTable table1;
    private JTextField txtName;
    private JTextField txtSalary;
    private JTextField txtMobile;
    private JButton save;
    private JButton update;
    private JButton Delete;
    private JButton search;
    private JTextField txtid;
    private JLabel empName;
    private JLabel Salary;
    private JLabel Mobile;

    public static void main(String[] args) {
        JFrame frame= new JFrame("Employee");
        frame.setContentPane(new EmployeeView().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    Connection con;
    PreparedStatement pst;

    public void connect(){
        try{
            Class.forName("con.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/CarCare", "root","");
            System.out.println("Success");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    void tableLoad()
    {
        try {
            pst = con.prepareStatement("select * from employee");
            ResultSet rs= pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public EmployeeView() {
        connect();
        tableLoad();
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String empName,salary,mobile;

                empName = txtName.getText();
                salary = txtSalary.getText();
                mobile = txtMobile.getText();

                try{
                    pst = con.prepareStatement("insert into employee(empName,salary,mobile) values(?,?,?)");
                    pst.setString(1,empName);
                    pst.setString(2,salary);
                    pst.setString(3,mobile);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Record Added!");
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();
                }
                catch (SQLException e1){
                    e1.printStackTrace();
                }

            }

        });
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    String empid = txtid.getText();

                    pst = con.prepareStatement("select empName,salary,mobile from employee where id = ?");
                    pst.setString(1, empid);
                    ResultSet rs = pst.executeQuery();

                    if(rs.next()==true)
                    {
                        String empName = rs.getString(1);
                        String salary = rs.getString(2);
                        String mobile = rs.getString(3);

                        txtName.setText(empName);
                        txtSalary.setText(salary);
                        txtMobile.setText(mobile);

                    }
                    else
                    {
                        txtName.setText("");
                        txtSalary.setText("");
                        txtMobile.setText("");
                        JOptionPane.showMessageDialog(null,"Invalid Employee No");

                    }
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empid,empName,salary,mobile;
                empName = txtName.getText();
                salary = txtSalary.getText();
                mobile = txtMobile.getText();
                empid = txtid.getText();

                try {
                    pst = con.prepareStatement("update employee set empname = ?,salary = ?,mobile = ? where id = ?");
                    pst.setString(1, empName);
                    pst.setString(2, salary);
                    pst.setString(3, mobile);
                    pst.setString(4, empid);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Updated!");
                    tableLoad();
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empid;
                empid = txtid.getText();

                try {
                    pst = con.prepareStatement("delete from employee  where id = ?");

                    pst.setString(1, empid);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record Deleted!");
                    tableLoad();
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {

                    e1.printStackTrace();
                }
            }
        });
    }
}
