package commun;

import java.io.Serializable;

public class ProfilePartyInfos implements Serializable {
	private static final long serialVersionUID = 4852720869895588030L;
	private ProfilePlayerInfos whitePlayer;
	private ProfilePlayerInfos blackPlayer;
	private ProfilePlayerInfos winner;
	private int identifierOfParty;

	public ProfilePartyInfos() {

	}

	public ProfilePartyInfos(ProfilePlayerInfos whitePlayer, ProfilePlayerInfos blackPlayer, ProfilePlayerInfos winner, int id) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		this.winner = winner;
		identifierOfParty = id;
	}

	public ProfilePlayerInfos getWhitePlayer() {
		return whitePlayer;
	}

	public void setWhitePlayer(ProfilePlayerInfos whitePlayer) {
		this.whitePlayer = whitePlayer;
	}

	public ProfilePlayerInfos getBlackPlayer() {
		return blackPlayer;
	}

	public void setBlackPlayer(ProfilePlayerInfos blackPlayer) {
		this.blackPlayer = blackPlayer;
	}

	public ProfilePlayerInfos getWinner() {
		return winner;
	}

	public void setWinner(ProfilePlayerInfos winner) {
		this.winner = winner;
	}

	public int getIdentifierOfParty() {
		return identifierOfParty;
	}

	public void setIdentifierOfParty(int identifierOfParty) {
		this.identifierOfParty = identifierOfParty;
	}

}
