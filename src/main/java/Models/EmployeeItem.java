package Models;

public class EmployeeItem {
    private int id;
    private String name;

    public EmployeeItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}