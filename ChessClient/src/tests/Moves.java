package tests;

import model.Party;
import model.Square;
import model.chessmens.King;
import model.chessmens.Pawn;
import model.chessmens.Rook;
import model.exceptions.RulesException;
import model.exceptions.SquareException;

import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import commun.EColor;
import commun.Player;

public class Moves {
	private Party p = null;
	private Square[][] board = null;
	
	private void plateauRoque(EColor color) {
		if(color == EColor.BLACK) {
			board[0][4].setChessmen(new King(EColor.BLACK));
			board[0][7].setChessmen(new Rook(EColor.BLACK));
			board[0][0].setChessmen(new Rook(EColor.BLACK));
			p.getPlateau().setWhoColorPlay(EColor.BLACK);
		} else if (color == EColor.WHITE) {
			board[7][4].setChessmen(new King(EColor.WHITE));
			board[7][7].setChessmen(new Rook(EColor.WHITE));
			board[7][0].setChessmen(new Rook(EColor.WHITE));
		}
		p.getPlateau().setBoard(board);
	}
	
	private void playBlackPetitRoque() throws RulesException, SquareException {
		Square src = board[0][4];
		Square dst = board[0][6];
		p.getPlateau().setCaseSelect(src);
		p.getPlateau().playStroke(src, dst);
		board = p.getPlateau().getBoard();
	}
	
	private void playWhitePetitRoque() throws RulesException, SquareException {
		Square src = board[7][4];
		Square dst = board[7][6];
		p.getPlateau().setCaseSelect(src);
		p.getPlateau().playStroke(src, dst);
		board = p.getPlateau().getBoard();
	}
	
	private void playBlackGrandRoque() throws RulesException, SquareException {
		Square src = board[0][4];
		Square dst = board[0][2];
		p.getPlateau().setCaseSelect(src);
		p.getPlateau().playStroke(src, dst);
		board = p.getPlateau().getBoard();
	}
	
	private void playWhiteGrandRoque() throws RulesException, SquareException {
		Square src = board[7][4];
		Square dst = board[7][2];
		p.getPlateau().setCaseSelect(src);
		p.getPlateau().playStroke(src, dst);
		board = p.getPlateau().getBoard();
	}
	
	@Before
    public void avantTests() {
		p = new Party();
		p.addPlayer(new Player("TestWhite",1000,EColor.WHITE));
		p.addPlayer(new Player("TestBlack",1000,EColor.BLACK));
		p.getPlateau().clearBoard();
		board = p.getPlateau().getBoard();
    }
    
    @After
    public void apresTests() {
    	p = null;
    	board = null;
    }
	
	@Test(expected = SquareException.class)
    public void wrongSourceMove() throws RulesException, SquareException {
		Square src = new Square(99,99,new Pawn(EColor.WHITE));
		Square dst = board[3][4];
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test(expected=RulesException.class)
    public void wrongDestinationMove() throws RulesException, SquareException {
		Square src = board[6][0];
		src.setChessmen(new Pawn(EColor.WHITE));
		Square dst = new Square(99,99,null);
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test(expected=RulesException.class)
    public void wrongPlayerMove() throws RulesException, SquareException {
		Square src = board[1][0];
		src.setChessmen(new Pawn(EColor.BLACK));
		Square dst = board[3][0];
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test(expected=SquareException.class)
    public void nullSourceMove() throws RulesException, SquareException {
		Square src = null;
		Square dst = board[0][0];
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test(expected=SquareException.class)
    public void nullDestinationMove() throws RulesException, SquareException {
		Square src = board[0][0];
		Square dst = null;
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test(expected=SquareException.class)
    public void noChessmanOnSource() throws RulesException, SquareException {
		Square src = board[0][0];
		Square dst = board[0][1];
		p.getPlateau().playStroke(src,dst);
    }
	
	@Test
	public void blackPetitRoque() throws RulesException, SquareException {
		plateauRoque(EColor.BLACK);
		playBlackPetitRoque();
		Assert.assertThat(board[0][5].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
		Assert.assertThat(board[0][6].getChessmen(), IsInstanceOf.instanceOf(King.class));
	}
	
	@Test
	public void whitePetitRoque() throws RulesException, SquareException {
		plateauRoque(EColor.WHITE);
		playWhitePetitRoque();
		Assert.assertThat(board[7][5].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
		Assert.assertThat(board[7][6].getChessmen(), IsInstanceOf.instanceOf(King.class));
	}
	
	@Test
	public void blackGrandRoque() throws RulesException, SquareException {
		plateauRoque(EColor.BLACK);
		playBlackGrandRoque();
		Assert.assertThat(board[0][3].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
		Assert.assertThat(board[0][2].getChessmen(), IsInstanceOf.instanceOf(King.class));
	}
	
	@Test
	public void whiteGrandRoque() throws RulesException, SquareException {
		plateauRoque(EColor.WHITE);
		playWhiteGrandRoque();
		Assert.assertThat(board[7][3].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
		Assert.assertThat(board[7][2].getChessmen(), IsInstanceOf.instanceOf(King.class));
	}
}
