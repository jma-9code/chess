package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class Bishop extends Chessmen {
	
	private static final Logger logger = Logger.getLogger(Bishop.class);

	public Bishop ( EColor _color ) {
		color = _color;
	}

	@Override
	public Chessmen clone() {
		return new Bishop(color);
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException {
		if (c == null) throw new SquareException("La case est nulle");
		if (c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE) throw new SquareException("Cette case n'est pas sur l'echequier");
		int x, y;
		ArrayList<Square> moves = new ArrayList<Square>();
		// Piece entre deux ?

		for ( x = c.getX() - 1, y = c.getY() - 1; x >= 0 && y >= 0; x--, y-- ) {
			if ( plateau[x][y].getChessmen() == null ) {
				moves.add(plateau[x][y]);
			} else if ( plateau[x][y].getChessmen().getColor() != c.getChessmen().getColor() ) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][y]);
				break;
			} else if ( plateau[x][y].getChessmen().getColor() == c.getChessmen().getColor() ) {
				break;
			}
		}

		for ( x = c.getX() - 1, y = c.getY() + 1; x >= 0 && y < SIZE; x--, y++ ) {
			if ( plateau[x][y].getChessmen() == null ) {
				moves.add(plateau[x][y]);
			} else if ( plateau[x][y].getChessmen().getColor() != c.getChessmen().getColor() ) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][y]);
				break;
			} else if ( plateau[x][y].getChessmen().getColor() == c.getChessmen().getColor() ) {
				break;
			}
		}

		for ( x = c.getX() + 1, y = c.getY() - 1; x < SIZE && y >= 0; x++, y-- ) {
			if ( plateau[x][y].getChessmen() == null ) {
				moves.add(plateau[x][y]);
			} else if ( plateau[x][y].getChessmen().getColor() != c.getChessmen().getColor() ) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][y]);
				break;
			} else if ( plateau[x][y].getChessmen().getColor() == c.getChessmen().getColor() ) {
				break;
			}
		}

		for ( x = c.getX() + 1, y = c.getY() + 1; x < SIZE && y < SIZE; x++, y++ ) {
			if ( plateau[x][y].getChessmen() == null ) {
				moves.add(plateau[x][y]);
			} else if ( plateau[x][y].getChessmen().getColor() != c.getChessmen().getColor() ) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][y]);
				break;
			} else if ( plateau[x][y].getChessmen().getColor() == c.getChessmen().getColor() ) {
				break;
			}
		}
		return moves;
	}

}
