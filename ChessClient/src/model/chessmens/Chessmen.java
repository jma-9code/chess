package model.chessmens;

import java.io.Serializable;
import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public abstract class Chessmen implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(Chessmen.class);
	protected final static int SIZE = Config.BOARD_SIZE;

	protected EColor color;

	public abstract ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException;

	public EColor getColor() {
		return color;
	}

	public void setColor(EColor color) {
		this.color = color;
	}

	public static final Chessmen createChessmen(Class chessmen, EColor color) {
		if ( chessmen == King.class ) { return new King(color); }
		if ( chessmen == Queen.class ) { return new Queen(color); }
		if ( chessmen == Bishop.class ) { return new Bishop(color); }
		if ( chessmen == Knight.class ) { return new Knight(color); }
		if ( chessmen == Rook.class ) { return new Rook(color); }
		return null;
	}

	public static char getChessmenChar(Chessmen chess) {
		if ( chess instanceof King ) { return 'K'; }
		if ( chess instanceof Queen ) { return 'Q'; }
		if ( chess instanceof Bishop ) { return 'B'; }
		if ( chess instanceof Knight ) { return 'N'; }
		if ( chess instanceof Pawn ) { return 'P'; }
		return ' ';
	}

	public static Class getChessmenClassByChar(String str) {
		if ( str.contains("K") || str.contains("O-O") ) { return King.class; }
		if ( str.contains("Q") ) { return Queen.class; }
		if ( str.contains("B") ) { return Bishop.class; }
		if ( str.contains("N") ) { return Knight.class; }
		if ( str.contains("R") ) { return Rook.class; }
		return Pawn.class;
	}

	public abstract Chessmen clone();
	
}
