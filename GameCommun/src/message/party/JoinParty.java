package message.party;

import message.IMsgParty;


public class JoinParty implements IMsgParty {

	private static final long serialVersionUID = 1L;
	private int id;
	private boolean partyJoined = false;
	
	public boolean isPartyJoined() {
		return partyJoined;
	}

	public void setPartyJoined(boolean partyJoined) {
		this.partyJoined = partyJoined;
	}

	public JoinParty(int _id){
		setId(_id);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
