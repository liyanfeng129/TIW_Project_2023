package packets;

import java.util.List;

import beans.Option;
import beans.Preventive;

public class PacketPreventivoDetails {

	private Preventive preventive;
	private List<Option> listOption;
	public PacketPreventivoDetails(Preventive preventive, List<Option> listOption) {
		super();
		this.preventive = preventive;
		this.listOption = listOption;
	}
	public Preventive getPreventive() {
		return preventive;
	}
	public void setPreventive(Preventive preventive) {
		this.preventive = preventive;
	}
	public List<Option> getListOption() {
		return listOption;
	}
	public void setListOption(List<Option> listOption) {
		this.listOption = listOption;
	}
	

	

}
