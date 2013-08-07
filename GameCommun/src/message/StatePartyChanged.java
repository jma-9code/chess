package message;


public class StatePartyChanged implements IMsgChess {

	private static final long serialVersionUID = -6101667955842834630L;
	private Change change;
	
	public enum Change {
		SURRENDER,
		DISCONNECTION,
		CHECKMATE,
		NULL		
	};
	
	public StatePartyChanged (Change change) {
		this.change = change;
	}

	public Change getChange() {
		return change;
	}

	public void setChange(Change change) {
		this.change = change;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}



