package message;

import java.util.regex.Pattern;

import commun.EGames;
import commun.Player;

public class Authentication implements IMsg {

	private static final long serialVersionUID = 1L;

	/* Indique si c'est une identification ou une inscription */
	public boolean login = false;
	public boolean subscription = false;

	private String password = null;
	private String username = null;
	private String email = null;
	private String countryCode = null;
	private Player player;
	private EGames game;
	
	public Authentication(Player _player) {
		player = _player;
	}
	
	public Authentication(String username, String pwd, String countryCode, String email, EGames game) {
		this.username = username;
		this.password = pwd;
		this.countryCode = countryCode;
		this.email = email;
		this.game = game;
	}

	public boolean isMailValid() {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		return (p.matcher(email).matches());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public EGames getGame() {
		return game;
	}

	public void setGame(EGames game) {
		this.game = game;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isSubscription() {
		return subscription;
	}

	public void setSubscription(boolean subscription) {
		this.subscription = subscription;
	}
}
