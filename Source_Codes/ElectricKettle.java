
public class ElectricKettle extends Product{
	private String material;
	private double capacity;
	private int powerRate;
	
	public ElectricKettle(String name, String material, int powerRate, double capacity, int quantity, double price, int itemNumber) {
		super(name, quantity, price, itemNumber);
		this.material = material;
		this.powerRate = powerRate;
		this.capacity = capacity;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public int getPowerRate() {
		return powerRate;
	}

	public void setPowerRate(int powerRate) {
		this.powerRate = powerRate;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
	
	public double getStockValue() {
		return getQuantity() * getPrice();
	}
	@Override
	public String toString() {
		return "Item number: " + getItemNumber() +
				"\nProduct name: " + getName() +
				"\nMaterial: " + getMaterial() +
				"\nCapacity (in Litres): " + getCapacity() +
				"\nRate (W): " + getQuantity() +
				"\nQuantity available: " + getQuantity() +
				"\nPrice (RM): " + getPrice() +
				"\nInventory value (RM): " + String.format("%.2f", getStockValue()) +
				"\nProduct status: " + (getStatus() ? "Active" : "Discontinued");
	}
		
}
