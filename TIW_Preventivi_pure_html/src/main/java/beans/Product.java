package beans;

public class Product {
	private int id;
	private String name;
	private String imgUrl;
	
	public Product()
	{
		this.id = -1;
		this.name = "";
		this.imgUrl = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int productId) {
		this.id = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	

}
