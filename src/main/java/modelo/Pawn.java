package modelo;

public class Pawn extends Piece {
	
	private Boolean first_move = true;
	private int dir = (getColor() == 0) ? 1 : -1;
	
	Pawn(int color, int fil, int col) {
		super(color, fil, col, "♙", "♟︎", 2);
	}

	@Override
	public void notifyMove(int newFil, int newCol) {
		super.notifyMove(newFil, newCol);
		setFirst_move(false);
	}

	public Boolean getFirst_move() {
		return first_move;
	}

	public void setFirst_move(Boolean first_move) {
		this.first_move = first_move;
	}

	public int getDir() {
		return dir;
	}
	

}
