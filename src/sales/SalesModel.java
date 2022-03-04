package sales;

import javafx.scene.control.Button;

public class SalesModel {
	private String productName;
	private int quantity;
	private double price;
	private double total;
	private int onhand;
	private Button remove;
	private Button increment;
	private Button decrement;
	public Button getIncrement() {
		return increment;
	}
	public void setIncrement(Button increment) {
		this.increment = increment;
	}
	public Button getDecrement() {
		return decrement;
	}
	public void setDecrement(Button decrement) {
		this.decrement = decrement;
	}
	public Button getRemove() {
		return remove;
	}
	public void setRemove(Button remove) {
		this.remove = remove;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getOnhand() {
		return onhand;
	}
	public void setOnhand(int onhand) {
		this.onhand = onhand;
	}
	public SalesModel(String productName, int quantity, double price, double total, int onhand) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.total = total;
		this.onhand = onhand;
	}
	
	public SalesModel() {
		super();
		
	}
	

}
