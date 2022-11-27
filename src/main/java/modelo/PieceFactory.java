package modelo;

public class PieceFactory {
	
	public static Piece createPiece(char type, int color, int fil, int col) {
		if (type == 'p') {
			return new Pawn(color, fil, col);
		} else if (type == 'r') {
			return new Rook(color, fil, col);
		} else if (type == 'k') {
			return new King(color, fil, col);
		} else if (type == 'n') {
			return new Knight(color, fil, col);
		} else if (type == 'b') {
			return new Bishop(color, fil, col);
		} else if (type == 'q') {
			return new Queen(color, fil, col);
		}
		return null;
	}
	
	public static Piece createPiece(char type, int color, int pos) {
		return createPiece(type, color, pos%8, (int)pos/8);
	}
}
