package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import modelo.Move.Flag;

class TestBoard {

	@Test
	void createStandardBoard() {
		Board b = new Board();
		assertNotNull(b);
		
		// Checkeo algunas piezas pero no todas porque si no me pego un tiro
		assertTrue(b.getPiece(1,1) instanceof Pawn, "es peon");
		assertTrue(b.getPiece(3,0) instanceof Queen, "es reina");
		assertTrue(b.getPiece(4,0) instanceof King, "es rey");
		assertTrue(b.getPiece(0,7) instanceof Rook, "es torre");
		assertTrue(b.getPiece(1,7) instanceof Knight, "es caballo");
		assertTrue(b.getPiece(2,7) instanceof Bishop, "es alfil");
		
		// Empieza el blanco
		assertEquals(b.colorToMove, 0);
		
		// Enroques
		assertEquals(b.getCastling(), 0b1111);
		
		// En_passant
		assertFalse(b.getEn_passant());
	}
	
	/** drawn position 
	 * para ver mejor irse a: https://www.chess.com/analysis
	........
	.....♚..
	...♟︎....
	.♟︎.♙♟︎..♟︎
	♟︎♙..♙♟︎.♙
	♙....♙.♔
	........
	........
	 */
	@Test
	void createCustomBoard() {
		String drawn_position = "8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8 b - - 99 50";
		Board b = new Board(drawn_position);
		
		//Checkeo algunas piezas
		assertTrue(b.getPiece(0,2) instanceof Pawn);
		assertEquals(b.getPiece(0,2).getColor(), 0);
		assertTrue(b.getPiece(7,2) instanceof King);
		assertEquals(b.getPiece(7,2).getColor(), 0);
		assertTrue(b.getPiece(5,6) instanceof King);
		assertEquals(b.getPiece(5,6).getColor(), 1);
		
		// Empieza el negro
		assertEquals(b.colorToMove, 1);
		
		// No se puede enrocar
		assertEquals(b.getCastling(), 0b0000);
	}
	
	@Test
	void movimientosValidos() {
		
		Board b = new Board();
		
		b.makeMove(new Move (1,1, 1,3)); //b4
		b.makeMove(new Move (2,6, 2,4)); //c5
		
		b.makeMove(new Move (1,3, 2,4)); //bxc5
		b.makeMove(new Move (6,7, 5,5)); //♞f6
		
		assertTrue(b.getPiece(2,4) instanceof Pawn);
		assertEquals(b.getPiece(2,4).getColor(), 0);
		assertTrue(b.getPiece(5,5) instanceof Knight);
	}
	
	/** Una previsualizacion del tablero de movimientos inválidos
	♜.♝♛..♞♜
	♟︎♟︎♟︎♟︎♟︎♟︎♟︎♟︎
	..♞.♚♝..
	........
	........
	.♘♗♕♔♘..
	.♙♙♙♙♙♙♙
	♖..♗...♖
	
	**/
	@Test
	void movimientosInvalidos() {
		Board b = new Board("r1bq2nr/pppppppp/2n1kb2/8/8/1NBQKN2/1PPPPPPP/R2B3R w - - 1 1");
		
		assertFalse(b.makeMove(new Move (1,1, 0,1)), "peon en horizontal");
		assertFalse(b.makeMove(new Move (1,1, 0,2)), "peon come aire");
		assertFalse(b.makeMove(new Move (1,1, 2,2)), "peon come mismo color");
		assertFalse(b.makeMove(new Move (1,1, 1,3)), "peon atravieza el caballo"); //este me lo habia comido
		
		assertFalse(b.makeMove(new Move (0,0, 5,0)), "torre atravieza alfil");
		
		assertFalse(b.makeMove(new Move (1,2, 3,1)), "caballo come mismo color");
		assertFalse(b.makeMove(new Move (1,2, 1,3)), "caballo se mueve mal");
		
		assertFalse(b.makeMove(new Move (2,2, 2,3)), "alfil en distinto color");
		assertFalse(b.makeMove(new Move (2,2, -1,-1)), "out of bounds"); //no va a encontrar nunca un movimiento asi
		assertThrows(IndexOutOfBoundsException.class, () -> b.getPiece(70), "out of bounds"); 
		
		assertFalse(b.makeMove(new Move(4,5, 5,5)), "rey choca con mismo color");
		// quedan por ver el del rey que es mucho más complicado
		// si consideramos los pseudo-legales (jaques)
	}
	
	/** Este lo voy a usar para checkear en_passant y enroques
	♜...♚..♜
	♟︎♟︎♟︎♟︎♟︎♟︎.♟︎
	........
	.♙......
	......♟︎.
	........
	♙.♙♙♙♙♙♙
	♖...♔..♖
	 */
	@Test
	void en_passant() {
		Board b = new Board("r3k2r/pppppp1p/8/1P6/6p1/8/P1PPPPPP/R3K2R w KQkq - 0 1");
		
		b.makeMove(new Move(7,1, 7,3));
		assertTrue(b.getEn_passant());
		assertTrue(b.makeMove(new Move (6,3, 7,2)), "en passant negro");
		
		b.makeMove(new Move (0,1, 0,2));
		b.makeMove(new Move (2,6, 2,4));
		
		assertTrue(b.makeMove(new Move (1,4, 2,5)), "en passant blanco");
		b.makeMove(new Move (1,6, 1,4));
		
		assertFalse(b.makeMove(new Move (2,5, 1,5)), "no se puede en_passant");
		assertFalse(b.makeMove(new Move (2,5, 1,6)), "no se puede en_passant");
		
	}
	
	@Test
	void enroqueKingSide() {
		Board b = new Board("r3k2r/pppppp1p/8/1P6/6p1/8/P1PPPPPP/R3K2R w KQkq - 0 1");
		
		// Caso normal
		assertTrue(b.makeMove(new Move(4,0, 6,0)), "enroque blanco");
		assertTrue(b.makeMove(new Move(4,7, 6,7)), "enroque negro");
		
		// Torre se puede mover despues de enrocar
		assertTrue(b.makeMove(new Move(5,0, 1,0)), "muevo torre blanca");
		assertTrue(b.makeMove(new Move(5,7, 1,7)), "muevo torre blanca");
		
		// Caso torre movida
		b = new Board("r3k2r/pppppp1p/8/1P6/6p1/8/P1PPPPPP/R3K2R w KQkq - 0 1");
		b.makeMove(new Move(7,0, 5,0));
		b.makeMove(new Move(7,7, 5,7));
		
		assertFalse(b.makeMove(new Move(4,0, 6,0)), "no se puede enroque blanco movida");
		assertFalse(b.makeMove(new Move(4,7, 6,7)), "no se puede enroque negro movida");
		
		// Caso vuelvo a poner en donde estaba
		b.makeMove(new Move(5,0, 7,0));
		b.makeMove(new Move(5,7, 7,7));
		
		assertFalse(b.makeMove(new Move(4,0, 6,0)), "no se puede enroque blanco vuelta");
		assertFalse(b.makeMove(new Move(4,7, 6,7)), "no se puede enroque negro vuelta");
		
	}
	
	@Test
	void enroqueQueenSide() {
		Board b = new Board("r3k2r/pppppp1p/8/1P6/6p1/8/P1PPPPPP/R3K2R w KQkq - 0 1");
		
		assertTrue(b.makeMove(new Move(4,0, 2,0)), "enroque blanco");
		assertTrue(b.makeMove(new Move(4,7, 2,7)), "enroque negro");
		
		assertFalse(b.makeMove(new Move(2,0, 3,0)), "verifico que torre este");
		assertFalse(b.makeMove(new Move(2,7, 3,7)), "verifico que torre este");
		
		// Torre se puede mover despues de enrocar
		assertTrue(b.makeMove(new Move(3,0, 5,0)), "muevo torre blanca");
		assertTrue(b.makeMove(new Move(3,7, 5,7)), "muevo torre negra");
		
	}
	
	/**
	.....♞..
	♙♙♙♙♙♙♙♙
	........
	♔.......
	.......♚
	........
	♟︎♟︎♟︎♟︎♟︎♟︎♟︎♟︎
	.....♗..
	 */
	@Test 
	void promocionesDePeon(){
		Board b = new Board("5n2/PPPPPPPP/8/K7/7k/8/pppppppp/5B2 w - - 0 1");
		
		assertTrue(b.makeMove(new Move(1,6, 1,7)), "promociona blanco reina");
		
		b.setPreferedPromote('n');
		assertTrue(b.makeMove(new Move(0,1, 0,0)), "promociona negro caballo");
		
		assertTrue(b.makeMove(new Move(1,7, 5,7)), "reina come otro caballo");
		assertTrue(b.makeMove(new Move(0,0, 1,2)), "negro hace movimiento de caballo");
		
	}
	
	/** ...♚♛...
		........
		........
		........
		........
		........
		........
		..♕♔....
	 */
	@Test
	void jaques() {
		Board b = new Board("3kq3/8/8/8/8/8/8/2QK4 w - - 0 1");
		
		assertTrue(b.makeMove(new Move(2,0, 3,1)), "dama hace jaque");
		assertTrue(b.in_check, "esta en jaque 1");
		
		assertTrue(b.makeMove(new Move(3,7, 2,7)), "rey se mueve");
		assertFalse(b.in_check, "no esta en jaque");
		
		assertTrue(b.makeMove(new Move(3,1, 4,1)), "movimiento espera");
		assertTrue(b.makeMove(new Move(4,7, 3,6)), "dama negra hace jaque");
		assertTrue(b.in_check, "esta en jaque 2");
		
		assertTrue(b.makeMove(new Move(4,1, 4,0)), "movimiento deberia haber tapado el jaque");
		assertTrue(b.in_check, "esta en jaque 3");
		assertEquals(b.gameStatus, 1, "game over");
	}
	
	@Test
	void draw() {
		Board b = new Board("3kq3/8/8/8/8/8/8/2QK4 w - - 99 198");
		b.makeMove(new Move(2,0, 2,1)); //movimiento para llegar a los 100
		
		b.makeMove(new Move(3,7, 2,7)); //movimiento para que se active el gameStatus
		assertEquals(b.gameStatus, 3);
		
		
	}
	
}
