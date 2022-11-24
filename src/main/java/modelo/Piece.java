package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
	
	private int color;
	private int fil;
	private int col;
	private int type;
	private String rep;
	private List<Move> moves;
	
	Piece(int color, int fil, int col, String white, String black, int type){
		this.color = color;
		this.fil = fil;
		this.col = col;
		this.type = type;
		this.rep = (color == 0) ? white : black;
		this.moves = new ArrayList<Move>();
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getFil() {
		return fil;
	}

	public void setFil(int fil) {
		this.fil = fil;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getType() {
		return type;
	}

	public int getPos() {
		return fil + col * 8;
	}

	public void setPos(int pos) {
		fil = pos % 8;
		col = (int) pos/8;
	}
	
	public void notifyMove(int newFil, int newCol) {
		this.col = newCol;
		this.fil = newFil;
	}
	
	public void notifyMove(int newPos) {
		this.col = (int)newPos/8;
		this.fil = newPos%8;
	}
	
	@Override
	public String toString() {
		return rep;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public void updateMoves(Board b) {
		this.moves = MoveGenerator.generateMoves(b, this);
	}
	
}
