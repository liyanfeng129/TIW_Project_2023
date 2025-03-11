package beans;

public class Option {
	private int preventiveId;
	private int productId;
	private int id;
	private String name;
	private String type;
	public Option()
	{
		this.name = null;
		this.type = null;
		preventiveId = -1;
		productId = -1;
	}
	public int getPreventiveId() {
		return preventiveId;
	}
	public void setPreventiveId(int preventiveId) {
		this.preventiveId = preventiveId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
	
}
