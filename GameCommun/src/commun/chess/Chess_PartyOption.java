package commun.chess;

import commun.AbsPartyOption;

public class Chess_PartyOption extends AbsPartyOption{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int maxTimePerPlayer = 10;
	
	public Chess_PartyOption(){
		
	}

	public Chess_PartyOption ( int maxTimePerPlayer ) {
		super();
		this.maxTimePerPlayer = maxTimePerPlayer;
	}

	public int getMaxTimePerPlayer() {
		return maxTimePerPlayer;
	}

	public void setMaxTimePerPlayer(int maxTimePerPlayer) {
		this.maxTimePerPlayer = maxTimePerPlayer;
	}

	@Override
	public String toString() {
		return "Tps=" + maxTimePerPlayer;
	}

}
