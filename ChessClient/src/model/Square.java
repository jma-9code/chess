package model;

import java.io.Serializable;

import model.chessmens.Chessmen;

import org.apache.log4j.Logger;

/**
 * Typiquement, d�fini une case sur un plateau de jeu. Une case est un
 * conteneur, il contient une pi�ce ou rien.
 */
public class Square implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(Square.class);
	
	private int x;
	private int y;
	private Chessmen chessmen;
	

	public Square ( int x, int y, Chessmen chessmen ) {
		super();
		this.x = x;
		this.y = y;
		this.chessmen = chessmen;
	}

	public Square clone() {
		return new Square(x, y, chessmen.clone());
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Chessmen getChessmen() {
		return chessmen;
	}

	public void setChessmen(Chessmen chessmen) {
		this.chessmen = chessmen;
	}

	@Override
	public String toString() {
		return "Case [x=" + x + ", y=" + y + ", piece=" + chessmen + "]";
	}
}
