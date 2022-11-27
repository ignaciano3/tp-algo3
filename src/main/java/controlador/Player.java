package controlador;

import modelo.Board;
import modelo.Move;

public interface Player {
	
	Move choseMove();
	
	void setBoard(Board b);
}
