package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class Queen extends Chessmen {
	
	private static final Logger logger = Logger.getLogger(Queen.class);
	
	public Queen ( EColor _color ) {
		color = _color;
	}

	@Override
	public Chessmen clone() {
		return new Queen(color);
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException{
		if (c == null) throw new SquareException("La case est nulle");
		if (c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE) throw new SquareException("Cette case n'est pas sur l'echequier");
		ArrayList<Square> moves = new ArrayList<Square>();

		// Coup de la tour
		Rook rook = new Rook(EColor.BLACK);
		moves.addAll(rook.movePossibilities(plateau, c));

		// Coup du fou
		Bishop bishop = new Bishop(EColor.BLACK);
		moves.addAll(bishop.movePossibilities(plateau, c));

		return moves;
	}

}
