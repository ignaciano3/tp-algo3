package controlador;

import modelo.Board;
import modelo.Move;

public class HumanPlayer implements Player {

	private int color;
	private Board b;
	
	public HumanPlayer(int color) {
		this.color = color;
	}

	@Override
	public Move choseMove() {
		return null;
	}

	@Override
	public void setBoard(Board b) {
		this.b = b;
		
	}

}
