package game;

import game.chess.Chess_Party;
import game.chess.Chess_PartyManager;
import message.Talk2All;
import message.Talk2Party;

import org.apache.log4j.Logger;

import client.Client;
import client.ClientManager;

public class Tchat {

	private static final Logger logger = Logger.getLogger(Tchat.class);

	public static void manageTchat(Client c, Object obj) {
		// Msg public
		if (obj instanceof Talk2All) {
			logger.info("Sent the message \"" + ((Talk2All) obj).getMessage() + "\" in broadcast.");
			Talk2All msg2All = (Talk2All) obj;
			msg2All.setMessage(c.getPlayer().getName() + " : " + msg2All.getMessage());
			ClientManager.get().sendToAll(msg2All);
		}

		// Msg prive
		else if (obj instanceof Talk2Party) {
			logger.info("Sent the message \"" + ((Talk2Party) obj).getMessage() + "\" in the party.");
			Talk2Party msg2party = (Talk2Party) obj;
			msg2party.setMessage(c.getPlayer().getName() + " : " + msg2party.getMessage());
			Chess_Party chessParty = Chess_PartyManager.get().getPartyInWhichIsClient(c);
			if (chessParty != null) {
				chessParty.sendMsgToPlayersOfParty((Talk2Party) obj);
			}
		}
	}

}
