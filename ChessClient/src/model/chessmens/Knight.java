package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class Knight extends Chessmen {
	
	private static final Logger logger = Logger.getLogger(Knight.class);

	public Knight ( EColor _color ) {
		color = _color;
	}

	@Override
	public Chessmen clone() {
		return new Knight(color);
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException {
		if (c == null) throw new SquareException("La case est nulle");
		if (c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE) throw new SquareException("Cette case n'est pas sur l'echequier");
		ArrayList<Square> moves = new ArrayList<Square>();
		/* y decalage de 2 */
		if ( c.getX() > 0 && c.getY() - 1 > 0 && (plateau[c.getX() - 1][c.getY() - 2].getChessmen() == null || plateau[c.getX() - 1][c.getY() - 2].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() - 1][c.getY() - 2]);
		}

		if ( c.getX() > 0 && c.getY() < SIZE - 2 && (plateau[c.getX() - 1][c.getY() + 2].getChessmen() == null || plateau[c.getX() - 1][c.getY() + 2].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() - 1][c.getY() + 2]);
		}

		if ( c.getX() < SIZE - 1 && c.getY() - 1 > 0 && (plateau[c.getX() + 1][c.getY() - 2].getChessmen() == null || plateau[c.getX() + 1][c.getY() - 2].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() + 1][c.getY() - 2]);
		}

		if ( c.getX() < SIZE - 1 && c.getY() < SIZE - 2 && (plateau[c.getX() + 1][c.getY() + 2].getChessmen() == null || plateau[c.getX() + 1][c.getY() + 2].getChessmen().getColor() != c
				.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX() + 1][c.getY() + 2]);
		}
		/* x decalage de 2 */
		if ( c.getX() - 1 > 0 && c.getY() > 0 && (plateau[c.getX() - 2][c.getY() - 1].getChessmen() == null || plateau[c.getX() - 2][c.getY() - 1].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() - 2][c.getY() - 1]);
		}

		if ( c.getX() - 1 > 0 && c.getY() < SIZE - 1 && (plateau[c.getX() - 2][c.getY() + 1].getChessmen() == null || plateau[c.getX() - 2][c.getY() + 1].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() - 2][c.getY() + 1]);
		}

		if ( c.getX() < SIZE - 2 && c.getY() > 0 && (plateau[c.getX() + 2][c.getY() - 1].getChessmen() == null || plateau[c.getX() + 2][c.getY() - 1].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() + 2][c.getY() - 1]);
		}

		if ( c.getX() < SIZE - 2 && c.getY() < SIZE - 1 && (plateau[c.getX() + 2][c.getY() + 1].getChessmen() == null || plateau[c.getX() + 2][c.getY() + 1].getChessmen().getColor() != c
				.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX() + 2][c.getY() + 1]);
		}
		return moves;
	}

}
