package commun;

import java.io.Serializable;
import java.util.ArrayList;

import message.party.StateOfParty;

public class AbsPartyInfo implements Serializable {
	private static final long serialVersionUID = 3314996054119405276L;
	protected int id;
	protected String name;
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected StateOfParty statut;
	protected AbsPartyOption option;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public StateOfParty getStatut() {
		return statut;
	}

	public void setStatut(StateOfParty statut) {
		this.statut = statut;
	}

	public AbsPartyOption getOption() {
		return option;
	}

	public void setOption(AbsPartyOption option) {
		this.option = option;
	}
}
