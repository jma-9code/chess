package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import main.Config;
import message.party.Stroke;
import model.Chessboard;
import model.Party;
import model.Square;
import model.chessmens.Chessmen;
import model.exceptions.SquareException;

import commun.EColor;
import commun.Player;

public class PGNReader {
	private static String patInfoScore = ".*1-0.*|.*0-1.*|.*1/2-1/2.*";
	private static String patSmallRoque = "O-O";
	private static String patBigRoque = "O-O-O";

	public static void readStrokes(Party p, String lines) {
		//Retire les tags
		String strokes = lines.substring(lines.lastIndexOf("]") + 1);
		//Retire le score
		int index;
		if ( (index = strokes.lastIndexOf(" ")) != -1 ) {
			strokes = strokes.substring(0, strokes.lastIndexOf(" "));
		} else {
			return;
		}

		EColor color;
		int count = 0;
		String[] statements = strokes.split(" ");
		boolean promotion = false;
		for ( String stroke : statements ) {
			promotion = false;
			Class chessmenPromotion = null;
			if ( count % 2 == 0 ) {
				color = EColor.WHITE;
			} else {
				color = EColor.BLACK;
			}

			//retire le numero du coup
			if ( stroke.contains(".") ) {
				stroke = stroke.substring(stroke.indexOf(".") + 1);
			}
			//Promotion de piece
			if ( stroke.contains("=") ) {
				promotion = true;
				int ind =  stroke.indexOf("=");
				chessmenPromotion = Chessmen.getChessmenClassByChar(stroke.substring(ind));
				stroke = stroke.substring(0, ind);
			}
			//Retire les informations superflues
			if ( stroke.matches(".*x.*|.*+.*|.*#.*|.*!.*|.*?.*") ) {
				stroke = stroke.replaceAll("[x+#!?]", "");
			}
			// Recuperation du type de piece
			Class chessmenClass = Chessmen.getChessmenClassByChar(stroke);
			//Retire le nom des pieces, laisse le rocque
			stroke = stroke.replaceAll("[A-Z&&[^O]]", "");
			//Necessaire pr lever l'ambiguiter
			int info = -1;
			boolean columns = true;

			Square dst = null;
			Square src = null;
			if ( stroke.matches(patSmallRoque) ) {
				if ( color == EColor.BLACK ) {
					dst = p.getPlateau().getBoard()[0][6];
				} else {
					dst = p.getPlateau().getBoard()[Config.BOARD_SIZE - 1][6];
				}
				src = p.getPlateau().getKingbyColor(p.getPlateau().getBoard(), color);
			} else if ( stroke.matches(patBigRoque) ) {
				if ( color == EColor.BLACK ) {
					dst = p.getPlateau().getBoard()[0][2];
				} else {
					dst = p.getPlateau().getBoard()[Config.BOARD_SIZE - 1][2];
				}
				src = p.getPlateau().getKingbyColor(p.getPlateau().getBoard(), color);
			} else {
				dst = p.getPlateau().getBoard()[8 - Integer.parseInt("" + stroke.charAt(stroke.length() - 1))][replaceCharToInt(stroke
						.charAt(stroke.length() - 2))];
				if ( Character.isDigit(stroke.charAt(0)) ) {
					info = 8 - Integer.parseInt("" + stroke.charAt(0));
					columns = false;
				} else {
					info = (stroke.length() == 3) ? replaceCharToInt(stroke.charAt(0)) : -1;
				}
			}

			if ( src == null ) {
				src = findSrcWithDst(chessmenClass, color, dst, p.getPlateau(), info, columns);
			}
			try {
				Stroke strokePlayed = p.getPlateau().playStroke(src, dst);
				if (promotion){
					p.getPlateau().havePromotion(strokePlayed, Chessmen.createChessmen(chessmenPromotion, color));
				}
			} catch (Exception e) {
				System.out.println("Mvt : " + count + " " + stroke + " -> " + e.getMessage());
				return;
			}
			count++;
		}
	}

	public static void readTag(Party p, String lines) {
		//Garde que les tags
		String tags = lines.substring(0, lines.lastIndexOf("]"));
		tags = tags.replace("[", "");
		String[] tagsList = tags.split("]");
		Player playerW = new Player("White", 1000, EColor.WHITE);
		Player playerB = new Player("Black", 1000, EColor.BLACK);
		String name, elo;
		for ( String tag : tagsList ) {
			if ( tag.matches("^White .*") ) {
				name = tag.substring(tag.indexOf("\"") + 1, tag.lastIndexOf("\""));
				playerW.setName(name);
			} else if ( tag.matches("^Black .*") ) {
				name = tag.substring(tag.indexOf("\"") + 1, tag.lastIndexOf("\""));
				playerB.setName(name);
			} else if ( tag.matches("^WhiteElo.*") ) {
				elo = tag.substring(tag.indexOf("\"") + 1, tag.lastIndexOf("\""));
				try {
					playerW.setScore(Integer.parseInt(elo));
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if ( tag.matches("^BlackElo.*") ) {
				elo = tag.substring(tag.indexOf("\"") + 1, tag.lastIndexOf("\""));
				try {
					playerB.setScore(Integer.parseInt(elo));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		p.addPlayer(playerW);
		p.addPlayer(playerB);
	}

	public static ArrayList<String> splitFile(String file) {
		ArrayList<String> partys = new ArrayList<String>();
		try {
			File f = new File(file);
			FileReader fr = new FileReader(f);
			BufferedReader brd = new BufferedReader(fr);
			boolean findParty = false;
			StringBuilder str = new StringBuilder();
			while ( brd.ready() ) {
				String line = brd.readLine();
				if ( line.matches(patInfoScore) && !line.contains("Result") ) {
					findParty = false;
					str.append(line);
					partys.add(str.toString());
					str.delete(0, str.length());
				} else if ( line.contains("[") || findParty ) {
					findParty = true;
					if ( line.trim().length() > 0 && !line.contains("[") ) {
						switch ( line.charAt(line.length() - 1) ) {
							case ' ':
							case '.':
							break;
							default:
								line += " ";
							break;
						}
						str.append(line);
					} else {
						str.append(line);
					}
				}
			}
			fr.close();
			brd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partys;
	}

	public static Party convertPGN(String file) {
		//Retourne uniquement la premiere partie definie dans le fichier
		Party party = null;
		ArrayList<String> partysString = splitFile(file);
		party = new Party(0, true);
		readTag(party, partysString.get(0));
		readStrokes(party, partysString.get(0));
		return party;
	}

	public static int replaceCharToInt(char car) {
		car = Character.toUpperCase(car);
		switch ( car ) {
			case 'A':
				return 0;
			case 'B':
				return 1;
			case 'C':
				return 2;
			case 'D':
				return 3;
			case 'E':
				return 4;
			case 'F':
				return 5;
			case 'G':
				return 6;
			case 'H':
				return 7;
			default:
				return 0;
		}
	}



	public static Square findSrcWithDst(Class chessmen, EColor color, Square dst, Chessboard chessboard, int infoSupp,
			boolean columns) {
		Square src = null;
		Square[][] board = chessboard.getBoard();
		for ( int i = 0; i < board.length; i++ ) {
			for ( int j = 0; j < board[0].length; j++ ) {
				if ( infoSupp != -1 && ((columns && j != infoSupp) || (!columns && i != infoSupp)) ) {
					continue;
				}
				Chessmen chmn = board[i][j].getChessmen();
				if ( chmn != null && chmn.getColor() == color && chmn.getClass() == chessmen ) {
					ArrayList<Square> possibilities;
					try {
						possibilities = Chessboard.filterMovePossibilities(board, board[i][j],
								chmn.movePossibilities(board, board[i][j]));
						if ( possibilities.contains(dst) ) { return board[i][j]; }
					} catch (SquareException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return src;
	}
}
