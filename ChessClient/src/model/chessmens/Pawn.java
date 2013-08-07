package model.chessmens;

import java.util.ArrayList;

import main.Config;
import model.Square;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;

public class Pawn extends Chessmen {

	private static final Logger logger = Logger.getLogger(Pawn.class);

	private boolean hasMove2Square = false;

	public Pawn ( EColor _color ) {
		color = _color;
	}

	@Override
	public ArrayList<Square> movePossibilities(Square[][] plateau, Square c) throws SquareException {
		if ( c == null )
			throw new SquareException("La case est nulle");
		if ( c.getX() >= Config.BOARD_SIZE || c.getY() >= Config.BOARD_SIZE )
			throw new SquareException("Cette case n'est pas sur l'echequier");
		ArrayList<Square> moves = new ArrayList<Square>();
		/* Les blanche commence a TAILLE-2 et vont vers 0 */
		if ( c.getChessmen().getColor() == EColor.WHITE ) {

			/* Deplacement de 1 seulement */
			if ( c.getX() > 0 && plateau[c.getX() - 1][c.getY()].getChessmen() == null ) {
				moves.add(plateau[c.getX() - 1][c.getY()]);
			}

			// Le pion est sur sa position de depart, decalage de 2 possibles
			if ( c.getX() == SIZE - 2 && plateau[c.getX() - 2][c.getY()].getChessmen() == null && plateau[c.getX() - 1][c
					.getY()].getChessmen() == null ) {
				moves.add(plateau[c.getX() - 2][c.getY()]);
			}

			if ( c.getY() > 0 && c.getX() > 0 ) {
				Chessmen chessmenDiag = plateau[c.getX() - 1][c.getY() - 1].getChessmen();
				Chessmen chessmenLeft = plateau[c.getX()][c.getY() - 1].getChessmen();
				// mange un pion sur la diagonale gauche
				if ( chessmenDiag != null && chessmenDiag.getColor() != EColor.WHITE ) {
					// il y a une piece adverse sur la case voulue
					moves.add(plateau[c.getX() - 1][c.getY() - 1]);
				} else if ( chessmenLeft != null && chessmenLeft instanceof Pawn && chessmenLeft.getColor() != EColor.WHITE && ((Pawn) chessmenLeft)
						.isHasMove2Square() ) {
					//en passant
					moves.add(plateau[c.getX() - 1][c.getY() - 1]);
				}
			}

			if ( c.getY() < SIZE - 1 && c.getX() > 0 ) {
				Chessmen chessmenDiag = plateau[c.getX() - 1][c.getY() + 1].getChessmen();
				Chessmen chessmenRight = plateau[c.getX()][c.getY() + 1].getChessmen();
				// mange un pion sur la diagonale droite
				if ( chessmenDiag != null && chessmenDiag.getColor() != EColor.WHITE ) {
					moves.add(plateau[c.getX() - 1][c.getY() + 1]);
				} else if ( chessmenRight != null && chessmenRight instanceof Pawn && chessmenRight.getColor() != EColor.WHITE && ((Pawn) chessmenRight)
						.isHasMove2Square() ) {
					//enPassant
					moves.add(plateau[c.getX() - 1][c.getY() + 1]);
				}
			}
		} else if ( c.getChessmen().getColor() == EColor.BLACK ) {
			/* Deplacement de 1 seulement */
			if ( c.getX() < SIZE - 1 && plateau[c.getX() + 1][c.getY()].getChessmen() == null ) {
				moves.add(plateau[c.getX() + 1][c.getY()]);
			}
			// Le pion est sur sa position de depart, decalage de 2 possibles
			if ( c.getX() == 1 && plateau[c.getX() + 2][c.getY()].getChessmen() == null && plateau[c.getX() + 1][c
					.getY()].getChessmen() == null ) {
				moves.add(plateau[c.getX() + 2][c.getY()]);
			}
			// mange un pion sur la diagonale gauche
			if ( c.getY() > 0 && c.getX() < SIZE - 1 ) {
				Chessmen chessmenDiag = plateau[c.getX() + 1][c.getY() - 1].getChessmen();
				Chessmen chessmenRight = plateau[c.getX()][c.getY() - 1].getChessmen();
				// mange un pion sur la diagonale droite
				if ( chessmenDiag != null && chessmenDiag.getColor() != EColor.BLACK ) {
					moves.add(plateau[c.getX() + 1][c.getY() - 1]);
				} else if ( chessmenRight != null && chessmenRight instanceof Pawn && chessmenRight.getColor() != EColor.BLACK && ((Pawn) chessmenRight)
						.isHasMove2Square() ) {
					//enPassant
					moves.add(plateau[c.getX() + 1][c.getY() - 1]);
				}
			}

			// mange un pion sur la diagonale droite
			if ( c.getY() < SIZE - 1 && c.getX() < SIZE - 1 ) {
				Chessmen chessmenDiag = plateau[c.getX() + 1][c.getY() + 1].getChessmen();
				Chessmen chessmenLeft = plateau[c.getX()][c.getY() + 1].getChessmen();
				// mange un pion sur la diagonale droite
				if ( chessmenDiag != null && chessmenDiag.getColor() != EColor.BLACK ) {
					moves.add(plateau[c.getX() + 1][c.getY() + 1]);
				} else if ( chessmenLeft != null && chessmenLeft instanceof Pawn && chessmenLeft.getColor() != EColor.BLACK && ((Pawn) chessmenLeft)
						.isHasMove2Square() ) {
					//enPassant
					moves.add(plateau[c.getX() + 1][c.getY() + 1]);
				}
			}
		}
		return moves;
	}

	@Override
	public Chessmen clone() {
		return new Pawn(color);
	}

	public boolean isHasMove2Square() {
		return hasMove2Square;
	}

	public void setHasMove2Square(boolean hasMove2Square) {
		this.hasMove2Square = hasMove2Square;
	}
}
