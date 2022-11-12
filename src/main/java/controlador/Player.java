package controlador;

import modelo.Move;

public interface Player {
	
	// deberia hacer un evento en caso de que se eliga movimiento
	Move choseMove(); //elegir movimiento
	
	void update(); //para actualizar movimientos disponibles
	
}
