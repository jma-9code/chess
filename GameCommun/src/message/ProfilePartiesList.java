package message;

import java.util.ArrayList;

import commun.ProfilePartyInfos;

public class ProfilePartiesList implements IMsgChess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2799926468426099067L;

	private ArrayList<ProfilePartyInfos> partiesList = null;
	private String playerName = "";
	private String playerEmail = "";
	private String playerCountry = "";
	private String subscriptionDate = "";
	private String connectionDate = "";
	private int playerScore;
	private int playerId;

	public ProfilePartiesList() {
		partiesList = new ArrayList<ProfilePartyInfos>();
	}

	public String getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(String subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public String getConnectionDate() {
		return connectionDate;
	}

	public void setConnectionDate(String connectionDate) {
		this.connectionDate = connectionDate;
	}

	public ProfilePartiesList(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getPlayerCountry() {
		return playerCountry;
	}

	public void setPlayerCountry(String playerCountry) {
		this.playerCountry = playerCountry;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public ArrayList<ProfilePartyInfos> getPartiesList() {
		return partiesList;
	}

	public void setPartiesList(ArrayList<ProfilePartyInfos> partiesList) {
		this.partiesList = partiesList;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
}
