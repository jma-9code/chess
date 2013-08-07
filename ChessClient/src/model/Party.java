package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;

import message.party.StateOfParty;
import message.party.Stroke;

import org.apache.log4j.Logger;

import commun.Player;

public class Party extends Observable {

	private static final Logger logger = Logger.getLogger(Party.class);

	protected int id;
	protected String name;
	protected ArrayList<Player> players = new ArrayList<Player>(2);
	protected Chessboard plateau;
	protected StateOfParty statut = StateOfParty.WAITING;

	private final Object lock = new Object();

	public Party() {
		plateau = new Chessboard(this);
	}

	public Party(int id, boolean replay) {
		setId(id);
		plateau = new Chessboard(this, replay);
	}

	public Party(int _id) {
		this();
		setId(_id);
	}

	public void stopGame() {
		plateau.endGame();
		// players.clear();
	}

	public static void saveParty(Party p, String file) {
		try {
			// ouverture d'un flux de sortie vers le fichier "personne"
			FileOutputStream fos = new FileOutputStream(file);
			// création d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			try {
				oos.writeObject(p.getPlayers());
				oos.writeObject(p.getPlateau().getStrokesPlayed());
				// on vide le tampon
				oos.flush();
			}
			finally {
				// fermeture des flux
				try {
					oos.close();
				}
				finally {
					fos.close();
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static Party loadParty(String file) throws Exception {
		Party party = new Party(0, true);
		// ouverture d'un flux d'entrée depuis le fichier
		FileInputStream fis = new FileInputStream(file);
		// création d'un "flux objet" avec le flux fichier
		ObjectInputStream ois = new ObjectInputStream(fis);
		party.setPlayers((ArrayList<Player>) ois.readObject());
		party.getPlateau().setStrokesPlayed((ArrayList<Stroke>) ois.readObject());
		ois.close();
		fis.close();
		return party;
	}

	public Player getPlayer(String name) {
		for (Player p : players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public void addPlayer(Player p) {
		synchronized (lock) {
			if (players.size() >= 2) {
				return;
			}
			players.add(p);
		}
	}

	public void deletePlayer(Player p) {
		synchronized (lock) {
			players.remove(p);
		}
	}

	protected void stateStatutChanged() {
		switch (statut) {
			case WAITING:
				break;
			case RUNNING:
				break;
			case FINISHED:
				// S'assure que tout est bien arreter
				stopGame();
				break;
		}
	}

	public StateOfParty getStatut() {
		return statut;
	}

	public void setStatut(StateOfParty statut) {
		if (this.statut != statut) {
			this.statut = statut;
			stateStatutChanged();
		}
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

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public Chessboard getPlateau() {
		return plateau;
	}

	public void setPlateau(Chessboard plateau) {
		this.plateau = plateau;
	}

}
