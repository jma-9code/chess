package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class King extends Chessmen {

	private static final Logger logger = Logger.getLogger(King.class);
	private boolean moved = false;

	public King ( EColor _color ) {
		color = _color;
	}

	@Override
	public Chessmen clone() {
		return new King(color);
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException{
		if (c == null) throw new SquareException("La case est nulle");
		if (c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE) throw new SquareException("Cette case n'est pas sur l'echequier");
		ArrayList<Square> moves = new ArrayList<Square>();
		if ( c.getX() > 0 && (plateau[c.getX() - 1][c.getY()].getChessmen() == null || plateau[c.getX() - 1][c.getY()].getChessmen().getColor() != c.getChessmen().getColor()) ) {
			// On verifie que le roi nest pas en echec a la pos x,y
			moves.add(plateau[c.getX() - 1][c.getY()]);
		}

		if ( c.getX() < SIZE - 1 && (plateau[c.getX() + 1][c.getY()].getChessmen() == null || plateau[c.getX() + 1][c.getY()].getChessmen().getColor() != c.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX() + 1][c.getY()]);
		}

		if ( c.getY() > 0 && (plateau[c.getX()][c.getY() - 1].getChessmen() == null || plateau[c.getX()][c.getY() - 1].getChessmen().getColor() != c.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX()][c.getY() - 1]);
		}

		if ( c.getY() < SIZE - 1 && (plateau[c.getX()][c.getY() + 1].getChessmen() == null || plateau[c.getX()][c.getY() + 1].getChessmen().getColor() != c.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX()][c.getY() + 1]);
		}

		if ( c.getX() > 0 && c.getY() < SIZE - 1 && (plateau[c.getX() - 1][c.getY() + 1].getChessmen() == null || plateau[c.getX() - 1][c.getY() + 1].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() - 1][c.getY() + 1]);
		}

		if ( c.getX() < SIZE - 1 && c.getY() > 0 && (plateau[c.getX() + 1][c.getY() - 1].getChessmen() == null || plateau[c.getX() + 1][c.getY() - 1].getChessmen().getColor() != c.getChessmen()
				.getColor()) ) {
			moves.add(plateau[c.getX() + 1][c.getY() - 1]);
		}

		if ( c.getX() > 0 && c.getY() > 0 && (plateau[c.getX() - 1][c.getY() - 1].getChessmen() == null || plateau[c.getX() - 1][c.getY() - 1].getChessmen().getColor() != c.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX() - 1][c.getY() - 1]);
		}

		if ( c.getX() < SIZE - 1 && c.getY() < SIZE - 1 && (plateau[c.getX() + 1][c.getY() + 1].getChessmen() == null || plateau[c.getX() + 1][c.getY() + 1].getChessmen().getColor() != c
				.getChessmen().getColor()) ) {
			moves.add(plateau[c.getX() + 1][c.getY() + 1]);
		}

		// Roque ?
		if ( !moved ) {
			// Recherche des attaques possible de l'adversaire
			ArrayList<Square> attackMoves = new ArrayList<Square>();
			for ( int i = 0; i < SIZE; i++ ) {
				for ( int j = 0; j < SIZE; j++ ) {
					if ( plateau[i][j].getChessmen() != null && plateau[i][j].getChessmen().getColor() != color && !(plateau[i][j].getChessmen() instanceof King) ) {
						attackMoves.addAll(plateau[i][j].getChessmen().movePossibilities(plateau, plateau[i][j]));
					}
				}
			}

			// Si le roi est en echec...pas de roque
			if ( !attackMoves.contains(c) ) {
				int offset = SIZE - 1;
				if ( color == EColor.BLACK ) {
					offset = 0;
				}
				// Grand roque
				if ( plateau[offset][0].getChessmen() != null && plateau[offset][0].getChessmen() instanceof Rook && !((Rook) plateau[offset][0].getChessmen()).isMoved() ) {
					boolean test = true;
					for ( int i = 1; i < c.getY(); i++ ) {
						if ( plateau[offset][i].getChessmen() != null || attackMoves.contains(plateau[offset][i]) ) {
							test = false;
							break;
						}
					}
					if ( test ) moves.add(plateau[offset][2]);
				}

				// petit roque
				if ( plateau[offset][SIZE - 1].getChessmen() != null && plateau[offset][SIZE - 1].getChessmen() instanceof Rook && !((Rook) plateau[offset][SIZE - 1].getChessmen()).isMoved() ) {
					boolean test = true;
					for ( int i = c.getY() + 1; i < SIZE - 1; i++ ) {
						if ( plateau[offset][i].getChessmen() != null || attackMoves.contains(plateau[offset][i]) ) {
							test = false;
							break;
						}
					}
					if ( test ) moves.add(plateau[offset][SIZE - 2]);
				}
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
