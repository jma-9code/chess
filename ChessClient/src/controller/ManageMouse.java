package controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import main.Config;
import message.party.StateOfParty;
import message.party.StateOfPlayer;
import message.party.Stroke;
import model.Chessboard;
import model.Party;
import model.PartyNetwork;
import model.Square;
import network.NetworkClient;
import network.NetworkException;

import org.apache.log4j.Logger;

import commun.EColor;

public class ManageMouse implements MouseListener, Observer {
	private static final Logger logger = Logger.getLogger(ManageMouse.class);
	private Party party;
	private Chessboard plateau;
	private boolean activate = false;

	public ManageMouse ( Party _party ) {
		party = _party;
		plateau = party.getPlateau();
		plateau.addObserver(this);
		activate = true;
		start();
	}

	public void stop() {
	}

	public void start() {
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if ( party instanceof PartyNetwork ) {
			//Le joueur joue en reseau, alors il ne peut pas jouer lorsque cest a l'autre
			PartyNetwork partyNetwork = (PartyNetwork) party;
			try {
				if ( NetworkClient.getInstance().getPlayer().getColor() != plateau.getWhoColorPlay() ) { return; }
			} catch (NetworkException e1) {
				logger.error("Player play in network", e1);
			}
		}

		if (party.getStatut() != StateOfParty.RUNNING)
			return;
		EColor color = plateau.getWhoColorPlay();
		Square caseSelectbyUser = plateau.getBoard()[e.getY() / Config.CHESSMEN_SIZE][e.getX() / Config.CHESSMEN_SIZE];
		if ( caseSelectbyUser == plateau.getCaseSelect() ) {
			plateau.setCaseSelect(null);
			return;
		}
		
		if ( activate && plateau.getCaseSelect() != null && (caseSelectbyUser.getChessmen() == null || caseSelectbyUser
				.getChessmen().getColor() != plateau.getCaseSelect().getChessmen().getColor()) ) {
			try {
				Stroke stroke = plateau.playStroke(plateau.getCaseSelect(), caseSelectbyUser);
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}
		}

		if ( caseSelectbyUser.getChessmen() != null && caseSelectbyUser.getChessmen().getColor() == color )
			plateau.setCaseSelect(caseSelectbyUser);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof StateOfPlayer ) {
			switch ( (StateOfPlayer) arg ) {
				case LOSE:
				case WIN:
					stop();
				break;
			}
		}

	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}
}
