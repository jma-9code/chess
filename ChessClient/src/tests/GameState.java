package tests;

import junit.framework.Assert;
import message.party.StateOfParty;
import model.Party;
import model.Square;
import model.chessmens.King;
import model.chessmens.Queen;
import model.chessmens.Rook;
import model.exceptions.RulesException;
import model.exceptions.SquareException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commun.EColor;
import commun.Player;

public class GameState {
	private Party p = null;
	private Square[][] board = null;

	@Before
	public void avantTests() {
		p = new Party();
		p.addPlayer(new Player("TestWhite", 1000, EColor.WHITE));
		p.addPlayer(new Player("TestBlack", 1000, EColor.BLACK));
		p.getPlateau().clearBoard();
		board = p.getPlateau().getBoard();
	}

	@After
	public void apresTests() {
		p = null;
		board = null;
	}

	public static void waiting(int n) {
		long t0, t1;

		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		}
		while ((t1 - t0) < (n * 1000));
	}

	private void checkTestBoard() {
		board[0][0].setChessmen(new Rook(EColor.WHITE));
		board[1][7].setChessmen(new King(EColor.BLACK));
		p.getPlateau().setBoard(board);
	}

	private void checkMateTestBoard() {
		board[2][3].setChessmen(new King(EColor.WHITE));
		board[0][3].setChessmen(new King(EColor.BLACK));
		board[1][6].setChessmen(new Rook(EColor.WHITE));
		p.getPlateau().setBoard(board);
	}

	private void patTestBoard() {
		board[0][0].setChessmen(new King(EColor.BLACK));
		board[3][1].setChessmen(new Queen(EColor.WHITE));
		board[3][2].setChessmen(new King(EColor.WHITE));
		p.getPlateau().setBoard(board);
	}

	@Test
	public void checkState() throws RulesException, SquareException {
		checkTestBoard();
		p.getPlateau().setCaseSelect(board[0][0]);
		p.getPlateau().playStroke(board[0][0], board[1][0]);
		Assert.assertEquals(true, p.getPlateau().isBlackIsEchec());
	}

	@Test
	public void checkMateState() throws RulesException, SquareException {
		checkMateTestBoard();
		p.getPlateau().setCaseSelect(board[1][6]);
		p.getPlateau().playStroke(board[1][6], board[0][6]);
		Assert.assertEquals(StateOfParty.FINISHED, p.getStatut());
	}

	@Test
	public void patState() throws RulesException, SquareException {
		patTestBoard();
		p.getPlateau().setCaseSelect(board[3][1]);
		p.getPlateau().playStroke(board[3][1], board[2][1]);
		Assert.assertEquals(StateOfParty.FINISHED, p.getStatut());
	}

	@Test
	public void outOfTime() {
		p.getPlateau().setTimeWhite(1);
		waiting(2);
		Assert.assertEquals(StateOfParty.FINISHED, p.getStatut());
	}
}
