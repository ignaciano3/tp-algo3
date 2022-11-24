package controlador;

import java.util.List;
import java.util.Random;

import modelo.Board;
import modelo.Move;

public class AIPlayer implements Player {
	
	private int color;
	private Board b;
	private Random randomNumberGenerator;
	
	public AIPlayer(int color){
		this.color = color;
		randomNumberGenerator = new Random();
	}
	
	@Override
	public Move choseMove() {
		List<Move> all_moves = b.getAllMoves(color);
	    Move randomMove = all_moves.get(randomNumberGenerator.nextInt(all_moves.size()));
		return randomMove;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setBoard(Board b) {
		this.b = b;
	}

}
