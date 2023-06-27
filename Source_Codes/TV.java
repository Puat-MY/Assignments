
public class TV extends Product {
	// Data fields
    private String screenType;
    private String resolution;
    private double displaySize;

    // Parameterized constructor
    public TV(String name, String screenType, String resolution, double displaySize, int quantity, double price, int itemNumber) {
        super(name, quantity, price, itemNumber);
        this.screenType = screenType;
        this.resolution = resolution;
        this.displaySize = displaySize;
    }

    // Getters and setters
    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public double getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(double displaySize) {
        this.displaySize = displaySize;
    }

    public double getStockValue() {
        return getQuantity() * getPrice();
    }

    // Override toString method
    @Override
    public String toString() {
        return "Item number: " + getItemNumber() +
                "\nProduct name: " + getName() +
                "\nScreen type: " + getScreenType() +
                "\nResolution: " + getResolution() +
                "\nDisplay size: " + getDisplaySize() +
                "\nQuantity available: " + getQuantity() +
                "\nPrice (RM): " + getPrice() +
                "\nInventory value (RM): " + String.format("%.2f", getStockValue()) +
                "\nProduct status: " + (getStatus() ? "Active" : "Discontinued");
    }
    
    //Testing
    /*public static void main(String[] args) {
	    TV tv = new TV(789, "LG TV", 20, 1999.99, "LED", "4K", 55);
	    System.out.println(tv.toString()); // should print TV info
	    System.out.println("Inventory value: " + tv.getStockValue()); // should print 39999.8
	    tv.addQuantity(10);
	    System.out.println("New quantity: " + tv.getQuantity()); // should print 30
	    tv.deductQuantity(7);
	    System.out.println("New quantity: " + tv.getQuantity()); // should print 23
	}*/
}