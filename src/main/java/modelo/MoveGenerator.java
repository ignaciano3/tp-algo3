package modelo;

import java.util.ArrayList;
import java.util.List;

import modelo.Move.Flag;

public class MoveGenerator {
	static int[] directionOffsets = new int[] {8, -8, -1, 1, 7, -7, 9, -9};
	static int[][] numSquaresToEdge = PrecomputeMoveData();
	
	static int[][] PrecomputeMoveData() {
		int[][] numSquaresToEdge = new int[64][64];
		
		for (int fila = 0; fila < 8; fila ++) {
			for (int col = 0; col < 8; col ++) {
				
				int numArriba = 7 - col;
				int numAbajo = col;
				int numIzq = fila;
				int numDer = 7 - fila;
				int pos = fila + col*8;
				
				numSquaresToEdge[pos] = new int[] {
						numArriba,
						numAbajo,
						numIzq,
						numDer,
						Math.min(numArriba, numIzq),
						Math.min(numAbajo, numDer),
						Math.min(numArriba, numDer),
						Math.min(numAbajo, numIzq)
				};
				
			}
		}
		return numSquaresToEdge;
	}
	
	static List<Move> generateMoves(Board b, Piece p){
		List<Move> moves;
		switch (p.getType()) {
			case 1:
				moves = generateKingMoves(b, (King) p);
				break;
			case 2:
				moves = generatePawnMoves(b, (Pawn) p);
				break;
			case 3:
				moves = generateKnightMoves(b, p);
				break;
			default: //queen, bishop, rook
				moves = generateSlidingMoves(b, p);
		}
		
		return moves;
	}

	static List<Move> generateSlidingMoves(Board b, Piece p) {
		List<Move> moves = new ArrayList<Move>();
		int posPiece = p.getPos();
		int type = p.getType();
		int color = p.getColor();
		
		int startDirIndex = (type == 4) ? 4 : 0; // Restricts bishop
		int endDirIndex = (type == 5) ? 4 : 8; // Restricts rook
		
		for (int dirIndex = startDirIndex; dirIndex < endDirIndex; dirIndex++) {
			for (int n = 0; n < numSquaresToEdge[posPiece][dirIndex]; n++) {
				
				int targetPiecePos = posPiece + directionOffsets[dirIndex] * (n+1);
				Piece targetPiece = b.getPiece(targetPiecePos);
				
				if (targetPiece == null) {
					moves.add(new Move(posPiece, targetPiecePos));
				} else if (targetPiece.getColor() == color) {
					break; // Esta bloqueada por pieza del mismo color
				} else {
					moves.add(new Move(posPiece, targetPiecePos));
					break; // Esta bloqueada por pieza del otro color
				}
			}
		}
		return moves;
	}
	
	static public List<Move> generatePawnMoves(Board b, Pawn p) {
		List<Move> moves = new ArrayList<Move>();
		Boolean first_move = p.getFirst_move();
		int dir = p.getDir();
		int pos = p.getPos();
		int color = p.getColor();
		
		// Normal moves
		if (b.getPiece(pos+8*dir) == null ) {
			var forward = new Move(pos, pos+8*dir);
			moves.add(forward);
			if (first_move && b.getPiece(pos+8*2*dir) == null) {
				var two_forward = new Move(pos, pos+8*2*dir);
				two_forward.setFlag(Flag.PAWN_TWO_FORWARD);
				moves.add(two_forward);
				
			}
		}
		
		// Take moves
		for (int i : new int[] {7*dir,9*dir}) {
			// Normal
			if (b.getPiece(pos+i) != null && 
					b.getPiece(pos+i).getColor() != color) {
				var take = new Move(pos, pos+i);
				moves.add(take);
			}
			
			// En passant
			if (!b.getEn_passant()) continue;
			
			if (b.getPiece(pos+i) == null &&
					b.getEn_passant_pos() == pos+i) {
				var en_passant = new Move(pos, pos+i);
				en_passant.setFlag(Flag.EN_PASSANT);
				moves.add(en_passant);
			}
			
		}
		return moves;
	}
	
	static public List<Move> generateKnightMoves(Board b, Piece p){
		List<Move> moves = new ArrayList<Move>();
		int pos = p.getPos();
		int color = p.getColor();
		
		int[] allKnightJumps = { 15, 17, -17, -15, 10, -6, 6, -10 };
		
		for (int i : allKnightJumps) {
			if (pos+i < 0 || pos+i > 64) {
				continue; // out of bounds
			}
			if (b.getPiece(pos + i) == null || b.getPiece(pos + i).getColor() != color) {
				moves.add(new Move(pos, pos+i));
			}
		}
		return moves;
	}
	
	private static List<Move> generateKingMoves(Board b, King p) {
		List<Move> moves = new ArrayList<Move>();
		int pos = p.getPos();
		int color = p.getColor();
		for (int i : directionOffsets) {
			if (pos+i < 0 || pos+i >64) {
				continue;
			}
			if (b.getPiece(pos+i) == null || b.getPiece(pos+i).getColor() != color) {
				//hay que checkear que no suiciden al rey
				moves.add(new Move (pos, pos+i));
			}
		}
		
		// Castle kingside
		int kingsideMask = (color == 0) ? (1 << 3) : (1 << 1);
		if ((b.getCastling() & kingsideMask) == kingsideMask &&
				b.getPiece(pos+1) == null &&
				b.getPiece(pos+2) == null &&
				b.getPiece(pos+3) instanceof Rook
				) {
			var castlingKingside = new Move(pos, pos+2);
			castlingKingside.setFlag(Flag.CASTLING_KINGSIDE);
			moves.add(castlingKingside);
		}
		
		// Castle queenside
		int queensideMask = (color == 0) ? (1 << 2) : (1 << 0);;
		if (	(b.getCastling() & queensideMask) == queensideMask &&
				b.getPiece(pos-1) == null &&
				b.getPiece(pos-2) == null &&
				b.getPiece(pos-3) == null &&
				b.getPiece(pos-4) instanceof Rook
				) {
			var castlingQueenside = new Move(pos, pos-2);
			castlingQueenside.setFlag(Flag.CASTLING_QUEENSIDE);
			moves.add(castlingQueenside);
			
		}
		
		// También tengo que ver el tema de los enroques
		return moves;
	}
	
}
