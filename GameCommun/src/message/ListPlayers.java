package message;

import java.util.ArrayList;

import commun.Player;

public class ListPlayers implements IMsgClient {

	private static final long serialVersionUID = 1477721087566499631L;
	private ArrayList<Player> listPlayers;

	public ListPlayers() {
		listPlayers = new ArrayList<Player>();
	}

	public ListPlayers(ArrayList<Player> listPlayers) {
		this.listPlayers = listPlayers;
	}

	public ArrayList<Player> getListPlayers() {
		return listPlayers;
	}

	public void setListPlayers(ArrayList<Player> listPlayers) {
		this.listPlayers = listPlayers;
	}

}
