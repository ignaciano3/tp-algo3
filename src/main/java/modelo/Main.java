package modelo;

public class Main {

	public static void main(String[] args) {
		
		Board b = new Board("r3k2r/pppppp1p/8/1P6/6p1/8/P1PPPPPP/R3K2R w KQkq - 0 1");
		
		//b.makeMove(new Move(4,0, 6,0)); // enroque blanco kingside
		//b.makeMove(new Move (4,7, 6,7)); // enroque negro kingside
		
		b.makeMove(new Move(4,0, 2,0)); // enroque blanco queenside
		b.makeMove(new Move(4,7, 2,7)); // enroque negro queenside
		System.out.print(b.toString());
		
		/**
		 * Me falta:
		 * - Concepto de jaque, jaque mate
		 * - Victoria derrota tablas
		 * - Arreglar vista
		 * - Arreglar todos los movimientos pseudolegales
		 * - Que de alguna manera pueda exportar info desde board
		 * - hacer strategy humano / bot
		 * - agregar promociones de peon
		 */
	}

}
