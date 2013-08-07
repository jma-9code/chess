package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class Rook extends Chessmen {
	// la piece a deja bougé ?

	private static final Logger logger = Logger.getLogger(Rook.class);

	// la piece a deja bougee ?
	private boolean moved = false;

	public Rook(EColor _color) {
		color = _color;
	}

	@Override
	public Chessmen clone() {
		return new Rook(color);
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException {
		if (c == null) {
			throw new SquareException("La case est nulle");
		}
		if (c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE) {
			throw new SquareException("Cette case n'est pas sur l'echequier");
		}
		// On admet que deplacements est init a null
		int x, y;
		ArrayList<Square> moves = new ArrayList<Square>();
		/* Vertical */
		// vers 0
		for (x = c.getX() - 1; x >= 0; x--) {
			if (plateau[x][c.getY()].getChessmen() == null) {
				moves.add(plateau[x][c.getY()]);
			}
			else if (plateau[x][c.getY()].getChessmen().getColor() != c.getChessmen().getColor()) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][c.getY()]);
				break;
			}
			else if (plateau[x][c.getY()].getChessmen().getColor() == c.getChessmen().getColor()) {
				break;
			}
		}
		// vers TAILLE
		for (x = c.getX() + 1; x < SIZE; x++) {
			if (plateau[x][c.getY()].getChessmen() == null) {
				moves.add(plateau[x][c.getY()]);
			}
			else if (plateau[x][c.getY()].getChessmen().getColor() != c.getChessmen().getColor()) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[x][c.getY()]);
				break;
			}
			else if (plateau[x][c.getY()].getChessmen().getColor() == c.getChessmen().getColor()) {
				break;
			}
		}

		/* Horizontale */
		// vers 0
		for (y = c.getY() - 1; y >= 0; y--) {
			if (plateau[c.getX()][y].getChessmen() == null) {
				moves.add(plateau[c.getX()][y]);
			}
			else if (plateau[c.getX()][y].getChessmen().getColor() != c.getChessmen().getColor()) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[c.getX()][y]);
				break;
			}
			else if (plateau[c.getX()][y].getChessmen().getColor() == c.getChessmen().getColor()) {
				break;
			}
		}
		// vers TAILLE
		for (y = c.getY() + 1; y < SIZE; y++) {
			if (plateau[c.getX()][y].getChessmen() == null) {
				moves.add(plateau[c.getX()][y]);
			}
			else if (plateau[c.getX()][y].getChessmen().getColor() != c.getChessmen().getColor()) {
				// On sort car la tour ne peut pas passer au dessus des piece
				moves.add(plateau[c.getX()][y]);
				break;
			}
			else if (plateau[c.getX()][y].getChessmen().getColor() == c.getChessmen().getColor()) {
				break;
			}
		}

		return moves;
	}

	public boolean isMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

}
