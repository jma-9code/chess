package game.chess;

import game.AbsParty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import message.Talk2Party;
import message.TechnicalError;
import message.party.EndParty;
import message.party.NullParty;
import message.party.StartParty;
import message.party.StateOfParty;
import message.party.StateOfPlayer;
import message.party.StateOfPlayerChanged;
import message.party.Stroke;

import org.apache.log4j.Logger;

import server.DataBase;
import client.Client;
import client.Client.StateOfClient;
import client.ClientManager;

import commun.AbsPartyInfo;
import commun.EColor;
import commun.Tools;
import commun.chess.Chess_PartyInfo;
import commun.chess.Chess_PartyOption;

public class Chess_Party extends AbsParty {

	private static final Logger logger = Logger.getLogger(Chess_Party.class);
	private HashMap<Client, StateOfPlayer> relClientStatut = new HashMap<Client, StateOfPlayer>(2);
	private final Object lock = new Object();
	private final Object lockGame = new Object();
	private ArrayList<Stroke> StrokesList = new ArrayList<Stroke>();
	private int identifierOfPartyInBDD;
	
	public Chess_Party(Client _owner, String _name) {
		id = nextid++;
		//option = new Puyo_PartyOption();
		owner = _owner;
		name = _name;
		addClient(owner);
		option = new Chess_PartyOption();
	}
	
	public AbsPartyInfo getPartyInfo(){
		synchronized (lock) {
			Chess_PartyInfo info = new Chess_PartyInfo();
			for (Client c : clients){
				info.getPlayers().add(c.getPlayer());
			}
			info.setOption(option);
			info.setId(id);
			info.setStatut(statut);
			info.setName(name);
			return info;
		}
	}
	
	public void setPlayerScore(Client c, int newScore){
		c.getPlayer().setScore(newScore);
		ClientManager.get().update(c, StateOfClient.NEWSCORE);
	}

	public void changeClientState(Client c, StateOfPlayer stateOfPlayer) {
		relClientStatut.put(c, stateOfPlayer);
	}

	public ArrayList<Stroke> getStrokesList() {
		return StrokesList;
	}

	public void setStrokesList(ArrayList<Stroke> strokesList) {
		StrokesList = strokesList;
	}

	public void updateParty(Client c, StateOfPlayerChanged endParty) {
		/* Mise a jour du statut du client dans la partie */
		relClientStatut.put(c, endParty.getStateOfPlayer());
		
		StateOfPlayer stateClient = relClientStatut.get(c);
		StateOfPlayer stateOpponent = relClientStatut.get(getOpponent(c));
		
		if (stateClient != StateOfPlayer.INGAME && stateOpponent != StateOfPlayer.INGAME) {
			if (stateClient == stateOpponent) {
				if (stateClient != StateOfPlayer.NULL) {
					// incoherence
				}
				else {
					stopParty();
				}
			}
			else {
				stopParty();
				updateScores();
			}

		}
		Chess_PartyManager.get().sendListOfPartiesToAll();
	}

	public void updateScores() {
		int oldScore, newScore;
		for (Map.Entry<Client, StateOfPlayer> stateOfClientIterator : relClientStatut.entrySet()) {
			if (stateOfClientIterator.getValue() == StateOfPlayer.LOSE || 
					stateOfClientIterator.getValue() == StateOfPlayer.LEFT) {
				try {
					oldScore = stateOfClientIterator.getKey().getPlayer().getScore();
					/*
					 * On met a jour le score du perdant dans le client et dans
					 * la BDD
					 */
					newScore = Tools.getNewScore(oldScore, getOpponent(stateOfClientIterator.getKey()).getPlayer().getScore(), false);
					DataBase.get().updateScoreOfPlayer(stateOfClientIterator.getKey().getPlayer().getName(), newScore);
					stateOfClientIterator.getKey().getPlayer().setScore(newScore);

					/*
					 * On met a jour le score du gagnant dans le client et dans
					 * la BDD
					 */
					newScore = Tools.getNewScore(getOpponent(stateOfClientIterator.getKey()).getPlayer().getScore(), oldScore, true);
					DataBase.get().updateScoreOfPlayer(getOpponent(stateOfClientIterator.getKey()).getPlayer().getName(), newScore);
					getOpponent(stateOfClientIterator.getKey()).getPlayer().setScore(newScore);
				}
				catch (TechnicalError e) {
					stateOfClientIterator.getKey().sendObject(e);
					getOpponent(stateOfClientIterator.getKey()).sendObject(e);
					e.printStackTrace();
				}
			} 
		}
	}

	public Client getOpponent(Client c) {
		Client opponent = null;
		for (Client cli : clients){
			if (cli == c) continue;
			opponent = cli;
		}
		return opponent;
	}
	
	public void sendMsgToPlayersOfParty(Talk2Party talk2Party) {
		for (Client client : getClients()) {
			client.sendObject(talk2Party);
		}
	}

	public void startParty(){
		synchronized (lock) {
			if (statut != StateOfParty.RUNNING) {
				statut = StateOfParty.RUNNING;
				StartParty startParty = new StartParty();
				Random random = new Random();
				int colorRandom = Math.abs(random.nextInt() % 2);
				if (getClients().size() == 2) {
					getClients().get(0).getPlayer().setColor(EColor.values()[(colorRandom + 1) % 2]);
					getClients().get(1).getPlayer().setColor(EColor.values()[colorRandom]);
					startParty.setPartyInfo((Chess_PartyInfo) getPartyInfo());
					for (Client c : clients) {
						c.sendObject(startParty);
						relClientStatut.put(c, StateOfPlayer.INGAME);
					}
					createPartyInBDD();
					updateObservers(statut);
				}
				else {
					logger.error("Start party nbPlayer < 2");
				}
			}
		}
	}
	
	public void createPartyInBDD() {
		int idWhitePlayer, idBlackPlayer;

		idWhitePlayer = getClients().get(0).getPlayer().getColor() == EColor.WHITE ? 0 : 1;
		idBlackPlayer = (idWhitePlayer + 1) % 2;

		try {
			DataBase.get().addNewParty(getClients().get(idBlackPlayer).getPlayer().getName(), getClients().get(idWhitePlayer).getPlayer().getName(),
					StateOfParty.RUNNING);
			setIdentifierOfPartyInBDD(DataBase.get().getIdOfParty(getClients().get(idBlackPlayer).getPlayer().getName(),
					getClients().get(idWhitePlayer).getPlayer().getName()));
			logger.info("Party created in BDD !");
		}
		catch (TechnicalError e) {
			logger.error("Error while creating party in BDD..");
			// pédale enculer
		}
	}

	public int getIdentifierOfPartyInBDD() {
		return identifierOfPartyInBDD;
	}

	public void setIdentifierOfPartyInBDD(int identifierOfPartyInBDD) {
		this.identifierOfPartyInBDD = identifierOfPartyInBDD;
	}

	public void majPartyInBDD() {
		String whoWins;
		if (getClients().size() == 1) {
			whoWins = getClients().get(0).getPlayer().getName();
		} else {
			if (relClientStatut.get(getClients().get(0)) == relClientStatut.get(getClients().get(1))) {
				whoWins = null;
			} else {
				whoWins = (relClientStatut.get(getClients().get(0)) == StateOfPlayer.WIN) ? 
						getClients().get(0).getPlayer().getName() : getClients().get(1).getPlayer().getName();
			}
		}
			
		try {
			DataBase.get().updateParty(getIdentifierOfPartyInBDD(), getStatut(), whoWins, serializeParty());
			logger.info("Party updated !");
		}
		catch (TechnicalError e) {
			logger.error("Error while updating party !");
			e.printStackTrace();
		}

	}

	public synchronized byte[] serializeParty() {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = null;
		byte[] compressData = null;
		try {
			oo = new ObjectOutputStream(bStream);
			oo.writeObject(getStrokesList());
			oo.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bStream.toByteArray();
	}

	public void handleNullPartyEvent(Client c, NullParty nullParty) {
		if (nullParty.isAccepted()) {
			logger.info("NullParty is accepted.");
			relClientStatut.put(c, StateOfPlayer.NULL);
			relClientStatut.put(getOpponent(c), StateOfPlayer.NULL);
			getOpponent(c).sendObject(nullParty);
			updateScores();
			stopParty();
			Chess_PartyManager.get().sendListOfPartiesToAll();
		}
		else {
			logger.info("NullParty is refused.");
			getOpponent(c).sendObject(nullParty);
		}
	}

	public void stopParty() {
		setStatut(StateOfParty.FINISHED);
		for (Client client : clients) {
			client.sendObject(new EndParty());
		}
		majPartyInBDD();
		logger.info("Sent EndParty (party :" + id + ") for all player IN");
		/*
		 * updateObservers (statut);
		 */
	}
	
	public boolean addClient(Client c){
		synchronized (lock) {
			if (clients.size() == 2) return false;
			if (statut.equals(StateOfParty.WAITING) && !clients.contains(c)) {
				clients.add(c);
				relClientStatut.put(c, StateOfPlayer.WAITING);
				return true;
			}
			return false;
		}
	}
	
	public boolean removeClient(Client c){
		synchronized (lock) {
			relClientStatut.remove(c);
			return clients.remove(c);
		}
	}

	/**
	 * Méthode de notification de tous les clients pour la mise à jour des options de la partie.
	 */
	public void updateOptionsForAll() {
		logger.info("Sent an update of options for party " + id);
		synchronized (lock) {
			for (Client cli : relClientStatut.keySet()) {
				if (cli != null && !cli.equals(owner)) {
					cli.sendObject(option);
				}
			}
		}
	}
}
