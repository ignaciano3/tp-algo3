package vista;

import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.stage.Stage;
import modelo.Board;

public class App extends Application {

	@Override
	public void start(Stage stage) throws FileNotFoundException {
		//String fen = "8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8 b - - 99 50";
		Board b = new Board();
		Vista v = new Vista(stage, b);
	}

	public static void main(String[] args) {
		launch(args);
	}

}