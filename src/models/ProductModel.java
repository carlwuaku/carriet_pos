package models;

public class ProductModel {
	
	private String name;
	private double price;
	private String category;
	private int id;
	private int deleted;
	private String description;
	
	public int getDeleted() {
		return deleted;
	}
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	
	public ProductModel(String name, double price, String category, int id) {
		super();
		this.name = name;
		this.price = price;
		this.category = category;
		this.id = id;
		this.deleted = 0;
	}
	
	public ProductModel(String name, double price, String category, int id, String description) {
		super();
		this.name = name;
		this.price = price;
		this.category = category;
		this.description = description;
		this.id = id;
		this.deleted = 0;
	}
	
	@Override
	public String toString() {
		return "ProductModel [name=" + name + ", price=" + price + ", category=" + category + ", id=" + id + "]";
	}
	
	/**
	 * generates a string compatible with insert syntax like 'tilapia', 23, 'fish'
	 * @return String
	 */
	public String dbString() {
		return "'"+name+ "',"+price+",'"+category+"'";
	}
	
	
	
	public ProductModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ProductModel(String name, double price, String category) {
		super();
		this.name = name;
		this.price = price;
		this.category = category;
	}
	
	public ProductModel(String name, double price, String category, String description) {
		super();
		this.name = name;
		this.price = price;
		this.category = category;
		this.description = description;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
