package model;

import gui.GUIErrorsException;
import gui.MainGUI;
import message.party.EndParty;
import message.party.StateOfPlayer;
import message.party.StateOfPlayerChanged;
import message.party.Stroke;
import model.chessmens.Chessmen;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkException;
import network.NetworkManager;

import org.apache.log4j.Logger;

import commun.EColor;
import commun.Player;
import commun.chess.Chess_PartyInfo;
import commun.chess.Chess_PartyOption;

public class PartyNetwork extends Party implements NetworkEventHandler {

	private static final Logger logger = Logger.getLogger(PartyNetwork.class);

	private Chess_PartyInfo info = new Chess_PartyInfo();

	public PartyNetwork(Chess_PartyInfo _info) {
		super();
		info = _info;
		refreshInfoParty();
		initHandlers();
	}

	private void initHandlers() {
		NetworkManager.getInstance().addHandler(this, Stroke.class);
		NetworkManager.getInstance().addHandler(this, EndParty.class);
	}

	@Override
	public void stopGame() {
		super.stopGame();
		NetworkManager.getInstance().removeHandler(this);
		logger.info("End of network party");
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		// Reception d'un coup
		if (obj.getSource() instanceof Stroke) {
			try {
				EColor color = plateau.getWhoColorPlay();
				Stroke receiv = (Stroke) obj.getSource();
				Stroke stroke = plateau.playStroke(receiv);
				// Gestion de la promotion de piece
				Chessmen chessmen = Chessmen.createChessmen(Chessmen.getChessmenClassByChar("" + receiv.getDstIsPromotedTo()), color);
				plateau.havePromotion(stroke, chessmen);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Recepetion de fin de partie
		else if (obj.getSource() instanceof EndParty) {
			stopGame();
		}
	}

	public Chess_PartyInfo getInfo() {
		return info;
	}

	public void refreshInfoParty() {
		for (Player player : players) {
			deletePlayer(player);
		}
		if (info.getPlayers().size() == 2) {
			// Le joueur mis en position 0 est le blanc
			if (info.getPlayers().get(0).getColor() == EColor.WHITE) {
				addPlayer(info.getPlayers().get(0));
				addPlayer(info.getPlayers().get(1));
			}
			else {
				addPlayer(info.getPlayers().get(1));
				addPlayer(info.getPlayers().get(0));
			}

			Chess_PartyOption option = (Chess_PartyOption) info.getOption();
			plateau.defineTime(option.getMaxTimePerPlayer() * 60);
		}
		else {
			new GUIErrorsException("Problème lors du lancement de la partie", "Il n'y a pas assez de joueurs dans la partie.").showErrors(MainGUI
					.getInstance());
		}
	}

	@Override
	protected void stateStatutChanged() {
		super.stateStatutChanged();

		switch (statut) {
			case FINISHED:
				// Fin de partie
				if (plateau.isBlackIsEchecMAT() || plateau.isWhiteIsEchecMAT() || plateau.isGameNull()) {
					logger.info("Notification réseau de fin de partie");
					Player partyPlayer = getCurrentPlayer();
					StateOfPlayer state = plateau.getStateOfPlayer(partyPlayer.getColor());
					NetworkClient.getInstance().sendObject(new StateOfPlayerChanged(state));
				}
				break;
		}
	}

	public void setInfo(Chess_PartyInfo info) {
		this.info = info;
		refreshInfoParty();
	}

	/**
	 * Retourne l'instance du player dans la partie correspondant au client.
	 * 
	 * @return
	 */
	public Player getCurrentPlayer() {
		try {
			Player pClient = NetworkClient.getInstance().getPlayer();
			for (Player p : info.getPlayers()) {
				if (p.equals(pClient)) {
					return p;
				}
			}
		}
		catch (NetworkException e) {
			logger.error("Client player not available : " + e.getMessage());
		}

		return null;
	}
}
