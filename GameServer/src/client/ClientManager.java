package client;

import game.chess.Chess_PartyManager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import message.ListPlayers;
import message.TechnicalError;
import message.party.ListParties;

import org.apache.log4j.Logger;

import server.DataBase;
import client.Client.StateOfClient;

import commun.Player;

public class ClientManager implements Observer {
	
	private static final Logger logger = Logger.getLogger(ClientManager.class);
	private static ClientManager instance = new ClientManager();
	private ArrayList<Client> clients = new ArrayList<Client>();
	private static final Object lock = new Object();

	private ClientManager () {

	}

	public static ClientManager get(){
		return instance;
	}

	public void addClient(Client c) {
		synchronized (lock) {
			logger.info("Client authentifie : " + c.getPlayer().getName());
			clients.add(c);
			sendListOfPlayersToAll();
		}
		ListParties listParties = new ListParties(Chess_PartyManager.get().getAllPartyInfo());
		c.sendObject(listParties);
	}

	public void removeClient(Client c) {
		synchronized (lock) {
			if (clients.contains(c) ) {
				clients.remove(c);
				logger.info("Client deconnexion : " + c.getPlayer().getName());
				sendListOfPlayersToAll();
			}
			Chess_PartyManager.get().leftParty(c);
		}
	}

	public void removeClients() {
		synchronized (lock) {
			boolean removed = false;
			for ( int i = 0; i < clients.size(); i++ ) {
				Client c = clients.get(i);
				c.interrupt();
				removed = true;
			}
			clients.clear();
			logger.info("Suppression de tous les clients du serveur");
		}
	}

	public ArrayList<Player> getListPlayer() {
		synchronized (lock) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Client c : clients){
				players.add(c.getPlayer());
			}
			return players;
		}
	}
	
	public Client getClientByName(String name){
		synchronized (lock) {
			for (Client c : clients){
				if (c.getPlayer().getName().equalsIgnoreCase(name)){
					return c;
				}
			}
			return null;
		}
	}
	
	public static void manageClients(Client c, Object obj) {
		if (obj instanceof ListPlayers) {
			logger.info("Client " + c.getInetAddress() + ":" + c.getPort() + " requests the list of clients.");
			((ListPlayers) obj).setListPlayers(ClientManager.get().getListPlayer());
			c.sendObject(obj);
		}
	}

	public void sendToAll(Object obj) {
		synchronized (lock) {
			for (Client c : clients){
				c.sendObject(obj);
			}
		}
	}

	public void sendListOfPlayersToAll() {
		ListPlayers listPlayers = new ListPlayers(ClientManager.get().getListPlayer());
		sendToAll(listPlayers);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof StateOfClient) {
			switch ((StateOfClient) arg1) {
				case TRY_CONNECT:
					logger.info("Attempt to connect : " + ((Client) arg0).getInetAddress() + ":"
 + ((Client) arg0).getPort());
				break;
				case AUTHENTICATION_FAIL:
					logger.error("Echec lors de l'authentification : " + ((Client) arg0).getInetAddress());
					break;
				case AUTHENTICATION_SUCCESS:
					addClient((Client)arg0);
					break;
				case DISCONNECT:
					logger.error("Client has disconnected !");
					removeClient((Client)arg0);
					break;
				case NEWSCORE:
					logger.info("MAJ Score : " + ((Client) arg0).getPlayer().getName());
					try {
						DataBase.get().updateScoreOfPlayer(((Client) arg0).getPlayer().getName(), ((Client) arg0).getPlayer().getScore());
					}
					catch (TechnicalError e) {
						((Client) arg0).sendObject(e);
					}
					sendListOfPlayersToAll();
				break;
				default:
					break;
			}
		}
		
	}

}