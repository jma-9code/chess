package commun;

import java.io.Serializable;

public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Pseudonyme du joueur
	 */
	protected String name = "DEFAULT";

	/**
	 * Pays du joueur représenté sur deux caractères (norme ISO 639-1)
	 */
	protected String country = "FR";

	/**
	 * Score associé à la plateforme de jeu
	 */
	protected int score = 1000;

	/**
	 * Couleur associé au joueur dans le jeu
	 */
	private EColor color;
	
	/**
	 * Le joueur est-il connecté au serveur
	 */
	private EPlayerState state;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public Player(String name, int score, EColor color) {
		this.name = name;
		this.score = score;
		this.color = color;
	}
	
	public Player(String name, int score, String country, EPlayerState state) {
		this.name = name;
		this.score = score;
		this.country = country;
		this.state = state;
	}

	public EPlayerState getState() {
		return state;
	}

	public void setState(EPlayerState state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return name;
	}

	public EColor getColor() {
		return color;
	}

	public void setColor(EColor color) {
		this.color = color;
	}

	@Override
	public boolean equals(Object obj) {
		Player p = (Player) obj;
		return name.equals(p.name) && country.equals(p.country);
	}
}
