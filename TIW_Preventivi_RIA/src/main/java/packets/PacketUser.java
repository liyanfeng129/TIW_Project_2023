package packets;

public class PacketUser {

	private String name;
	private int id;
	private String permission;
	public PacketUser(String name, int id, String permission) {
		super();
		this.name = name;
		this.id = id;
		this.permission = permission;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
}
