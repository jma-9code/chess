package message.party;

import message.IMsgParty;

import commun.chess.Chess_PartyInfo;


public class StartParty implements IMsgParty {

	private static final long serialVersionUID = 8107518438928290713L;
	private Chess_PartyInfo partyInfo;

	public StartParty() {

	}

	public Chess_PartyInfo getPartyInfo() {
		return partyInfo;
	}
	public void setPartyInfo(Chess_PartyInfo partyInfo) {
		this.partyInfo = partyInfo;
	}

}
