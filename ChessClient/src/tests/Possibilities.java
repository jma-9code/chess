package tests;

import model.Party;
import model.Square;

import org.junit.After;
import org.junit.Before;

import commun.EColor;
import commun.Player;


public class Possibilities {
	private Party p = null;
	private Square[][] board = null;
	
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

}
