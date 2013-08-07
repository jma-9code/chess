package gui.chessGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import model.Chessboard;
import model.Chessboard.EChessInfoPlateau;
import model.Party;
import model.chessmens.Bishop;
import model.chessmens.Chessmen;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;

import org.apache.log4j.Logger;

import tools.Sprites;

import commun.EColor;

import controller.ManageMouse;

public class PartyGUI extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(PartyGUI.class);
	private static final long serialVersionUID = 1L;
	protected Party party;
	protected Chessboard plateau;
	protected BoardGUI boardGUI;
	protected JPanel pnl_chessmensB;
	protected JPanel pnl_infoPlayers;
	protected ArrayList<JLabel> chessmensTakedLbl = new ArrayList<JLabel>();
	protected JPanel pnl_chessmensW;
	protected DecimalFormat chronoFormat = new DecimalFormat("00");
	protected JButton bt_null;
	protected JLabel lbl_playerB;
	protected JLabel lbl_playerW;
	protected JLabel lbl_timeW;
	protected JLabel lbl_timeB;
	protected ManageMouse mnk;
	protected SpringLayout springLayout;

	public PartyGUI(Party _party) {
		setPreferredSize(new Dimension(525, 430));
		// Compatibilité avec wbuilder
		if (_party != null) {
			party = _party;
		}
		else {
			party = new Party();
		}
		initGUI();
		updateInfos();
	}

	protected void initGUI() {
		plateau = party.getPlateau();
		plateau.addObserver(this);
		boardGUI = new BoardGUI(plateau);
		mnk = new ManageMouse(party);
		boardGUI.addMouseListener(mnk);

		springLayout = new SpringLayout();
		setLayout(springLayout);

		springLayout.putConstraint(SpringLayout.NORTH, boardGUI, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, boardGUI, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, boardGUI, 400, SpringLayout.NORTH, this);
		boardGUI.setBorder(null);
		add(boardGUI);

		pnl_infoPlayers = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, pnl_infoPlayers, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_infoPlayers, 400, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, pnl_infoPlayers, -125, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, boardGUI, 0, SpringLayout.WEST, pnl_infoPlayers);
		springLayout.putConstraint(SpringLayout.EAST, pnl_infoPlayers, 0, SpringLayout.EAST, this);
		pnl_infoPlayers.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		add(pnl_infoPlayers);
		SpringLayout sl_pnl_infoPlayers = new SpringLayout();
		pnl_infoPlayers.setLayout(sl_pnl_infoPlayers);

		pnl_chessmensB = new JPanel();
		sl_pnl_infoPlayers.putConstraint(SpringLayout.NORTH, pnl_chessmensB, 32, SpringLayout.NORTH, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.SOUTH, pnl_chessmensB, 152, SpringLayout.NORTH, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.EAST, pnl_chessmensB, 0, SpringLayout.EAST, pnl_infoPlayers);
		pnl_chessmensB.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		pnl_infoPlayers.add(pnl_chessmensB);
		pnl_chessmensB.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		pnl_chessmensW = new JPanel();
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, pnl_chessmensB, 0, SpringLayout.WEST, pnl_chessmensW);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.NORTH, pnl_chessmensW, -130, SpringLayout.SOUTH, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, pnl_chessmensW, 0, SpringLayout.WEST, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.SOUTH, pnl_chessmensW, 0, SpringLayout.SOUTH, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.EAST, pnl_chessmensW, 0, SpringLayout.EAST, pnl_infoPlayers);
		pnl_chessmensW.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnl_infoPlayers.add(pnl_chessmensW);
		pnl_chessmensW.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		bt_null = new JButton("Match nul");
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, bt_null, 20, SpringLayout.WEST, pnl_infoPlayers);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.SOUTH, bt_null, -52, SpringLayout.NORTH, pnl_chessmensW);
		bt_null.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				requestNull();
			}
		});
		pnl_infoPlayers.add(bt_null);

		lbl_playerB = new JLabel("BLACK", new ImageIcon(Sprites.getInstance().getChessmen(King.class, EColor.BLACK, true)), JLabel.LEFT);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, lbl_playerB, 0, SpringLayout.WEST, pnl_chessmensB);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.SOUTH, lbl_playerB, -6, SpringLayout.NORTH, pnl_chessmensB);
		pnl_infoPlayers.add(lbl_playerB);
		lbl_playerW = new JLabel("WHITE", new ImageIcon(Sprites.getInstance().getChessmen(King.class, EColor.WHITE, true)), JLabel.LEFT);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, lbl_playerW, 0, SpringLayout.WEST, pnl_chessmensB);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.SOUTH, lbl_playerW, -6, SpringLayout.NORTH, pnl_chessmensW);
		pnl_infoPlayers.add(lbl_playerW);

		Font font = new Font("Arial", Font.ITALIC, 14);
		lbl_timeW = new JLabel("00:00");
		lbl_timeW.setFont(font);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.NORTH, lbl_timeW, 4, SpringLayout.NORTH, lbl_playerW);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, lbl_timeW, 6, SpringLayout.EAST, lbl_playerW);
		pnl_infoPlayers.add(lbl_timeW);

		lbl_timeB = new JLabel("00:00");
		lbl_timeB.setFont(font);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.NORTH, lbl_timeB, 4, SpringLayout.NORTH, lbl_playerB);
		sl_pnl_infoPlayers.putConstraint(SpringLayout.WEST, lbl_timeB, 6, SpringLayout.EAST, lbl_playerB);
		pnl_infoPlayers.add(lbl_timeB);
	}

	protected void requestNull() {
		int val = JOptionPane.showConfirmDialog(this, "Acceptez vous le match nul ?", "Demande de match nul", JOptionPane.YES_NO_OPTION);
		if (val == JOptionPane.OK_OPTION) {
			party.getPlateau().requestNullAccepted();
		}
	}

	protected final void updateTime() {
		if (plateau.getTimeBlack() < 10) {
			lbl_timeB.setForeground(Color.RED);
		}
		if (plateau.getTimeWhite() < 10) {
			lbl_timeW.setForeground(Color.RED);
		}

		lbl_timeB.setText(chronoFormat.format(plateau.getTimeBlack() / 60) + ":" + chronoFormat.format(plateau.getTimeBlack() % 60));
		lbl_timeW.setText(chronoFormat.format(plateau.getTimeWhite() / 60) + ":" + chronoFormat.format(plateau.getTimeWhite() % 60));

		if (EColor.WHITE.equals(plateau.getWhoColorPlay())) {
			lbl_timeW.setForeground(Color.ORANGE);
			lbl_timeB.setForeground(Color.WHITE);
		}
		else {
			lbl_timeB.setForeground(Color.ORANGE);
			lbl_timeW.setForeground(Color.WHITE);
		}
	}

	protected final void updateInfos() {
		if (party.getPlayers().size() != 2) {
			return;
		}
		lbl_playerB.setIcon(new ImageIcon(Sprites.getInstance().getChessmen(King.class, EColor.BLACK, true)));

		lbl_playerB.setText(party.getPlayers().get(1).getName());
		lbl_playerB.setToolTipText(party.getPlayers().get(1).getName() + " - " + party.getPlayers().get(1).getScore());

		lbl_playerW.setIcon(new ImageIcon(Sprites.getInstance().getChessmen(King.class, EColor.WHITE, true)));
		lbl_playerW.setText(party.getPlayers().get(0).getName());
		lbl_playerW.setToolTipText(party.getPlayers().get(0).getName() + " - " + party.getPlayers().get(0).getScore());
	}

	protected final void updateChessmensTake() {
		for (JLabel lbl : chessmensTakedLbl) {
			pnl_chessmensW.remove(lbl);
			pnl_chessmensB.remove(lbl);
		}
		chessmensTakedLbl.clear();

		LinkedList<?>[] whiteAndBlack = { plateau.getChessmenTakedWhite(), plateau.getChessmenTakedBlack() };

		for (int i = 0; i < 2; i++) {
			boolean tabChessmen[] = { false, false, false, false, false };
			for (Chessmen chessmen : (LinkedList<Chessmen>) whiteAndBlack[i]) {
				if (chessmen.getClass() == Pawn.class && !tabChessmen[0]) {
					tabChessmen[0] = true;
				}
				else if (chessmen.getClass() == Rook.class && !tabChessmen[1]) {
					tabChessmen[1] = true;
				}
				else if (chessmen.getClass() == Knight.class && !tabChessmen[2]) {
					tabChessmen[2] = true;
				}
				else if (chessmen.getClass() == Bishop.class && !tabChessmen[3]) {
					tabChessmen[3] = true;
				}
				else if (chessmen.getClass() == Queen.class && !tabChessmen[4]) {
					tabChessmen[4] = true;
				}
				else {
					continue;
				}

				int count = Chessboard.countNbChessmen((LinkedList<Chessmen>) whiteAndBlack[i], chessmen.getClass());
				final JLabel img_lbl = new JLabel("x" + count, new ImageIcon(Sprites.getInstance().getChessmen(chessmen.getClass(),
						chessmen.getColor(), true)), JLabel.CENTER);
				if (i == 0) {
					pnl_chessmensB.add(img_lbl);
				}
				else {
					pnl_chessmensW.add(img_lbl);
				}
				chessmensTakedLbl.add(img_lbl);
			}
		}

		pnl_chessmensW.validate();
		pnl_chessmensB.validate();
		repaint();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof EChessInfoPlateau) {
			EChessInfoPlateau info = (EChessInfoPlateau) arg1;
			switch (info) {
				case TAKE:
					updateChessmensTake();
					break;
				case MOVE:
					break;
				case CHECK:
					break;
				case CHECKMATE:
					break;
				case TIME:
					updateTime();
					break;
				case NULL:
					break;
				default:
					break;
			}

		}
	}
}
