package controlador;

import java.util.ArrayList;
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
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Move> all_moves = b.getAllMoves(color);
		List<Move> eat_moves = tryEatMove(all_moves);
		
		Move randomMove = null;
		
		if (b.in_check) {
			List<Move> scape_from_check = tryScapeFromCheck();
			if (!scape_from_check.isEmpty()) {
				return selectRandomMove(scape_from_check);
			}
		}
		
		if(!eat_moves.isEmpty()) {
			randomMove = selectRandomMove(eat_moves);
		} else {
			randomMove = selectRandomMove(all_moves);
		}
		
		return randomMove;
	}
	
	private List<Move> tryEatMove(List<Move> all_moves) {
		List<Move> eatMoves = new ArrayList<Move>();
		for (Move m: all_moves) {
			if (b.getPiece(m.getPos_to()) != null 
					&& b.getPiece(m.getPos_to()).getType() != 2) { //para que no empieze a comer peones
				eatMoves.add(m);
			}
		}
		return eatMoves;
		
	}

	private List<Move> tryScapeFromCheck() {
		var my_king_pos = (color == 0) ? b.whiteKingPos : b.blackKingPos;
		var my_king = b.getPiece(my_king_pos);
		return my_king.getMoves();
	}
	
	private Move selectRandomMove(List<Move> list) {
		Move randomMove = list.get(randomNumberGenerator.nextInt(list.size()));
		return randomMove;
	}

	@Override
	public void setBoard(Board b) {
		this.b = b;
	}

}
