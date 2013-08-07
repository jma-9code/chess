package message.party;

import message.IMsgChess;


public class Stroke implements IMsgChess {

	private static final long serialVersionUID = -7750284292254340818L;
	private int sourceX;
	private int sourceY;
	private int destinationX;
	private int destinationY;

	/**
	 * Permet de savoir si la piece qui se retrouve sur dst, a été promu.
	 */
	private char dstIsPromotedTo = ' ';
	
	
	public Stroke (int sourceX, int sourceY, int destinationX, int destinationY) {
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.destinationX = destinationX;
		this.destinationY = destinationY;		
	}
	

	public int getSourceX() {
		return sourceX;
	}

	public void setSourceX(int sourceX) {
		this.sourceX = sourceX;
	}

	public int getSourceY() {
		return sourceY;
	}

	public void setSourceY(int sourceY) {
		this.sourceY = sourceY;
	}

	public int getDestinationX() {
		return destinationX;
	}

	public void setDestinationX(int destinationX) {
		this.destinationX = destinationX;
	}

	public int getDestinationY() {
		return destinationY;
	}

	public void setDestinationY(int destinationY) {
		this.destinationY = destinationY;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "(" + sourceX + ", " + sourceY + ") -> (" + destinationX + ", " + destinationY + ")";
	}


	public char getDstIsPromotedTo() {
		return dstIsPromotedTo;
	}


	public void setDstIsPromotedTo(char dstIsPromotedTo) {
		this.dstIsPromotedTo = dstIsPromotedTo;
	}
}