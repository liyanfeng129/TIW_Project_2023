package beans;


public class Preventive {
	


	private int id;
	private int clientId;
	private int operatorId;
	private String clientName;
	private String operatorName;
	private String productName;
	private String imgUrl;
	private String creationDate;
	private float price;
	public Preventive() {
		this.id = -1;
		this.clientId = -1;
		this.clientName = "";
		this.operatorName = "";
		this.productName = "";
		this.creationDate = null;
		this.imgUrl = "";
		this.operatorId = -1;
		this.price = 0;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getClientName() {
		return clientName;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getId() {
		return id;
	}
	public void setId(int preventiveId) {
		this.id = preventiveId;
	}
	
	
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationTime) {
		this.creationDate = creationTime;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}


	
	
}
