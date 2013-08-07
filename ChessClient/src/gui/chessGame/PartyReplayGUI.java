package gui.chessGame;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import main.Config;
import message.party.StateOfParty;
import message.party.Stroke;
import model.Party;
import model.Square;
import model.chessmens.Chessmen;
import model.chessmens.Pawn;

import commun.EColor;

public class PartyReplayGUI extends PartyGUI implements Observer, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pnl_discuss;
	private SpringLayout springLayout = new SpringLayout();
	private JButton bt_playNext;
	private JButton bt_autoPlay;
	private JButton bt_playBack;
	private ArrayList<Stroke> strokes = null;
	private int nbStrokes = 0;
	private boolean autoplay = false;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private static ImageIcon[] icons = {new ImageIcon(PartyReplayGUI.class.getResource("/res/images/back.png")),
		new ImageIcon(PartyReplayGUI.class.getResource("/res/images/play.png")), 
		new ImageIcon(PartyReplayGUI.class.getResource("/res/images/next.png")),
			new ImageIcon(PartyReplayGUI.class.getResource("/res/images/pause.png")) };
	private LinkedList<Boolean> takedChessmen = new LinkedList<Boolean>();
	private LinkedList<Boolean> enPassantStroke = new LinkedList<Boolean>();
	private JPanel pnl_control;
	private JButton bt_goBegin;
	private JButton bt_goEnd;
	private final Object lock = new Object();

	public PartyReplayGUI ( Party p ) {
		super(p);
		strokes = (ArrayList<Stroke>) p.getPlateau().getStrokesPlayed().clone();
		party.getPlateau().initBoard();
		//desactive la possibilité de jouer
		mnk.setActivate(false);
		lbl_timeB.setVisible(false);
		lbl_timeB.setEnabled(false);
		lbl_timeW.setEnabled(false);
		lbl_timeW.setVisible(false);
		bt_null.setVisible(false);
		bt_null.setEnabled(false);
		setPreferredSize(new Dimension(525, 490));
		springLayout.putConstraint(SpringLayout.NORTH, boardGUI, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, boardGUI, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, boardGUI, 450, SpringLayout.NORTH, this);
		springLayout = (SpringLayout) getLayout();
		
		pnl_discuss = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, pnl_discuss, 0, SpringLayout.SOUTH, boardGUI);
		springLayout.putConstraint(SpringLayout.WEST, pnl_discuss, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_discuss, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pnl_discuss, 0, SpringLayout.EAST, this);
		add(pnl_discuss);
		pnl_discuss.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		pnl_control = new JPanel();
		pnl_control.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "0/?", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		pnl_discuss.add(pnl_control);

		bt_goBegin = new JButton("");
		bt_goBegin.addActionListener(this);
		bt_goBegin.setIcon(new ImageIcon(PartyReplayGUI.class.getResource("/res/images/goBegin.png")));
		pnl_control.add(bt_goBegin);

		bt_playBack = new JButton("");
		pnl_control.add(bt_playBack);
		bt_playBack.setIcon(icons[0]);

		bt_autoPlay = new JButton("");
		pnl_control.add(bt_autoPlay);
		bt_autoPlay.setIcon(icons[1]);
		bt_autoPlay.addActionListener(this);

		bt_playNext = new JButton("");
		pnl_control.add(bt_playNext);
		bt_playNext.setIcon(icons[2]);
		bt_playNext.addActionListener(this);
		bt_playBack.addActionListener(this);

		pnl_control.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "0/" + strokes.size(),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		bt_goEnd = new JButton("");
		bt_goEnd.addActionListener(this);
		bt_goEnd.setIcon(new ImageIcon(PartyReplayGUI.class.getResource("/res/images/goEnd.png")));
		pnl_control.add(bt_goEnd);
		updateInfos();
	}

	public void destroyReplay() {
		party.stopGame();
		executor.shutdownNow();
	}

	public void playStrokeNext() {
		if ( nbStrokes >= strokes.size() )
			return;
		synchronized (lock) {
			plateau.setWhoColorPlay((nbStrokes % 2 == 0) ? EColor.WHITE : EColor.BLACK);
			EColor color = plateau.getWhoColorPlay();
			int count = plateau.getChessmenTakedBlack().size() + plateau.getChessmenTakedWhite().size();
			Stroke str = strokes.get(nbStrokes);
			Square src = plateau.getBoard()[str.getSourceX()][str.getSourceY()];
			Square dst = plateau.getBoard()[str.getDestinationX()][str.getDestinationY()];

			//Gestion du enpassant a l'envers
			if ( src.getChessmen() != null && src.getChessmen() instanceof Pawn ) {
				if ( Math.abs(src.getY() - dst.getY()) == 1 && dst.getChessmen() == null ) {
					enPassantStroke.add(true);
				} else {
					enPassantStroke.add(false);
				}
			} else {
				enPassantStroke.add(false);
			}

			try {
				Stroke stroke = plateau.playStroke(src, dst);
				if ( strokes.get(nbStrokes).getDstIsPromotedTo() != ' ' ) {
					Class chessmen = Chessmen.getChessmenClassByChar("" + strokes.get(nbStrokes).getDstIsPromotedTo());
					plateau.havePromotion(stroke, Chessmen.createChessmen(chessmen, color));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			nbStrokes++;
			if ( count != plateau.getChessmenTakedBlack().size() + plateau.getChessmenTakedWhite().size() ) {
				takedChessmen.add(true);
			} else {
				takedChessmen.add(false);
			}
			pnl_control.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
					nbStrokes + "/" + strokes.size(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
	}


	public void playStrokeBack() {
		if ( nbStrokes <= 0 )
			return;
		synchronized (lock) {
			nbStrokes--;
			plateau.setWhoColorPlay((nbStrokes % 2 == 0) ? EColor.WHITE : EColor.BLACK);
			EColor color = plateau.getWhoColorPlay();
			plateau.setBlackIsEchec(false);
			plateau.setBlackIsEchecMAT(false);
			plateau.setWhiteIsEchec(false);
			plateau.setWhiteIsEchecMAT(false);
			party.setStatut(StateOfParty.RUNNING);

			Stroke str = strokes.get(nbStrokes);
			//Inversion src et dst
			Square dst = plateau.getBoard()[str.getSourceX()][str.getSourceY()];
			Square src = plateau.getBoard()[str.getDestinationX()][str.getDestinationY()];
			try {
				Stroke stroke = plateau.playStroke(src, dst);
				//Reverse promotion
				if ( strokes.get(nbStrokes).getDstIsPromotedTo() != ' ' ) {
					dst.setChessmen(new Pawn(color));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//GEstion des pieces mangees
			if ( takedChessmen.removeLast() ) {
				if ( plateau.getWhoColorPlay() == EColor.BLACK ) {
					src.setChessmen(plateau.getChessmenTakedBlack().removeLast());
				} else {
					src.setChessmen(plateau.getChessmenTakedWhite().removeLast());
				}
				updateChessmensTake();
			}

			//GEstion du enpassant
			if ( enPassantStroke.removeLast() ) {
				if ( plateau.getWhoColorPlay() == EColor.BLACK ) {
					Square realsrc = plateau.getBoard()[src.getX() + 1][src.getY()];
					realsrc.setChessmen(src.getChessmen());
					src.setChessmen(null);
				} else {
					Square realsrc = plateau.getBoard()[src.getX() - 1][src.getY()];
					realsrc.setChessmen(src.getChessmen());
					src.setChessmen(null);
				}
			}
			pnl_control.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
					nbStrokes + "/" + strokes.size(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if ( arg0.getSource() == bt_goEnd ) {
			int valSpeed = Config.MOVE_RAPIDITY;
			Config.MOVE_RAPIDITY = 0;
			while ( nbStrokes < strokes.size() ) {
				playStrokeNext();
			}
			Config.MOVE_RAPIDITY = valSpeed;
		} else if ( arg0.getSource() == bt_goBegin ) {
			int valSpeed = Config.MOVE_RAPIDITY;
			Config.MOVE_RAPIDITY = 0;
			while ( nbStrokes != 0 ) {
				playStrokeBack();
			}
			Config.MOVE_RAPIDITY = valSpeed;
		} else if ( arg0.getSource() == bt_autoPlay ) {
			autoplay = !autoplay;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setName("Chessboard Replay Automatic Movement");
					bt_autoPlay.setIcon(icons[3]);
					while ( autoplay && nbStrokes >= 0 && nbStrokes < strokes.size() ) {
						playStrokeNext();
						try {
							Thread.sleep(Config.MOVE_RAPIDITY * 2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					bt_autoPlay.setIcon(icons[1]);
				}
			});
		} else if ( arg0.getSource() == bt_playBack ) {
			playStrokeBack();
		} else if ( arg0.getSource() == bt_playNext ) {
			playStrokeNext();
		}
	}
}
