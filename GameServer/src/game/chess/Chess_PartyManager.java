package game.chess;

import game.AbsParty;
import game.AbsPartyManager;

import java.util.Observable;

import message.party.ListParties;
import message.party.StateOfParty;

import org.apache.log4j.Logger;

import client.Client;
import client.ClientManager;

public class Chess_PartyManager extends AbsPartyManager {

	private static Chess_PartyManager instance = new Chess_PartyManager();
	private static final Logger logger = Logger.getLogger(Chess_PartyManager.class);

	public static Chess_PartyManager get() {
		return instance;
	}

	private Chess_PartyManager() {
	}

	public boolean addParty(AbsParty p) {
		synchronized (lock) {
			if (getPartyInWhichIsClient(p.getOwner()) != null) {
				return false;
			}
			partys.put(p.getOwner(), p);
			logger.info(partys.size() + " parties with just one added.");
			p.addObserver(this);
			sendListOfPartiesToAll();
			return true;
		}
	}

	public boolean joinParty(int id, Client c) {
		synchronized (lock) {
			if (getPartyInWhichIsClient(c) != null) {
				return false;
			}
			Chess_Party p = null;
			for (AbsParty part : partys.values()) {
				if (part.getId() == id) {
					p = (Chess_Party) part;
				}
			}
			if (p != null) {
				if (p.getStatut() == StateOfParty.WAITING) {
					if (p.addClient(c)) {
						sendListOfPartiesToAll();
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				System.out.println("PARTIE JOIN INEXISTANTE");
				return false;
			}
		}
	}

	public void leftParty(Client c) {
		synchronized (lock) {
			Chess_Party p = (Chess_Party) Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (p != null) {
				/* On supprime le client de la partie */
				logger.info(c.getPlayer().getName() + " has left the party " + p.getName());

				// suppression du client de la partie
				p.removeClient(c);

				if (p.getOwner().equals(c)) {

					// suppression de la partie pour le client owner
					partys.remove(c);
				}

				/* S'il ne reste plus de joueurs dans la partie, on la supprime */
				if (p.getClients().isEmpty()) {
					partys.remove(c);
				}
				else {
					switch (p.getStatut()) {
					/**
					 * Si la partie n'a pas encore ete demarree :
					 * - Si le client parti est le proprietaire, on supprime la
					 * partie
					 * et on la cree a nouveau en changeant de proprietaire.
					 * - Si le client parti n'est pas le proprietaire, on ne
					 * fait rien.
					 * */
						case WAITING:
							if (p.getOwner() == c) {
								p.setOwner(p.getClients().get(0));
								partys.put(p.getClients().get(0), p);
							}
							break;

						/**
						 * Si la partie avait ete demarree, on la passe a l'etat
						 * finished.
						 * De plus, si le client parti est le proprietaire, on
						 * supprime la partie
						 * et on la cree a nouveau en changeant de proprietaire.
						 */
						case RUNNING:
							p.stopParty();
							if (p.getOwner() == c) {
								p.setOwner(p.getClients().get(0));
								partys.put(p.getClients().get(0), p);
							}
							break;
						/**
						 * Si la partie est finie, on la passe a l'etat
						 * finished.
						 * De plus, si le client parti est le proprietaire, on
						 * supprime la partie
						 * et on la cree a nouveau en changeant de proprietaire.
						 */
						case FINISHED:
							if (p.getOwner() == c) {
								p.setOwner(p.getClients().get(0));
								partys.put(p.getClients().get(0), p);
							}
							break;
					}
				}
				sendListOfPartiesToAll();
			}
		}
	}

	public Chess_Party getPartyInWhichIsClient(Client cli) {
		if (partys.get(cli) != null) {
			return (Chess_Party) partys.get(cli);
		}
		else {
			for (AbsParty p : partys.values()) {
				if (p == null)
					continue;
				for (Client c : p.getClients()) {
					if (cli == c) {
						return (Chess_Party) p;
					}
				}
			}
		}
		return null;
	}

	public void sendListOfPartiesToAll() {
		ListParties listParties = new ListParties(Chess_PartyManager.get().getAllPartyInfo());
		logger.info("Sent " + listParties.getListOfParties().size() + " parties");
		ClientManager.get().sendToAll(listParties);
	}

	
	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("ICI");
	}
}
