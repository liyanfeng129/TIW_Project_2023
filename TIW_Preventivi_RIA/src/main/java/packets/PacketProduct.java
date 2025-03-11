package packets;

import java.util.List;

import beans.Option;


public class PacketProduct {
	//TODO modificare in nome, id , 
	private String name;
	private String imgUrl;
	private int id;
	private List<Option> opsList ;
	public PacketProduct(String name, int id, String imgUrl, List<Option> opsList) {
		super();
		this.name = name;
		this.id = id;
		this.imgUrl = imgUrl;
		this.opsList = opsList;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Option> getOpsList() {
		return opsList;
	}
	public void setOpsList(List<Option> opsList) {
		this.opsList = opsList;
	}
	
	
	
}
