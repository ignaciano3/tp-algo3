package modelo;

import java.util.List;

import modelo.Move.Flag;

public record MoveUpdate(
		List<Move> lastMoves,
		Flag flag,
		Boolean inCheck,
		int gameStatus) {
	
	
}
