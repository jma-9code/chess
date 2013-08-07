package tests;

import model.Party;
import model.Square;
import model.chessmens.Bishop;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;

import org.hamcrest.core.IsInstanceOf;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import commun.EColor;

public class Chessboard {
	private static Party p = null;
	private static Square[][] board = null;
	
	@BeforeClass
    public static void avantTests() {
		p = new Party();
		board = p.getPlateau().getBoard();
    }
    
    @AfterClass
    public static void apresTests() {
    	p = null;
    	board = null;
    }
    
    @Test
    public void blackRookPosition() {
    	Assert.assertThat(board[0][0].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
    	Assert.assertThat(board[0][7].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
    }
    
    @Test
    public void blackKnightPosition() {
    	Assert.assertThat(board[0][1].getChessmen(), IsInstanceOf.instanceOf(Knight.class));
    	Assert.assertThat(board[0][6].getChessmen(), IsInstanceOf.instanceOf(Knight.class));
    }
    
    @Test
    public void blackBishopPosition() {
    	Assert.assertThat(board[0][2].getChessmen(), IsInstanceOf.instanceOf(Bishop.class));
    	Assert.assertThat(board[0][5].getChessmen(), IsInstanceOf.instanceOf(Bishop.class));
    }
    
    @Test
    public void blackQueenPosition() {
    	Assert.assertThat(board[0][3].getChessmen(), IsInstanceOf.instanceOf(Queen.class));
    }
    
    @Test
    public void blackKingPosition() {
    	Assert.assertThat(board[0][4].getChessmen(), IsInstanceOf.instanceOf(King.class));
    }
    
    @Test
    public void blackPawnPosition() {
    	for (int i = 0; i < 8; i++){
    		Assert.assertThat(board[1][i].getChessmen(), IsInstanceOf.instanceOf(Pawn.class));
    	}
    }
    
    @Test
    public void blackRookColor() {
    	Assert.assertEquals(EColor.BLACK, board[0][0].getChessmen().getColor());
    	Assert.assertEquals(EColor.BLACK, board[0][7].getChessmen().getColor());
    }
    
    @Test
    public void blackKnightColor() {
    	Assert.assertEquals(EColor.BLACK, board[0][1].getChessmen().getColor());
    	Assert.assertEquals(EColor.BLACK, board[0][6].getChessmen().getColor());
    }
    
    @Test
    public void blackBishopColor() {
    	Assert.assertEquals(EColor.BLACK, board[0][2].getChessmen().getColor());
    	Assert.assertEquals(EColor.BLACK, board[0][5].getChessmen().getColor());
    }
    
    @Test
    public void blackQueenColor() {
    	Assert.assertEquals(EColor.BLACK, board[0][3].getChessmen().getColor());
    }
    
    @Test
    public void blackKingColor() {
    	Assert.assertEquals(EColor.BLACK, board[0][4].getChessmen().getColor());
    }
    
    @Test
    public void blackPawnColor() {
    	for (int i = 0; i < 8; i++){
    		Assert.assertEquals(EColor.BLACK, board[1][i].getChessmen().getColor());
    	}
    }

    @Test
    public void whiteRookPosition() {
    	Assert.assertThat(board[7][0].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
    	Assert.assertThat(board[7][7].getChessmen(), IsInstanceOf.instanceOf(Rook.class));
    }
    
    @Test
    public void whiteKnightPosition() {
    	Assert.assertThat(board[7][1].getChessmen(), IsInstanceOf.instanceOf(Knight.class));
    	Assert.assertThat(board[7][6].getChessmen(), IsInstanceOf.instanceOf(Knight.class));
    }
    
    @Test
    public void whiteBishopPosition() {
    	Assert.assertThat(board[7][2].getChessmen(), IsInstanceOf.instanceOf(Bishop.class));
    	Assert.assertThat(board[7][5].getChessmen(), IsInstanceOf.instanceOf(Bishop.class));
    }
    
    @Test
    public void whiteQueenPosition() {
    	Assert.assertThat(board[7][3].getChessmen(), IsInstanceOf.instanceOf(Queen.class));
    }
    
    @Test
    public void whiteKingPosition() {
    	Assert.assertThat(board[7][4].getChessmen(), IsInstanceOf.instanceOf(King.class));
    }
    
    @Test
    public void whitePawnPosition() {
    	for (int i = 0; i < 8; i++){
    		Assert.assertThat(board[6][i].getChessmen(), IsInstanceOf.instanceOf(Pawn.class));
    	}
    }
    
    @Test
    public void whiteRookColor() {
    	Assert.assertEquals(EColor.WHITE, board[7][0].getChessmen().getColor());
    	Assert.assertEquals(EColor.WHITE, board[7][7].getChessmen().getColor());
    }
    
    @Test
    public void whiteKnightColor() {
    	Assert.assertEquals(EColor.WHITE, board[7][1].getChessmen().getColor());
    	Assert.assertEquals(EColor.WHITE, board[7][6].getChessmen().getColor());
    }
    
    @Test
    public void whiteBishopColor() {
    	Assert.assertEquals(EColor.WHITE, board[7][2].getChessmen().getColor());
    	Assert.assertEquals(EColor.WHITE, board[7][5].getChessmen().getColor());
    }
    
    @Test
    public void whiteQueenColor() {
    	Assert.assertEquals(EColor.WHITE, board[7][3].getChessmen().getColor());
    }
    
    @Test
    public void whiteKingColor() {
    	Assert.assertEquals(EColor.WHITE, board[7][4].getChessmen().getColor());
    }
    
    @Test
    public void whitePawnColor() {
    	for (int i = 0; i < 8; i++){
    		Assert.assertEquals(EColor.WHITE, board[6][i].getChessmen().getColor());
    	}
    }
}
