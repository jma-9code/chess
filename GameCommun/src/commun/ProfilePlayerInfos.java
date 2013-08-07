package commun;

import java.io.Serializable;

public class ProfilePlayerInfos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -700202673078431223L;
	private String playerName = "";
	private String playerCountry = "";
	private int playerScore;

	public ProfilePlayerInfos() {

	}

	public ProfilePlayerInfos(String name, String country, int score) {
		playerName = name;
		playerCountry = country;
		playerScore = score;
	}

	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
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
}
