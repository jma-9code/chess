package message.party;

import message.IMsgParty;


public class StateOfPlayerChanged implements IMsgParty {

	private static final long serialVersionUID = 1L;
	private StateOfPlayer stateOfPlayer;
	
	public StateOfPlayerChanged(StateOfPlayer stateOfPlayer) {
		this.stateOfPlayer = stateOfPlayer;
	}

	public StateOfPlayer getStateOfPlayer() {
		return stateOfPlayer;
	}

	public void setStateOfPlayer(StateOfPlayer stateOfPlayer) {
		this.stateOfPlayer = stateOfPlayer;
	}
}
