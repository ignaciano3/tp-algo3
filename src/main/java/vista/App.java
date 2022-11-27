package vista;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import modelo.Board;

public class App extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		//String fen = "8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8 b - - 99 50";
		Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
		var scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Chess");
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}