
public class Refrigerator extends Product {
	// Data fields
	private String doorDesign;
    private String color;
    private double capacity;    

    // Parameterized constructor
    public Refrigerator(String name, String doorDesign, String color, double capacity, int quantity, double price, int itemNumber) {
        super(name, quantity, price, itemNumber);
        this.doorDesign = doorDesign;
        this.color = color;
        this.capacity = capacity;
    }

    // Getters and setters
    public String getDoorDesign() {
        return doorDesign;
    }

    public void setDoorDesign(String doorDesign) {
        this.doorDesign = doorDesign;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getStockValue() {
        return getQuantity() * getPrice();
    }

    // Override toString method
    @Override
    public String toString() {
        return "Item number: " + getItemNumber() +
                "\nProduct name: " + getName() +
                "\nDoor design: " + getDoorDesign() +
                "\nColor: " + getColor() +
                "\nCapacity (in Litres): " + getCapacity() +
                "\nQuantity available: " + getQuantity() +
                "\nPrice (RM): " + getPrice() +
                "\nInventory value (RM): " + String.format("%.2f", getStockValue()) +
                "\nProduct status: " + (getStatus() ? "Active" : "Discontinued");
    }
    
    //Testing
    /*public static void main(String[] args) {
	    Refrigerator refrigerator = new Refrigerator(456, "Samsung Refrigerator", 10, 2499.99, "Double-door", "Black", 500);
	    System.out.println(refrigerator.toString()); // should print refrigerator info
	    System.out.println("Inventory value: " + refrigerator.getStockValue()); // should print 24999.9
	    refrigerator.addQuantity(5);
	    System.out.println("New quantity: " + refrigerator.getQuantity()); // should print 15
	    refrigerator.deductQuantity(3);
	    System.out.println("New quantity: " + refrigerator.getQuantity()); // should print 12
    }*/
}
