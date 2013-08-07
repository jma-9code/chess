package game.chess;

import message.Error;
import message.Error.ErrorType;
import message.ProfileEdit;
import message.ProfilePartiesList;
import message.TechnicalError;
import message.party.JoinParty;
import message.party.LeftParty;
import message.party.ListParties;
import message.party.NewParty;
import message.party.NullParty;
import message.party.StartParty;
import message.party.StateOfParty;
import message.party.StateOfPlayer;
import message.party.StateOfPlayerChanged;
import message.party.Stroke;

import org.apache.log4j.Logger;

import server.DataBase;
import client.Client;

import commun.AbsPartyOption;

public class Chess_Network {
	private static final Logger logger = Logger.getLogger(Chess_Network.class);
	public final static int PASSWORD_MINIMAL_SIZE = 4;

	public static void manageChessPlayer(Client c, Object obj) {
		if (obj instanceof ProfilePartiesList) {
			ProfilePartiesList profilePartiesList = (ProfilePartiesList) obj;
			try {
				String name = profilePartiesList.getPlayerName();
				System.out.println(name);
				profilePartiesList.setPlayerId(DataBase.get().getIdentifierOfPlayer(name));
				profilePartiesList.setPlayerEmail(DataBase.get().getEmailOfPlayer(name));
				profilePartiesList.setPlayerCountry(DataBase.get().getCountryOfPlayer(name).toUpperCase());
				profilePartiesList.setPlayerScore(DataBase.get().getScoreOfPlayer(name));
				profilePartiesList.setSubscriptionDate(DataBase.get().getSubscriptionDateOfPlayer(name));
				profilePartiesList.setConnectionDate(DataBase.get().getConnectionDateOfPlayer(name));
				DataBase.get().getPartiesOfPlayer(profilePartiesList);
				c.sendObject(profilePartiesList);
			}
			catch (TechnicalError technicalError) {
				c.sendObject(technicalError);
				logger.error("Sent an technical error.");
				logger.error(technicalError.getMessage());
			}
		}
		else if (obj instanceof Stroke) {
			logger.info("Client " + c.getPlayer().getName() + " has sent a stroke.");
			Chess_Party party = (Chess_Party) Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (party.getStatut() == StateOfParty.RUNNING) {
				party.getStrokesList().add((Stroke) obj);
				party.getOpponent(c).sendObject(obj);
			}
		}
		else if (obj instanceof ProfileEdit) {
			ProfileEdit profileEdit = (ProfileEdit) obj;
			Error error = new Error();
			String name = profileEdit.getUsername();
			logger.info("Reception of a ProfileEdit object.");
			if (profileEdit.isChgPwd()) {
				try {
					logger.info("ProfileEdit for password change.");
					String hashOldPassword = Client.sha256(profileEdit.getOldPassword());
					String hashNewPassword = Client.sha256(profileEdit.getNewPassword());
					if (DataBase.get().getHashPasswordOfPlayer(name).equals(hashOldPassword)) {
						if (profileEdit.getNewPassword().length() > PASSWORD_MINIMAL_SIZE) {
							DataBase.get().updatePasswordOfPlayer(name, hashNewPassword);
							c.sendObject(profileEdit);
							logger.info("Password changed.");
						}
						else {
							error.addErrorType(ErrorType.TOO_SHORT_PASSWORD);
							c.sendObject(error);
							logger.error("Sent an error.");
						}
					}
					else {
						error.addErrorType(ErrorType.INCORRECT_PASSWORD);
						c.sendObject(error);
						logger.error("Sent an error.");
					}
				}
				catch (TechnicalError technicalError) {
					c.sendObject(technicalError);
					logger.error("Sent an technical error.");
					logger.error(technicalError.getMessage());
				}
			}
			else if (profileEdit.isEdit()) {
				logger.info("ProfileEdit for edition.");
				try {
					if (!profileEdit.getEmail().isEmpty()) {
						if (profileEdit.isMailValid()) {
							DataBase.get().updateMailOfPlayer(name, profileEdit.getEmail());
							logger.info("Email changed.");
						}
						else {
							error.addErrorType(ErrorType.INCORRECT_EMAIL);
							c.sendObject(error);
							logger.error("Sent an error.");
						}
					}
					if (!error.hasError()) {
						if (!profileEdit.getCountry().isEmpty()) {
							DataBase.get().updateCountryOfPlayer(name, profileEdit.getCountry());
							logger.info("Country changed.");
						}
						c.sendObject(profileEdit);
					}
				}
				catch (TechnicalError technicalError) {
					c.sendObject(technicalError);
					logger.error("Sent an technical error.");
					logger.error(technicalError.getMessage());
				}
			}
		}
	}

	public static void manageChessParties(Client c, Object obj) {
		if (obj instanceof ListParties) {
			logger.info("Client " + c.getPlayer().getName() + " requests the list of parties.");
			((ListParties) obj).setListOfParties(Chess_PartyManager.get().getAllPartyInfo());
			c.sendObject(obj);
		}
		else if (obj instanceof NewParty) {
			/*
			 * Une demande de création de partie est demandée, on renvoie la
			 * confirmation de création avec l'id de la partie affectée. Puis on
			 * met à jour la liste des parties pour tous les clients.
			 */
			logger.info("Client " + c.getPlayer().getName() + " wants to create a new party " + "titled " + ((NewParty) obj).getName()
					+ ".");
			Chess_Party party = new Chess_Party(c, ((NewParty) obj).getName());
			((NewParty) obj).setCreated(Chess_PartyManager.get().addParty(party));
			((NewParty) obj).setIdParty(Chess_PartyManager.get().getPartyInWhichIsClient(c).getId());
			logger.info("ID = " + ((NewParty) obj).getIdParty());
			c.sendObject(obj);
		}
		else if (obj instanceof AbsPartyOption) {
			Chess_Party party = Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (party != null) {
				logger.debug("Option party update : " + obj);
				party.setOption((AbsPartyOption) obj);
				party.updateOptionsForAll();
			}
			else {
				logger.error("Option party update received, but no party is linked to the client.");
			}
		}
		else if (obj instanceof JoinParty) {
			logger.info("Client " + c.getPlayer().getName() + " wants to join the party " + ((JoinParty) obj).getId());
			((JoinParty) obj).setPartyJoined(Chess_PartyManager.get().joinParty(((JoinParty) obj).getId(), c));
			c.sendObject(obj);
		}
		else if (obj instanceof StartParty) {
			logger.info("Client " + c.getPlayer().getName() + " has started its party "
					+ Chess_PartyManager.get().getPartys().get(c).getName() + ".");
			Chess_PartyManager.get().getPartys().get(c).startParty();

		}
		else if (obj instanceof LeftParty) {
			logger.info("Client " + c.getPlayer().getName() + " has sent an event LeftParty from the party "
					+ Chess_PartyManager.get().getPartyInWhichIsClient(c).getName() + ".");
			Chess_Party p = Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (p != null) {
				if (p.getStatut() == StateOfParty.RUNNING) {
					p.changeClientState(c, StateOfPlayer.LEFT);
					p.updateScores();
				}
				Chess_PartyManager.get().leftParty(c);
			}
		}
		else if (obj instanceof StateOfPlayerChanged) {
			logger.info("Client " + c.getPlayer().getName() + " has sent an event stateOfPlayerChanged "
					+ ((StateOfPlayerChanged) obj).getStateOfPlayer() + " in the party "
					+ Chess_PartyManager.get().getPartyInWhichIsClient(c).getName() + ".");
			Chess_Party p = Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (p != null) {
				p.updateParty(c, (StateOfPlayerChanged) obj);
			}
		}
		else if (obj instanceof NullParty) {
			logger.info("Client " + c.getPlayer().getName() + " has sent an event NullParty in the party "
					+ Chess_PartyManager.get().getPartyInWhichIsClient(c).getName() + ".");
			Chess_Party p = Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (p != null) {
				if (((NullParty) obj).isQuestion()) {
					logger.info("The NullParty event is a question.");
					p.getOpponent(c).sendObject(obj);
				}
				else {
					logger.info("The NullParty event is an answer.");
					p.handleNullPartyEvent(c, (NullParty) obj);
				}
			}
		}
	}
}
