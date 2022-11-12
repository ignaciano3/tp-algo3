package modelo;

import java.util.ArrayList;
import java.util.List;

import modelo.Move.Flag;

public class Board {

	private Piece[] square = new Piece[64];
	public String fen;

	public int colorToMove;
	private int castling;
	
	private int whiteCastleKingSideMask = 1 << 3;
	private int whiteCastleQueenSideMask = 1 << 2;
	private int blackCastleKingSideMask = 1 << 1;
	private int blackCastleQueenSideMask = 1 << 0;
	
	private int whiteCastle = whiteCastleKingSideMask | whiteCastleQueenSideMask;
	private int blackCastle = blackCastleKingSideMask | blackCastleQueenSideMask;
	
	public Boolean en_passant;
	private int en_passant_pos; // pos to enpassant
	public int half_moves; // to ensure draw
	public int full_moves; // que lastima que no existe un readonly

	public Board() {
		fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		LoadPositionFromFen(fen);
		updateAllMoves();
	}

	public Board(String fen) {
		LoadPositionFromFen(fen);
		updateAllMoves();
	}
	
	public Boolean makeMove(Move move) {
		int pos_from = move.getPos_from();
		int pos_to = move.getPos_to();
		Piece pieceFrom = square[pos_from];
		
		if (pieceFrom == null || pieceFrom.getColor() != colorToMove) {
			return false;
		}
		
		for (Move x : pieceFrom.getMoves()) {
			
			if (x.equals(move)) {
				
				// DISABLES CASTLING
				if (colorToMove == 0 && ((castling & whiteCastle) == whiteCastle) ||
					colorToMove == 1 && ((castling & blackCastle) == blackCastle))
					disableCastling(pieceFrom);
				
				// CASTLE KINGSIDE
				if (x.getFlag() == Flag.CASTLING_KINGSIDE) {
					Piece rook = square[pos_to+1];
					square[pos_to-1] = rook;
					square[pos_to+1] = null;
				}
				
				// CASTLE QUEENSIDE
				if (x.getFlag() == Flag.CASTLING_QUEENSIDE) {
					Piece rook = square[pos_to-2];
					square[pos_to+1] = rook;
					square[pos_to-2] = null;
				}
				
				// PAWN TWO FORWARD
				if (x.getFlag() == Flag.PAWN_TWO_FORWARD) {
					en_passant_pos = pos_to + (pos_from - pos_to)/2;
					en_passant = true;
				} else {
					en_passant = false;
				}
				
				// EN PASSANT
				if (x.getFlag() == Flag.EN_PASSANT) {
					int dir = (colorToMove == 0) ? -1 : 1;
					int remove_pawn = pos_to + 8 * dir;
					square[remove_pawn] = null;
					en_passant = false;
				}
				
				// ALL
				pieceFrom.notifyMove(move.getX_to(), move.getY_to());
				square[pos_from] = null;
				square[pos_to] = pieceFrom;
				colorToMove ^= 1;
				
				updateAllMoves();
				return true;
			}
		}
		return false; // debería guardar los movimientos después
	}

	public Boolean getEn_passant() {
		return en_passant;
	}

	public int getEn_passant_pos() {
		return en_passant_pos;
	}

	public void updateAllMoves() {
		for (Piece p: square) {
			if (p == null) continue;
			p.updateMoves(this);
		}
	}
	
	// Aclaro, los tests mas importantes del ajedrez checkean
	// que tengas los 100000 y pico movimientos definidos
	// para una determinada profundidad
	// https://www.chessprogramming.org/Perft_Results
	public List<Move> getAllMoves() {
		List<Move> allMoves = new ArrayList<Move>();
		for (Piece p: square) {
			if (p == null) continue;
			
			for (Move m : p.getMoves()) {
				allMoves.add(m);
			}
		}
		return allMoves; 
	}
	
	public Piece getPiece(int pos) {
		if (pos >= 0 && pos < 64) {
			return square[pos];
		}
		throw new IndexOutOfBoundsException();
	}
	
	public Piece getPiece(int fil, int col) {
		return getPiece(fil+col*8);
	}

	private void LoadPositionFromFen(String fen) {
		String[] sections = fen.split(" ");
		LoadPieces(sections[0].toCharArray());
		colorToMove = LoadMove(sections[1]);
		setCastling(LoadCastling(sections[2]));
		en_passant_pos = LoadEnPassant(sections[3]);
		en_passant = (en_passant_pos != -1) ? true : false;
		half_moves = LoadHalfMoves(sections[4]);
		full_moves = LoadFullMoves(sections[5]);
	}

	private void LoadPieces(char[] pieces_fen) {
		int y = 7;
		int x = 0;

		for (Character actual : pieces_fen) {
			if (Character.isDigit(actual)) {
				x += Character.getNumericValue(actual);
			} else if (actual.equals('/')) {
				x=0;
				y--;
			} else {
				char type = Character.toLowerCase(actual);
				int color = (Character.isUpperCase(actual)) ? 0 : 1;
				square[y*8+x] = PieceFactory.createPiece(type, color, x, y);
				x++;
			}
		}

	}

	private int LoadMove(String move) {
		if (move.equals("w")) {
			return 0;
		}
		return 1;
	}

	private int LoadCastling(String castling) {
		int cast = 0;
		if (castling.contains("K")) {
			cast += whiteCastleKingSideMask;
		}
		if (castling.contains("Q")) {
			cast += whiteCastleQueenSideMask;
		}
		if (castling.contains("k")) {
			cast += blackCastleKingSideMask;
		}
		if (castling.contains("q")) {
			cast += blackCastleQueenSideMask;
		}
		return cast;
	}

	private int LoadEnPassant(String en_passant) {
		if (en_passant.equals("-")) {
			return -1;
		}
		var en_passant_pos = en_passant.toCharArray();
		return ((int)en_passant_pos[0] - 97)*8 + en_passant_pos[1];
	}

	private int LoadHalfMoves(String half_moves) {
		return Integer.parseInt(half_moves);
	}

	private int LoadFullMoves(String full_moves) {
		return Integer.parseInt(full_moves);
	}

	@Override
	public String toString() {
		// for debugging
		String boardRep = "";

		for (int y = 7; y>=0; y--) {
			for (int x =0; x<8; x++) {
				Piece piece = square[y*8+x];
				if (piece != null) {
					boardRep += piece.toString();
				} else {
					boardRep += ".";
				}
			}
			boardRep += "\n";
		}
		return boardRep;
	}
	
	public String toStringBlack() {
		// for debugging
		String boardRep = "";

		int x = 0;
		for (Piece piece : square) {
			if (x == 8) {
				boardRep += "\n";
				x = 0;
			}
			if (piece != null) {
				boardRep += piece.toString();
			} else {
				boardRep += ".";
			}
			x++;
		}
		boardRep += "\n";
		
		return boardRep;
	}

	public int getCastling() {
		return castling;
	}

	public void setCastling(int castling) {
		this.castling = castling;
	}
	
	public void disableCastling(Piece pieceFrom) {
		int pos_from = pieceFrom.getPos();
		int castlingMask = 0;
		
		if(pieceFrom instanceof King)
			castlingMask = (colorToMove == 0) ? 0b1100 : 0b0011;
		
		else if(pieceFrom instanceof Rook) {
			switch (pos_from) {
				case 7:
					castlingMask = whiteCastleKingSideMask;
					break;
				case 1:
					castlingMask = whiteCastleQueenSideMask;
					break;
				case 63:
					castlingMask = blackCastleKingSideMask;
					break;
				case 56:
					castlingMask = blackCastleQueenSideMask;
					break;
				}
			}
		
		castling &= ~castlingMask;
		}
	
}
