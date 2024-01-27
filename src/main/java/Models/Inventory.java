package Models;

public class Inventory {
    private static int nextproductId = 1;
    private int productId;
    private String name;
    private int quantity;
    private double price;

    public Inventory() {
    }

    public Inventory(String name, int quantity, double price) {
        this.productId = nextproductId++;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static int getnextproductId() {
        return nextproductId;
    }
}
