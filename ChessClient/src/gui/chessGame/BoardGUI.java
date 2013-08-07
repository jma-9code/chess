package gui.chessGame;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Config;
import message.party.Stroke;
import model.Chessboard;
import model.Square;
import model.chessmens.Chessmen;

import org.apache.log4j.Logger;

import tools.Sprites;

import commun.EColor;

public class BoardGUI extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3525578991634859138L;
	private static final Logger logger = Logger.getLogger(BoardGUI.class);

	private Chessboard plateau;
	private BoardGUI himself;
	private ArrayBlockingQueue<Chessmen> chessmensInMove = new ArrayBlockingQueue<Chessmen>(32);
	private final Object lock = new Object();

	public BoardGUI(Chessboard _plateau) {
		this();
		himself = this;
		setPreferredSize(new Dimension(Config.CHESSMEN_SIZE * Config.BOARD_SIZE, Config.CHESSMEN_SIZE * Config.BOARD_SIZE));
		plateau = _plateau;
		plateau.addObserver(this);
	}

	/**
	 * Create the panel.
	 */
	public BoardGUI() {
		setLayout(null);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		/** Lissage du texte et des dessins */
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		updateBoard(g);
		updateGUI(g);
	}

	private void updateBoard(Graphics g) {
		if (plateau == null) {
			return;
		}

		// synchronized (plateau.getLock()) {
		for (int i = 0; i < plateau.getBoard().length; i++) {
			for (int j = 0; j < plateau.getBoard()[0].length; j++) {

				// Dammier
				if ((i + j) % 2 == 0) {
					g.setColor(Config.BOARD_SQUARE_WHITE);
				}
				else {
					g.setColor(Config.BOARD_SQUARE_BLACK);
				}
				g.fillRect(j * Config.CHESSMEN_SIZE, i * Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);

				Chessmen chessmen = plateau.getBoard()[i][j].getChessmen();

				// on ne fait pas les pieces vides ou en deplacement
				if (chessmen == null) {
					continue;
				}
				if (chessmensInMove.contains(chessmen)) {
					continue;
				}

				// Ajout des pieces
				g.drawImage(Sprites.getInstance().getChessmen(chessmen.getClass(), chessmen.getColor(), false), j * Config.CHESSMEN_SIZE, i
						* Config.CHESSMEN_SIZE, this);

			}
		}
		// }
	}

	private void updateGUI(Graphics g) {
		if (plateau == null) {
			return;
		}
		Square c_select = plateau.getCaseSelect();
		if (c_select != null && c_select.getChessmen() != null) {
			g.setColor(Config.BOARD_SQUARE_SELECT);
			g.drawRect(c_select.getY() * Config.CHESSMEN_SIZE, c_select.getX() * Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);
			g.setColor(Config.BOARD_POSSIBILITY);
			for (Square c : plateau.getPossibilities()) {
				g.drawRect(c.getY() * Config.CHESSMEN_SIZE, c.getX() * Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);
			}
		}

		/* ECHEC / Echec et mat */
		if (plateau.getPlayerInEchec() != null || plateau.getPlayerInEchecMAT() != null) {
			Square c_king = null;
			if (plateau.getPlayerInEchec() == EColor.BLACK || plateau.getPlayerInEchecMAT() == EColor.BLACK) {
				c_king = Chessboard.getKingbyColor(plateau.getBoard(), EColor.BLACK);
			}
			else {
				c_king = Chessboard.getKingbyColor(plateau.getBoard(), EColor.WHITE);
			}
			if (c_king == null) {
				return;
			}
			if (plateau.isBlackIsEchecMAT() || plateau.isWhiteIsEchecMAT()) {
				g.setColor(Config.BOARD_CHECKMATE);
				g.drawLine(c_king.getY() * Config.CHESSMEN_SIZE, c_king.getX() * Config.CHESSMEN_SIZE, c_king.getY() * Config.CHESSMEN_SIZE
						+ Config.CHESSMEN_SIZE, c_king.getX() * Config.CHESSMEN_SIZE + Config.CHESSMEN_SIZE);
				g.drawLine(c_king.getY() * Config.CHESSMEN_SIZE + Config.CHESSMEN_SIZE, c_king.getX() * Config.CHESSMEN_SIZE, c_king.getY()
						* Config.CHESSMEN_SIZE, c_king.getX() * Config.CHESSMEN_SIZE + Config.CHESSMEN_SIZE);
			}
			else {
				g.setColor(Config.BOARD_CHECK);
			}
			g.drawRect(c_king.getY() * Config.CHESSMEN_SIZE, c_king.getX() * Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);

		}
	}

	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		if (arg1 instanceof Stroke) {
			if (Config.MOVE_RAPIDITY > 0) {
				final Stroke stroke = ((Stroke) arg1);
				final Chessmen chessmen = plateau.getBoard()[stroke.getSourceX()][stroke.getSourceY()].getChessmen();
				// thread d'animation
				ChessmenAnimatedGUI animeGUI = new ChessmenAnimatedGUI(stroke, chessmen, chessmensInMove, this);
				new Thread(animeGUI, "Chessboard Chessmen Movement").start();
			}
		}
		repaint();
	}

	/** Gestion du mouvement d'une piece */
	private class ChessmenAnimatedGUI extends JLabel implements Runnable {
		private Stroke stroke;
		private Chessmen chessmen;
		ArrayBlockingQueue<Chessmen> chessmensInMove;
		BoardGUI boardGUI;

		public ChessmenAnimatedGUI(Stroke _stroke, Chessmen _chessmen, ArrayBlockingQueue<Chessmen> _chessmensInMove, BoardGUI _boardGUI) {
			stroke = _stroke;
			chessmen = _chessmen;
			chessmensInMove = _chessmensInMove;
			boardGUI = _boardGUI;

			chessmensInMove.add(chessmen);
			setIcon(new ImageIcon(Sprites.getInstance().getChessmen(chessmen.getClass(), chessmen.getColor(), false)));
		}

		@Override
		public void run() {
			int yd = stroke.getDestinationY() - stroke.getSourceY();
			int xd = stroke.getDestinationX() - stroke.getSourceX();
			int imax = stroke.getDestinationY() * Config.CHESSMEN_SIZE;
			int jmax = stroke.getDestinationX() * Config.CHESSMEN_SIZE;
			int i = stroke.getSourceY() * Config.CHESSMEN_SIZE;
			int j = stroke.getSourceX() * Config.CHESSMEN_SIZE;
			int movesMax = (Math.abs(imax - i) < Math.abs(jmax - j)) ? Math.abs(jmax - j) : Math.abs(imax - i);
			setBounds(i, j, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);
			boardGUI.add(this);
			while (i != imax || j != jmax) {
				if (yd >= 0 && xd >= 0) {
					if (imax != i) {
						i++;
					}
					if (jmax != j) {
						j++;
					}
				}
				else if (yd < 0 && xd < 0) {
					if (imax != i) {
						i--;
					}
					if (jmax != j) {
						j--;
					}
				}
				else if (yd >= 0 && xd < 0) {

					if (imax != i) {
						i++;
					}
					if (jmax != j) {
						j--;
					}
				}
				else if (yd < 0 && xd >= 0) {
					if (imax != i) {
						i--;
					}
					if (jmax != j) {
						j++;
					}
				}
				setBounds(i, j, Config.CHESSMEN_SIZE, Config.CHESSMEN_SIZE);
				try {
					Thread.sleep(Config.MOVE_RAPIDITY / movesMax);
				}
				catch (InterruptedException e) {
				}
				boardGUI.repaint();
			}
			chessmensInMove.remove(chessmen);
			boardGUI.remove(this);
		}

	}

}
