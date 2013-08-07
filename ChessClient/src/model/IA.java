package model;

import java.util.ArrayList;

import model.chessmens.Bishop;
import model.chessmens.Chessmen;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;
import model.exceptions.SquareException;

import org.apache.log4j.Logger;

import commun.EColor;
public class IA {

	private static final Logger logger = Logger.getLogger(IA.class);
	
	public static StrokeIA getIAStroke(Square[][] plateau, EColor color, int profondeur) throws SquareException{
		if ( profondeur < 0 ) return null;
		profondeur -= 1;

		Square[][] tmp = Chessboard.boardClone(plateau);
		int quality_max = Integer.MIN_VALUE;
		StrokeIA stroke = null;
		ArrayList<Square> p_cases = new ArrayList<Square>();
		// Identification de la liste des cases/iece du joueur
		for ( int i = 0; i < tmp.length; i++ ) {
			for ( int j = 0; j < tmp[0].length; j++ ) {
				if ( tmp[i][j].getChessmen() != null && tmp[i][j].getChessmen().getColor() == color ) {
					p_cases.add(tmp[i][j]);
				}
			}
		}

		// Recherche du meilleur coup pour le joueur
		for ( Square src : p_cases ) {
			ArrayList<Square> dst_pos = src.getChessmen().movePossibilities(tmp, src);
			for ( Square dst : dst_pos ) {
				int quality = 0;
				Square[][] tmp_dst = Chessboard.boardClone(tmp);
				// Simultation du Deplacement dans un tableau temporaire
				Chessmen take = Chessboard.move(tmp_dst, tmp_dst[src.getX()][src.getY()], tmp_dst[dst.getX()][dst.getY()]);

				if ( Chessboard.isEchec(tmp, (color == EColor.BLACK) ? EColor.WHITE : EColor.BLACK) ) {
					quality += 10;

					if ( Chessboard.isEchecMat(tmp, (color == EColor.BLACK) ? EColor.WHITE : EColor.BLACK) ) {
						quality += 100;
					}
				} else {
					/* On quantifie le poid de la piece prise */
					if ( take != null ) {
						if ( take instanceof Pawn ) {
							quality += 1;
						} else if ( take instanceof Rook ) {
							quality += 2;
						} else if ( take instanceof Knight ) {
							quality += 3;
						} else if ( take instanceof Bishop ) {
							quality += 4;
						} else if ( take instanceof Queen ) {
							quality += 5;
						} else if ( take instanceof King ) {
							quality += 6;
						}
					}
				}

				StrokeIA stroketmp = getIAStroke(tmp, (color == EColor.BLACK) ? EColor.WHITE : EColor.BLACK, profondeur);
				if ( stroketmp != null ) {
					quality += stroketmp.quality;
				}

				if ( quality > quality_max ) {
					quality_max = quality;
					stroke = new StrokeIA(quality, src.getX(), src.getY(), dst.getX(), dst.getY());
				}
			}
		}

		return stroke;
	}
}
