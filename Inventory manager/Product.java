
public class Product {
    // Data fields
    private String name;
    private double price;
    private int quantity;
    private int itemNumber;
    private boolean status;

    // Default constructor
    public Product() {
        this.name = "";
        this.price = 0;
        this.quantity = 0;
        this.itemNumber = 0;
        this.status = true;
    }

    // Parameterized constructor
    public Product(String name, int quantity, double price, int itemNumber) {
        this.itemNumber = itemNumber;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.status = true;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean active) {
        status = active;
    }

    // Instance methods
    public double getInventoryValue() {
        return price * quantity;
    }

    public void addQuantity(int amount) {
        if (status) {
            quantity += amount;
        }
    }

    public void deductQuantity(int amount) {
        if (status && quantity - amount >= 0) {
            quantity -= amount;
        }
    }

    // Override toString method
    @Override
    public String toString() {
    	/*String status = this.status ? "Active" : "Discontinued";*/ //put in return much better right?
        return "Item number		: " + itemNumber +
                "\nProduct name		: " + name +
                "\nQuantity available	: " + quantity +
                "\nPrice (RM)		: " + price +
                "\nInventory value (RM)	: " + String.format("%.2f", getInventoryValue()) +
                "\nProduct status		: " + (status ? "Active" : "Discontinued");
    }
    
    //Testing
    /*public static void main(String[] args) {
        Product product = new Product(123, "T-shirt", 50, 25.99);
        System.out.println(product.toString()); // should print product info
        System.out.println("Inventory value: " + product.getInventoryValue()); // should print 1299.5
        product.addQuantity(10);
        System.out.println("New quantity: " + product.getQuantity()); // should print 60
        product.deductQuantity(5);
        System.out.println("New quantity: " + product.getQuantity()); // should print 55
    }*/
}
