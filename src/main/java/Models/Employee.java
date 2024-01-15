package Models;

public class Employee {
    private static int nextempid = 1;
    private int empid;
    private String empName;
    private String mobile;
    private String salary;

    public Employee() {
    }

    public Employee(String empName, String mobile, String salary) {
        this.empid = nextempid++;
        this.empName = empName;
        this.mobile = mobile;
        this.salary = salary;
    }

    public int getempid() {
        return empid;
    }

    public void setempid(int empid) {
        this.empid = empid;
    }

    public String getempName() {
        return empName;
    }

    public void setempName(String empName) {
        this.empName = empName;
    }

    public String getmobile() {
        return mobile;
    }

    public void setmobile(String mobile) {
        this.mobile = mobile;
    }

    public String getsalary() {
        return salary;
    }

    public void setsalary(String salary) {
        this.salary = salary;
    }

    public static int getNextempid() {
        return nextempid;
    }
}