package model;

import gui.MainGUI;
import gui.chessGame.ChessmenChoiceGUI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.Config;
import message.party.StateOfParty;
import message.party.StateOfPlayer;
import message.party.Stroke;
import model.chessmens.Bishop;
import model.chessmens.Chessmen;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;
import model.exceptions.RulesException;
import model.exceptions.SquareException;
import network.NetworkClient;

import org.apache.log4j.Logger;

import commun.EColor;

/**
 * Plateau de jeu compos� d'un ensemble de case conteneur de pi�ce.
 */
public class Chessboard extends Observable {

	private static final Logger logger = Logger.getLogger(Chessboard.class);

	private final static int SIZE = Config.BOARD_SIZE;

	private Square[][] board = new Square[SIZE][SIZE];
	private Party party;

	private final Object lock = new Object();
	private ScheduledExecutorService executorTimer = Executors.newSingleThreadScheduledExecutor();

	// flags de replay ou non
	private boolean replay = false;

	// flags de partie
	private boolean whiteIsEchec = false;
	private boolean blackIsEchec = false;
	private boolean whiteIsEchecMAT = false;
	private boolean blackIsEchecMAT = false;
	private boolean gameNull = false;
	private EColor whoColorPlay = EColor.WHITE;

	// Liste des pieces mangees
	private LinkedList<Chessmen> chessmenTakedWhite = new LinkedList<Chessmen>();
	private LinkedList<Chessmen> chessmenTakedBlack = new LinkedList<Chessmen>();

	// gestion du temps
	private int timeWhite = Config.PARTY_TIME;
	private int timeBlack = Config.PARTY_TIME;

	// gestion des coups
	private int nbStroke = 0;
	private ArrayList<Stroke> strokesPlayed = new ArrayList<Stroke>();

	// Tampon pour eviter des calculs
	private Square caseSelect = null;
	private ArrayList<Square> possibilities = new ArrayList<Square>();

	// Variable pour gerer le en passant
	private boolean enPassantPossible = false;
	private Pawn tmpPawn;

	public enum EChessInfoPlateau {
		MOVE, TAKE, SELECTCASE, CHECK, CHECKMATE, NULL, TIME;
	}

	public Chessboard(Party _party, boolean _replay) {
		this(_party);
		replay = _replay;
		endTimer();
	}

	public Chessboard(Party _party) {
		setParty(_party);
		initBoard();
		party.setStatut(StateOfParty.RUNNING);
		startTimer();
	}

	public void clearBoard() {
		whoColorPlay = EColor.WHITE;
		timeWhite = Config.PARTY_TIME;
		timeBlack = Config.PARTY_TIME;
		strokesPlayed.clear();
		chessmenTakedWhite.clear();
		chessmenTakedBlack.clear();
		whiteIsEchec = false;
		blackIsEchec = false;
		whiteIsEchecMAT = false;
		blackIsEchecMAT = false;
		tmpPawn = null;
		enPassantPossible = false;
		party.setStatut(StateOfParty.RUNNING);
		gameNull = false;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (board[i][j] == null) {
					board[i][j] = new Square(i, j, null);
				}
				else {
					board[i][j].setChessmen(null);
				}
			}
		}
	}

	public void defineTime(int time) {
		timeWhite = time;
		timeBlack = time;
	}

	public boolean testPromotionIsPossible() {
		int i, j;
		for (i = 0; i < SIZE; i++) {
			for (j = 0; j < SIZE; j++) {
				Chessmen chessmen = board[i][j].getChessmen();
				if (chessmen != null && chessmen instanceof Pawn) {
					if (chessmen.getColor() == EColor.BLACK && board[i][j].getX() == Config.BOARD_SIZE - 1) {
						return true;
					}
					else if (chessmen.getColor() == EColor.WHITE && board[i][j].getX() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void havePromotion(Stroke stroke, Chessmen newChessmen) {
		Square square = board[stroke.getDestinationX()][stroke.getDestinationY()];
		if (square.getChessmen() == null || !(square.getChessmen() instanceof Pawn) || newChessmen == null) {
			return;
		}
		Chessmen chessmen = square.getChessmen();
		char charChessmen = Chessmen.getChessmenChar(newChessmen);
		if (chessmen.getColor() == EColor.BLACK && square.getX() == Config.BOARD_SIZE - 1) {
			square.setChessmen(newChessmen);
			stroke.setDstIsPromotedTo(charChessmen);
		}
		else if (chessmen.getColor() == EColor.WHITE && square.getX() == 0) {
			square.setChessmen(newChessmen);
			stroke.setDstIsPromotedTo(charChessmen);
		}
		updateStatus();
	}

	public void initBoard() {
		int i, j;
		clearBoard();
		// Init des pions pour chaque joueur, et le reste a rien
		for (i = 0; i < SIZE; i++) {
			for (j = 0; j < SIZE; j++) {
				Square c = new Square(i, j, null);
				board[i][j] = c;

				if (i == 1) {
					c.setChessmen(new Pawn(EColor.BLACK));
				}
				else if (i == SIZE - 2) {
					c.setChessmen(new Pawn(EColor.WHITE));
				}
			}
		}

		j = 0;
		// Premiere ligne de chaque joueur
		while (j < SIZE) {
			switch (j) {
				case 0:
				case 7:
					board[0][j].setChessmen(new Rook(EColor.BLACK));
					board[SIZE - 1][j].setChessmen(new Rook(EColor.WHITE));
					break;

				case 1:
				case 6:
					board[0][j].setChessmen(new Knight(EColor.BLACK));
					board[SIZE - 1][j].setChessmen(new Knight(EColor.WHITE));
					break;

				case 2:
				case 5:
					board[0][j].setChessmen(new Bishop(EColor.BLACK));
					board[SIZE - 1][j].setChessmen(new Bishop(EColor.WHITE));
					break;

				case 3:
					board[0][j].setChessmen(new Queen(EColor.BLACK));
					board[SIZE - 1][j].setChessmen(new Queen(EColor.WHITE));

					break;
				case 4:
					board[0][j].setChessmen(new King(EColor.BLACK));
					board[SIZE - 1][j].setChessmen(new King(EColor.WHITE));
					break;
			}
			j++;
		}
	}

	private void castlingManagement(Square src, Square dst) {
		/* Gestion du roque */
		if (src.getChessmen() instanceof Rook) {
			if (!replay) {
				((Rook) src.getChessmen()).setMoved(true);
			}
		}
		else if (src.getChessmen() instanceof King) {
			if (!replay) {
				((King) src.getChessmen()).setMoved(true);
			}

			if (src.getY() == 4) {
				// On joue le petit roque
				if (src.getY() - dst.getY() < -1) {
					board[src.getX()][src.getY() + 1].setChessmen(board[src.getX()][SIZE - 1].getChessmen());
					board[src.getX()][SIZE - 1].setChessmen(null);
				}
				// on joue le grand roque
				else if (src.getY() - dst.getY() > 1) {
					board[src.getX()][src.getY() - 1].setChessmen(board[src.getX()][0].getChessmen());
					board[src.getX()][0].setChessmen(null);
				}
			}// Rook inverse pour le replay
			else if (src.getY() == 2 || src.getY() == 6) {
				// On joue le grand roque
				if (src.getY() - dst.getY() < -1) {
					board[src.getX()][src.getY() - 2].setChessmen(board[src.getX()][src.getY() + 1].getChessmen());
					board[src.getX()][src.getY() + 1].setChessmen(null);
				}
				// on joue le petit roque
				else if (src.getY() - dst.getY() > 1) {
					board[src.getX()][src.getY() + 1].setChessmen(board[src.getX()][src.getY() - 1].getChessmen());
					board[src.getX()][src.getY() - 1].setChessmen(null);
				}
			}
		}
	}

	private void enPassantManagement(Square src, Square dst) {
		if (enPassantPossible && Math.abs(src.getY() - dst.getY()) != 0) {
			if (dst.getChessmen() instanceof Pawn && dst.getChessmen().getColor() != tmpPawn.getColor()) {
				// Diag de gauche
				if (src.getY() > 0 && board[src.getX()][src.getY() - 1].getChessmen() != null) {
					if (tmpPawn == board[src.getX()][src.getY() - 1].getChessmen()) {
						board[src.getX()][src.getY() - 1].setChessmen(null);
						if (tmpPawn.getColor() == EColor.WHITE) {
							chessmenTakedWhite.add(tmpPawn);
						}
						else {
							chessmenTakedBlack.add(tmpPawn);
						}
					}
				}
				// diag de droite
				if (src.getY() < SIZE - 1 && board[src.getX()][src.getY() + 1].getChessmen() != null) {
					if (tmpPawn == board[src.getX()][src.getY() + 1].getChessmen()) {
						board[src.getX()][src.getY() + 1].setChessmen(null);
						if (tmpPawn.getColor() == EColor.WHITE) {
							chessmenTakedWhite.add(tmpPawn);
						}
						else {
							chessmenTakedBlack.add(tmpPawn);
						}
					}
				}
				updateObservers(EChessInfoPlateau.TAKE);
			}
			tmpPawn.setHasMove2Square(false);
			enPassantPossible = false;
		}
		else if (enPassantPossible) {
			tmpPawn.setHasMove2Square(false);
			enPassantPossible = false;
		}

		// Utilisé pour la prise en passant
		if (dst.getChessmen() instanceof Pawn) {
			if (Math.abs(src.getX() - dst.getX()) >= 2) {
				tmpPawn = (Pawn) dst.getChessmen();
				tmpPawn.setHasMove2Square(true);
				enPassantPossible = true;
			}
		}
	}

	public static boolean testPat(Square[][] board) {
		// Test du pat
		ArrayList<Square> attackMovesA = new ArrayList<Square>();
		ArrayList<Square> attackMovesB = new ArrayList<Square>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (board[i][j].getChessmen() != null && board[i][j].getChessmen().getColor() == EColor.BLACK) {
					try {
						attackMovesA.addAll(filterMovePossibilities(board, board[i][j],
								board[i][j].getChessmen().movePossibilities(board, board[i][j])));
					}
					catch (SquareException e) {
						e.printStackTrace();
					}
				}
				if (board[i][j].getChessmen() != null && board[i][j].getChessmen().getColor() == EColor.WHITE) {
					try {
						attackMovesB.addAll(filterMovePossibilities(board, board[i][j],
								board[i][j].getChessmen().movePossibilities(board, board[i][j])));
					}
					catch (SquareException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (attackMovesA.isEmpty() || attackMovesB.isEmpty()) {
			return true;
		}
		return false;
	}

	public Stroke playStroke(Stroke stroke) throws RulesException, SquareException {
		return playStroke(stroke.getSourceX(), stroke.getSourceY(), stroke.getDestinationX(), stroke.getDestinationY());
	}

	public Stroke playStroke(int srcX, int srcY, int dstX, int dstY) throws RulesException, SquareException {
		if (srcX < 0 || srcX > SIZE - 1 || srcY < 0 || srcY > SIZE - 1) {
			throw new SquareException("La case n'exsite pas !");
		}
		if (dstX < 0 || dstX > SIZE - 1 || dstY < 0 || dstY > SIZE - 1) {
			throw new SquareException("La case n'exsite pas !");
		}
		Square src = board[srcX][srcY];
		Square dst = board[dstX][dstY];
		return playStroke(src, dst);
	}

	public Stroke playStroke(Square src, Square dst) throws RulesException, SquareException {
		synchronized (lock) {
			if (party.getStatut() != StateOfParty.RUNNING) {
				throw new RulesException("La partie est finie ou en attente");
			}
			if (src == null || dst == null || src.getChessmen() == null) {
				throw new SquareException("Aucune piece sur la case !");
			}
			if (!replay && src.getChessmen().getColor() != whoColorPlay) {
				throw new RulesException("C'est a l'autre joueur de jouer !");
			}

			// On joue le coup sans preselectionner une case
			if (caseSelect == null) {
				possibilities.addAll(filterMovePossibilities(board, src, src.getChessmen().movePossibilities(board, src)));
			}
			if (!replay && !(getPossibilities().contains(dst))) {
				throw new RulesException("Ce coup est impossible !");
			}

			// Gestion du roque
			castlingManagement(src, dst);

			Stroke stroke = new Stroke(src.getX(), src.getY(), dst.getX(), dst.getY());
			// Indique qu'il va faire se mouvement
			updateObservers(stroke);

			// Fait le mouvement
			Chessmen take = move(board, src, dst);

			// Gestion du en passant
			enPassantManagement(src, dst);

			// Maj des pieces prises
			if (take != null) {
				if (whoColorPlay == EColor.BLACK) {
					chessmenTakedWhite.add(take);
				}
				else {
					chessmenTakedBlack.add(take);
				}
				updateObservers(EChessInfoPlateau.TAKE);
			}

			if (testPromotionIsPossible()) {
				if (party instanceof PartyNetwork) {
					EColor colorPlayer = ((PartyNetwork) party).getCurrentPlayer().getColor();
					if (whoColorPlay.equals(colorPlayer)) {
						ChessmenChoiceGUI choice = new ChessmenChoiceGUI(MainGUI.getInstance(), whoColorPlay);
						Chessmen chessmen = choice.getChessmen();
						havePromotion(stroke, chessmen);
					}
				}
				else {
					ChessmenChoiceGUI choice = new ChessmenChoiceGUI(MainGUI.getInstance(), whoColorPlay);
					Chessmen chessmen = choice.getChessmen();
					havePromotion(stroke, chessmen);
				}
			}

			// Enregistrement des coups joues
			nbStroke++;
			strokesPlayed.add(stroke);

			// On joue en reseau ??
			if (party instanceof PartyNetwork) {
				EColor colorPlayer = ((PartyNetwork) party).getCurrentPlayer().getColor();
				if (whoColorPlay.equals(colorPlayer)) {
					NetworkClient.getInstance().sendObject(stroke);
				}
			}

			// Chgmt de joueur
			whoColorPlay = (whoColorPlay == EColor.BLACK) ? EColor.WHITE : EColor.BLACK;

			updateStatus();
			setCaseSelect(null);
			return stroke;
		}
	}

	private void updateStatus() {
		// echec ? echec et mat ?
		if (isEchec(board, whoColorPlay)) {
			if (isEchecMat(board, whoColorPlay)) {
				if (whoColorPlay == EColor.BLACK) {
					blackIsEchecMAT = true;
				}
				else {
					whiteIsEchecMAT = true;
				}
				updateObservers(EChessInfoPlateau.CHECKMATE);
				endGame();
			}
			else {
				if (whoColorPlay == EColor.BLACK) {
					blackIsEchec = true;
				}
				else {
					whiteIsEchec = true;
				}
				updateObservers(EChessInfoPlateau.CHECK);
			}
		}
		else {
			// pat ?
			if (testPat(board)) {
				setGameNull(true);
				updateObservers(EChessInfoPlateau.NULL);
				endGame();
			}

			if (whoColorPlay == EColor.WHITE) {
				blackIsEchecMAT = false;
				blackIsEchec = false;
			}
			else {
				whiteIsEchecMAT = false;
				whiteIsEchec = false;
			}
		}
	}

	public void requestNullAccepted() {
		gameNull = true;
		endGame();
	}

	public void startTimer() {
		executorTimer = Executors.newSingleThreadScheduledExecutor();
		executorTimer.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("Chessboard Timer");
				switch (whoColorPlay) {
					case BLACK:
						setTimeBlack(getTimeBlack() - 1);
						break;
					case WHITE:
						setTimeWhite(getTimeWhite() - 1);
						break;
				}

				updateObservers(EChessInfoPlateau.TIME);

				if (timeBlack <= 0 || timeWhite <= 0) {
					if (timeBlack <= 0) {
						blackIsEchecMAT = true;
					}
					else {
						whiteIsEchecMAT = true;
					}
					updateObservers(EChessInfoPlateau.CHECKMATE);
					endGame();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public void endTimer() {
		executorTimer.shutdownNow();
	}

	public void endGame() {
		endTimer();
		party.setStatut(StateOfParty.FINISHED);
	}

	public EColor getPlayerInEchec() {
		if (whiteIsEchec) {
			return EColor.WHITE;
		}
		else if (blackIsEchec) {
			return EColor.BLACK;
		}
		return null;
	}

	public EColor getPlayerInEchecMAT() {
		if (whiteIsEchecMAT) {
			return EColor.WHITE;
		}
		else if (blackIsEchecMAT) {
			return EColor.BLACK;
		}
		return null;
	}

	public static Chessmen move(Square[][] plateau, Square src, Square dst) {
		Chessmen take = null;

		// piece take
		if (dst.getChessmen() != null) {
			take = dst.getChessmen().clone();
		}

		dst.setChessmen(src.getChessmen());
		src.setChessmen(null);
		return take;
	}

	public static Square getKingbyColor(Square plateau[][], EColor color) {
		for (int i = 0; i < plateau.length; i++) {
			for (int j = 0; j < plateau[0].length; j++) {
				if (plateau[i][j].getChessmen() != null && plateau[i][j].getChessmen() instanceof King
						&& plateau[i][j].getChessmen().getColor() == color) {
					return plateau[i][j];
				}
			}
		}
		return null;
	}

	public static boolean isEchec(Square plateau[][], EColor color) {
		int i, j;
		Square c_king = getKingbyColor(plateau, color);
		if (c_king == null) {
			return false;
		}

		for (i = 0; i < plateau.length; i++) {
			for (j = 0; j < plateau[0].length; j++) {
				if (plateau[i][j].getChessmen() != null && plateau[i][j].getChessmen().getColor() != color) {
					// Recherche des cases possibles
					ArrayList<Square> moves;
					try {
						moves = plateau[i][j].getChessmen().movePossibilities(plateau, plateau[i][j]);
						for (Square c : moves) {
							if (c.getX() == c_king.getX() && c.getY() == c_king.getY()) {
								return true;
							}
						}
					}
					catch (SquareException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	public static boolean simulEchec(Square[][] plateau, Square src, Square dest) {
		// Si la case darrivee contient une piece, c'est quon va la manger, on
		// memorise le pointeur
		Chessmen ptmp = null;
		if (dest.getChessmen() != null) {
			ptmp = dest.getChessmen();
		}

		// On cherche le roi du joueur en echec
		EColor couleurSrc = src.getChessmen().getColor();
		Chessmen take = move(plateau, src, dest);
		boolean echec = isEchec(plateau, couleurSrc);
		move(plateau, dest, src);
		if (take != null) {
			// On remet le nouveau pointeur car lancienne a ete liberee
			dest.setChessmen(ptmp);
		}
		return echec;
	}

	public static boolean isEchecMat(Square[][] plateau, EColor color) {
		int i, j;
		// C'est bien le roi la case ?
		Square c = getKingbyColor(plateau, color);
		if (c == null) {
			return false;
		}
		// On essaye de bouger toutes les pieces du joueur en echec...et on fait
		// une simulation
		for (i = 0; i < SIZE; i++) {
			for (j = 0; j < SIZE; j++) {
				if (plateau[i][j].getChessmen() != null && plateau[i][j].getChessmen().getColor() == c.getChessmen().getColor()) {
					// Recherche des cases possibles
					ArrayList<Square> moves;
					try {
						moves = plateau[i][j].getChessmen().movePossibilities(plateau, plateau[i][j]);
						for (Square c_move : moves) {
							if (!simulEchec(plateau, plateau[i][j], c_move)) {
								return false;
							}
						}
					}
					catch (SquareException e) {
						//
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	public Square[][] getBoard() {
		return board;
	}

	public void setBoard(Square[][] board) {
		this.board = board;
	}

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Square getCaseSelect() {
		return caseSelect;
	}

	public void setCaseSelect(Square _caseSelect) {
		if (caseSelect == _caseSelect) {
			return;
		}
		this.caseSelect = _caseSelect;
		if (caseSelect != null && caseSelect.getChessmen() != null) {
			if (caseSelect.getChessmen().getColor() != whoColorPlay) {
				// Piece adverse
				possibilities.clear();
				caseSelect = null;
			}
			else {
				possibilities.clear();
				try {
					possibilities.addAll(filterMovePossibilities(board, caseSelect, caseSelect.getChessmen().movePossibilities(board, caseSelect)));
				}
				catch (SquareException e) {
					e.printStackTrace();
				}
			}
		}
		updateObservers(EChessInfoPlateau.SELECTCASE);
	}

	public static ArrayList<Square> filterMovePossibilities(Square[][] plateau, Square src, ArrayList<Square> moves) {
		ArrayList<Square> movesFilter = new ArrayList<Square>();
		for (Square c : moves) {
			if (!simulEchec(plateau, src, c)) {
				movesFilter.add(c);
			}
		}
		return movesFilter;
	}

	public static Square[][] boardClone(Square[][] plateau) {
		Square[][] ret = new Square[plateau.length][plateau[0].length];
		for (int i = 0; i < plateau.length; i++) {
			for (int j = 0; j < plateau[0].length; j++) {
				ret[i][j] = plateau[i][j].clone();
			}
		}
		return ret;
	}

	public static int countNbChessmen(LinkedList<Chessmen> whiteAndBlack, Class chessmen) {
		int count = 0;
		for (Chessmen chessm : whiteAndBlack) {
			if (chessmen == chessm.getClass()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Retourne l'état du joueur par rapport à sa couleur
	 * 
	 * @param color
	 * @return
	 */
	public StateOfPlayer getStateOfPlayer(EColor color) {
		if (blackIsEchecMAT) {
			if (EColor.BLACK.equals(color)) {
				return StateOfPlayer.LOSE;
			}
			else {
				return StateOfPlayer.WIN;
			}
		}
		else if (whiteIsEchecMAT) {
			if (EColor.WHITE.equals(color)) {
				return StateOfPlayer.LOSE;
			}
			else {
				return StateOfPlayer.WIN;
			}
		}
		else if (gameNull) {
			return StateOfPlayer.NULL;
		}
		else {
			return StateOfPlayer.INGAME;
		}
	}

	public ArrayList<Square> getPossibilities() {
		return possibilities;
	}

	public void setPossibilities(ArrayList<Square> possibilities) {
		this.possibilities = possibilities;
	}

	public int getTimeWhite() {
		return timeWhite;
	}

	public void setTimeWhite(int timeWhite) {
		this.timeWhite = timeWhite;
	}

	public int getTimeBlack() {
		return timeBlack;
	}

	public void setTimeBlack(int timeBlack) {
		this.timeBlack = timeBlack;
	}

	public boolean isGameNull() {
		return gameNull;
	}

	public void setGameNull(boolean gameNull) {
		this.gameNull = gameNull;
	}

	public boolean isWhiteIsEchec() {
		return whiteIsEchec;
	}

	public void setWhiteIsEchec(boolean whiteIsEchec) {
		this.whiteIsEchec = whiteIsEchec;
	}

	public boolean isBlackIsEchec() {
		return blackIsEchec;
	}

	public void setBlackIsEchec(boolean blackIsEchec) {
		this.blackIsEchec = blackIsEchec;
	}

	public boolean isWhiteIsEchecMAT() {
		return whiteIsEchecMAT;
	}

	public void setWhiteIsEchecMAT(boolean whiteIsEchecMAT) {
		this.whiteIsEchecMAT = whiteIsEchecMAT;
	}

	public boolean isBlackIsEchecMAT() {
		return blackIsEchecMAT;
	}

	public void setBlackIsEchecMAT(boolean blackIsEchecMAT) {
		this.blackIsEchecMAT = blackIsEchecMAT;
	}

	public EColor getWhoColorPlay() {
		return whoColorPlay;
	}

	public int getNbStroke() {
		return nbStroke;
	}

	public void setNbStroke(int nbStroke) {
		this.nbStroke = nbStroke;
	}

	public ArrayList<Stroke> getStrokesPlayed() {
		return strokesPlayed;
	}

	public void setStrokesPlayed(ArrayList<Stroke> strokesPlayed) {
		this.strokesPlayed = strokesPlayed;
	}

	public boolean isReplay() {
		return replay;
	}

	public void setReplay(boolean replay) {
		this.replay = replay;
	}

	public LinkedList<Chessmen> getChessmenTakedWhite() {
		return chessmenTakedWhite;
	}

	public void setChessmenTakedWhite(LinkedList<Chessmen> chessmenTakedWhite) {
		this.chessmenTakedWhite = chessmenTakedWhite;
	}

	public LinkedList<Chessmen> getChessmenTakedBlack() {
		return chessmenTakedBlack;
	}

	public void setChessmenTakedBlack(LinkedList<Chessmen> chessmenTakedBlack) {
		this.chessmenTakedBlack = chessmenTakedBlack;
	}

	public void setWhoColorPlay(EColor whoColorPlay) {
		this.whoColorPlay = whoColorPlay;
	}

	public Object getLock() {
		return lock;
	}

	public boolean isEnPassantPossible() {
		return enPassantPossible;
	}

	public void setEnPassantPossible(boolean enPassantPossible) {
		this.enPassantPossible = enPassantPossible;
	}

	public Pawn getTmpPawn() {
		return tmpPawn;
	}

	public void setTmpPawn(Pawn tmpPawn) {
		this.tmpPawn = tmpPawn;
	}
}
