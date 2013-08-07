package message.party;

import message.IMsgParty;

public class NewParty implements IMsgParty {

	private static final long serialVersionUID = 1L;
	private int idParty;
	private String name;
	private boolean partyCreated = false;
	
	public NewParty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getIdParty() {
		return idParty;
	}

	public void setIdParty(int idParty) {
		this.idParty = idParty;
	}

	public boolean isCreated() {
		return partyCreated;
	}

	public void setCreated(boolean created) {
		this.partyCreated = created;
	}

	public void setName(String name) {
		this.name = name;
	}

}
